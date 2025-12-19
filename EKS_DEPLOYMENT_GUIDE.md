# AWS EKS Deployment Guide - Payment Echo System

## Prerequisites

- AWS CLI configured with appropriate credentials
- kubectl installed and configured
- eksctl installed (or use AWS Console)
- Docker installed
- Access to an ECR repository (or Docker Hub)

## Step 1: Build and Push Docker Image

### 1.1 Create Dockerfile

Create `Dockerfile` in project root:

```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 1.2 Build Docker Image

```bash
# Build image
docker build -t payment-echo-system:latest .

# Tag for ECR (replace ACCOUNT_ID and REGION)
docker tag payment-echo-system:latest ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/payment-echo-system:latest
```

### 1.3 Push to ECR

```bash
# Login to ECR
aws ecr get-login-password --region REGION | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com

# Create ECR repository (if not exists)
aws ecr create-repository --repository-name payment-echo-system --region REGION

# Push image
docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/payment-echo-system:latest
```

## Step 2: Create EKS Cluster

### 2.1 Using eksctl (Recommended)

```bash
# Create cluster with node group
eksctl create cluster \
  --name payment-echo-cluster \
  --region us-west-2 \
  --nodegroup-name payment-echo-nodes \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 4 \
  --managed
```

### 2.2 Using AWS Console

1. Go to EKS Console → Create Cluster
2. Configure:
   - Name: `payment-echo-cluster`
   - Kubernetes version: 1.28+
   - Service role: Create new or use existing
3. Configure networking (VPC, subnets)
4. Create cluster

### 2.3 Configure kubectl

```bash
# Update kubeconfig
aws eks update-kubeconfig --name payment-echo-cluster --region REGION

# Verify connection
kubectl get nodes
```

## Step 3: Create Kubernetes Manifests

### 3.1 Namespace

Create `k8s/namespace.yaml`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: payment-echo
```

### 3.2 ConfigMap

Create `k8s/configmap.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: payment-echo-config
  namespace: payment-echo
data:
  application.properties: |
    # Database Configuration
    spring.datasource.url=jdbc:h2:mem:paymentdb;DB_CLOSE_DELAY=-1
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

    # Actuator
    management.endpoints.web.exposure.include=health,info,metrics
    management.endpoint.health.show-details=always

    # Logging
    logging.level.com.example.paymentecho=INFO
    logging.level.org.springframework.web=INFO

    # Sample Data
    app.data.initialize=true
```

### 3.3 Deployment

Create `k8s/deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-echo-deployment
  namespace: payment-echo
spec:
  replicas: 2
  selector:
    matchLabels:
      app: payment-echo
  template:
    metadata:
      labels:
        app: payment-echo
    spec:
      containers:
        - name: payment-echo
          image: ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/payment-echo-system:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: JAVA_OPTS
              value: "-Xmx512m -Xms256m"
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
```

### 3.4 Service

Create `k8s/service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payment-echo-service
  namespace: payment-echo
spec:
  selector:
    app: payment-echo
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```

### 3.5 Ingress (Optional - for ALB)

Create `k8s/ingress.yaml`:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: payment-echo-ingress
  namespace: payment-echo
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
spec:
  rules:
    - host: payment-echo.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: payment-echo-service
                port:
                  number: 80
```

## Step 4: Deploy to EKS

### 4.1 Apply Manifests

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap
kubectl apply -f k8s/configmap.yaml

# Apply Deployment
kubectl apply -f k8s/deployment.yaml

# Apply Service
kubectl apply -f k8s/service.yaml

# Apply Ingress (if using)
kubectl apply -f k8s/ingress.yaml
```

### 4.2 Verify Deployment

```bash
# Check pods
kubectl get pods -n payment-echo

# Check services
kubectl get svc -n payment-echo

# Check deployment status
kubectl get deployment -n payment-echo

# View logs
kubectl logs -f deployment/payment-echo-deployment -n payment-echo
```

### 4.3 Get LoadBalancer URL

```bash
# Get external IP/URL
kubectl get svc payment-echo-service -n payment-echo

# Test health endpoint
curl http://EXTERNAL-IP/actuator/health
```

## Step 5: Database Configuration (Production)

### Option A: RDS PostgreSQL

1. Create RDS PostgreSQL instance
2. Update ConfigMap:

```yaml
spring.datasource.url=jdbc:postgresql://RDS_ENDPOINT:5432/paymentdb
spring.datasource.username=admin
spring.datasource.password=SECRET_PASSWORD
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

3. Create Secret:

```bash
kubectl create secret generic payment-echo-db-secret \
  --from-literal=username=admin \
  --from-literal=password=SECRET_PASSWORD \
  -n payment-echo
```

4. Update Deployment to use Secret:

```yaml
env:
  - name: SPRING_DATASOURCE_USERNAME
    valueFrom:
      secretKeyRef:
        name: payment-echo-db-secret
        key: username
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: payment-echo-db-secret
        key: password
```

### Option B: Persistent H2 Database

Create PersistentVolumeClaim:

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: payment-echo-pvc
  namespace: payment-echo
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
```

Update Deployment to mount PVC:

```yaml
volumeMounts:
  - name: data
    mountPath: /app/data
volumes:
  - name: data
    persistentVolumeClaim:
      claimName: payment-echo-pvc
```

## Step 6: Monitoring and Logging

### 6.1 CloudWatch Logs

Install CloudWatch Logs agent or use Fluent Bit:

```bash
# Install Fluent Bit
kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/fluent-bit/fluent-bit.yaml
```

### 6.2 Prometheus Metrics

Add Prometheus annotations to Service:

```yaml
metadata:
  annotations:
    prometheus.io/scrape: "true"
    prometheus.io/port: "8080"
    prometheus.io/path: "/actuator/prometheus"
```

## Step 7: CI/CD Pipeline

### 7.1 GitHub Actions Example

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to EKS

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-west-2

      - name: Login to ECR
        run: |
          aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.us-west-2.amazonaws.com

      - name: Build and push Docker image
        run: |
          docker build -t payment-echo-system:${{ github.sha }} .
          docker tag payment-echo-system:${{ github.sha }} ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.us-west-2.amazonaws.com/payment-echo-system:${{ github.sha }}
          docker push ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.us-west-2.amazonaws.com/payment-echo-system:${{ github.sha }}

      - name: Update kubeconfig
        run: |
          aws eks update-kubeconfig --name payment-echo-cluster --region us-west-2

      - name: Deploy to EKS
        run: |
          kubectl set image deployment/payment-echo-deployment payment-echo=${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.us-west-2.amazonaws.com/payment-echo-system:${{ github.sha }} -n payment-echo
          kubectl rollout status deployment/payment-echo-deployment -n payment-echo
```

## Step 8: Scaling

### 8.1 Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: payment-echo-hpa
  namespace: payment-echo
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: payment-echo-deployment
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

Apply:

```bash
kubectl apply -f k8s/hpa.yaml
```

### 8.2 Manual Scaling

```bash
# Scale to 5 replicas
kubectl scale deployment payment-echo-deployment --replicas=5 -n payment-echo
```

## Step 9: Troubleshooting

### Common Issues

1. **Pods not starting**

   ```bash
   kubectl describe pod <pod-name> -n payment-echo
   kubectl logs <pod-name> -n payment-echo
   ```

2. **Service not accessible**

   ```bash
   kubectl get svc -n payment-echo
   kubectl describe svc payment-echo-service -n payment-echo
   ```

3. **Image pull errors**

   ```bash
   # Check ECR permissions
   aws ecr describe-repositories --region REGION

   # Verify image exists
   aws ecr describe-images --repository-name payment-echo-system --region REGION
   ```

4. **Database connection issues**

   ```bash
   # Check ConfigMap
   kubectl get configmap payment-echo-config -n payment-echo -o yaml

   # Check Secrets
   kubectl get secrets -n payment-echo
   ```

## Step 10: Cleanup

```bash
# Delete resources
kubectl delete -f k8s/

# Delete cluster (if using eksctl)
eksctl delete cluster --name payment-echo-cluster --region REGION

# Delete ECR repository
aws ecr delete-repository --repository-name payment-echo-system --force --region REGION
```

## Security Best Practices

1. **Use Secrets for sensitive data** (passwords, API keys)
2. **Enable Pod Security Policies**
3. **Use Network Policies** to restrict traffic
4. **Enable encryption at rest** for EBS volumes
5. **Use IAM roles for service accounts** (IRSA)
6. **Regularly update base images**
7. **Scan images for vulnerabilities**

## Cost Optimization

1. **Use Spot Instances** for non-production workloads
2. **Right-size node groups** based on actual usage
3. **Enable cluster autoscaling**
4. **Use Reserved Instances** for predictable workloads
5. **Monitor CloudWatch costs**

## Additional Resources

- [EKS User Guide](https://docs.aws.amazon.com/eks/latest/userguide/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)

