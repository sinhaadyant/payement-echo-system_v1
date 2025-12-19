# Render Deployment Guide - Payment Echo System

Complete guide for deploying the Payment Echo System on Render's free tier.

## 📋 Prerequisites

- ✅ GitHub repository with your code
- ✅ Render account (sign up at https://render.com)
- ✅ Java 17 compatible application
- ✅ Build tool: Gradle (or Maven)

## 🎯 Quick Start

### Option 1: Using Gradle (Current Setup)

**Build Command:**
```bash
./gradlew clean build -x test
```

**Start Command:**
```bash
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

### Option 2: Using Maven

**Build Command:**
```bash
mvn clean package -DskipTests
```

**Start Command:**
```bash
java -jar target/payment-echo-system-0.0.1-SNAPSHOT.jar
```

## 🚀 Step-by-Step Deployment

### Step 1: Verify Local Build

**For Gradle:**
```bash
./gradlew clean build -x test
ls -lh build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

**For Maven:**
```bash
mvn clean package -DskipTests
ls -lh target/payment-echo-system-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
- JAR file should be created
- File size: ~50-80 MB
- Should be executable

### Step 2: Test JAR Locally

```bash
# Set PORT environment variable (Render requirement)
export PORT=8080

# Run the JAR
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
# OR for Maven:
# java -jar target/payment-echo-system-0.0.1-SNAPSHOT.jar

# In another terminal, test it:
curl http://localhost:8080/actuator/health
```

**Expected:** `{"status":"UP"}` or similar health check response

### Step 3: Push to GitHub

```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### Step 4: Create PostgreSQL Database on Render

1. Go to Render Dashboard: https://dashboard.render.com
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name**: `payment-echo-db`
   - **Database**: `payment_echo_db`
   - **User**: `paymentuser`
   - **Plan**: **Free**
   - **Region**: Choose closest to you
4. Click **"Create Database"**
5. **Save the connection details** (you'll need them)

### Step 5: Create Web Service on Render

1. Go to Render Dashboard
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository
4. Configure the service:

#### Basic Settings:
- **Name**: `payment-echo-system`
- **Region**: Same as database
- **Branch**: `main` (or your default branch)
- **Root Directory**: Leave empty (or `.` if needed)
- **Runtime**: `Java`
- **Build Command**: 
  - **Gradle**: `./gradlew clean build -x test`
  - **Maven**: `mvn clean package -DskipTests`
- **Start Command**: 
  - **Gradle**: `java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar`
  - **Maven**: `java -jar target/payment-echo-system-0.0.1-SNAPSHOT.jar`

#### Environment Variables:

Add these in the **Environment** section:

| Key | Value | Notes |
|-----|-------|-------|
| `JAVA_VERSION` | `17` | Java version |
| `PORT` | `8080` | Usually auto-set by Render |
| `SPRING_PROFILES_ACTIVE` | `production` | Activates production config |
| `DATABASE_URL` | `(from PostgreSQL service)` | Full JDBC URL |
| `DB_USERNAME` | `paymentuser` | Database username |
| `DB_PASSWORD` | `(from PostgreSQL service)` | Database password |

**How to get DATABASE_URL:**
- Go to your PostgreSQL service
- Click on **"Connections"** tab
- Copy the **"Internal Database URL"** or construct it:
  ```
  jdbc:postgresql://dpg-xxxxx-a/payment_echo_db
  ```

**Important:** Render provides `DATABASE_URL` automatically if you link the services, but you may need to set it manually.

### Step 6: Link Database to Web Service

1. In your Web Service settings
2. Go to **"Environment"** section
3. Click **"Link Resource"**
4. Select your PostgreSQL database
5. Render will automatically add `DATABASE_URL` environment variable

### Step 7: Deploy

1. Click **"Create Web Service"**
2. Render will:
   - Clone your repository
   - Run the build command
   - Start your application
3. Monitor the **"Logs"** tab for progress

### Step 8: Verify Deployment

Once deployment completes:

```bash
# Get your app URL (shown in Render dashboard)
curl https://your-app-name.onrender.com/actuator/health

# Test API
curl https://your-app-name.onrender.com/api/v1/payments

# Test Swagger UI (in browser)
https://your-app-name.onrender.com/swagger-ui.html
```

## 🔧 Configuration Details

### Port Configuration

The application is configured to read `PORT` from environment variables:

```properties
# application.properties
server.port=${PORT:8080}
```

This is **required** for Render - Render sets the `PORT` environment variable dynamically.

### Database Configuration

**Production Profile** (`application-production.properties`):
- Uses PostgreSQL (Render's free database)
- Reads connection from `DATABASE_URL` environment variable
- Optimized connection pool for free tier

**Local Development** (`application.properties`):
- Uses MySQL (for local development)
- Can be overridden with environment variables

### Environment Variables Summary

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PORT` | ✅ Yes | `8080` | Server port (set by Render) |
| `SPRING_PROFILES_ACTIVE` | ✅ Yes | - | Set to `production` |
| `DATABASE_URL` | ✅ Yes | - | PostgreSQL connection URL |
| `DB_USERNAME` | Optional | `paymentuser` | Database username |
| `DB_PASSWORD` | ✅ Yes | - | Database password |

## 📝 Render Configuration File (render.yaml)

If you prefer Infrastructure as Code, use the provided `render.yaml`:

```yaml
services:
  - type: web
    name: payment-echo-system
    runtime: java
    plan: free
    buildCommand: ./gradlew clean build -x test
    startCommand: java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: production
  - type: pspg
    name: payment-echo-db
    plan: free
    databaseName: payment_echo_db
```

**To use render.yaml:**
1. Commit `render.yaml` to your repository
2. In Render Dashboard, click **"New +"** → **"Blueprint"**
3. Connect your repository
4. Render will create services automatically

## ✅ Verification Checklist

Before deploying, verify:

- [ ] **Build works locally**
  ```bash
  ./gradlew clean build -x test  # or mvn clean package -DskipTests
  ```

- [ ] **JAR file is created**
  ```bash
  ls -lh build/libs/*.jar  # or target/*.jar for Maven
  ```

- [ ] **JAR runs locally**
  ```bash
  PORT=8080 java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
  ```

- [ ] **Health endpoint works**
  ```bash
  curl http://localhost:8080/actuator/health
  ```

- [ ] **Code is pushed to GitHub**
  ```bash
  git status
  git push origin main
  ```

- [ ] **Environment variables are set in Render**
  - `SPRING_PROFILES_ACTIVE=production`
  - `DATABASE_URL` (from PostgreSQL service)
  - `PORT` (auto-set by Render)

- [ ] **Database is created and accessible**

## 🐛 Common Issues & Solutions

### Issue 1: Build Fails - "Gradle wrapper not found"

**Solution:**
```bash
# Ensure gradlew is executable
chmod +x gradlew
git add gradlew gradlew.bat gradle/
git commit -m "Add Gradle wrapper"
git push
```

### Issue 2: Build Fails - "Maven not found"

**Solution:**
- Render supports Maven automatically
- Ensure `pom.xml` is in root directory
- Check build command: `mvn clean package -DskipTests`

### Issue 3: Application Won't Start - "Port already in use"

**Solution:**
- This shouldn't happen on Render
- Verify `server.port=${PORT:8080}` in `application.properties`
- Render sets `PORT` automatically

### Issue 4: Database Connection Failed

**Symptoms:**
```
Could not connect to database
```

**Solutions:**
1. **Check DATABASE_URL format:**
   ```
   jdbc:postgresql://host:port/database
   ```

2. **Verify database is running:**
   - Go to PostgreSQL service in Render
   - Check status is "Available"

3. **Check environment variables:**
   - `DATABASE_URL` is set correctly
   - `DB_PASSWORD` matches database password

4. **Verify database name:**
   - Should match `payment_echo_db`

5. **Check firewall/network:**
   - Use "Internal Database URL" for same region
   - External URL may have restrictions

### Issue 5: Application Crashes on Startup

**Check logs in Render Dashboard:**

1. Go to your Web Service
2. Click **"Logs"** tab
3. Look for error messages

**Common causes:**
- Missing environment variables
- Database connection issues
- Port configuration problems
- Memory issues (free tier has limits)

### Issue 6: "Out of Memory" Errors

**Solution:**
- Free tier has limited memory
- Optimize connection pool (already done in production config)
- Reduce logging verbosity
- Consider upgrading plan if needed

### Issue 7: Build Takes Too Long

**Solution:**
- Skip tests: `-x test` (Gradle) or `-DskipTests` (Maven)
- Use build cache if available
- Consider using Render's build cache

### Issue 8: JAR File Not Found

**Symptoms:**
```
Error: Unable to access jarfile build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

**Solutions:**
1. **Verify build command output:**
   - Check logs for actual JAR location
   - May be in different directory

2. **Check JAR name:**
   ```bash
   # After build, check actual filename
   find . -name "*.jar" -type f
   ```

3. **Update start command:**
   - Use actual JAR path from build output

### Issue 9: Health Check Fails

**Symptoms:**
- Render shows service as unhealthy
- Health endpoint returns error

**Solutions:**
1. **Verify health endpoint:**
   ```bash
   curl https://your-app.onrender.com/actuator/health
   ```

2. **Check health check path in Render:**
   - Should be: `/actuator/health`

3. **Verify actuator is enabled:**
   - Check `application-production.properties`
   - `management.endpoints.web.exposure.include=health,info`

### Issue 10: Application Starts But Returns 404

**Solutions:**
1. **Check base path:**
   - API endpoints: `/api/v1/...`
   - Health: `/actuator/health`

2. **Verify context path:**
   - Should be root `/` (no context path)

3. **Test endpoints:**
   ```bash
   curl https://your-app.onrender.com/actuator/health
   curl https://your-app.onrender.com/api/v1/payments
   ```

## 🔍 Debugging Tips

### View Logs

1. **In Render Dashboard:**
   - Go to Web Service
   - Click **"Logs"** tab
   - Filter by level (ERROR, WARN, INFO)

2. **Real-time logs:**
   ```bash
   # Use Render CLI (if installed)
   render logs -s payment-echo-system
   ```

### Test Database Connection

Add this to verify database connection:

```kotlin
// Temporary test endpoint
@GetMapping("/test-db")
fun testDb(): String {
    return try {
        val count = paymentRepository.count()
        "Database connected! Payments: $count"
    } catch (e: Exception) {
        "Database error: ${e.message}"
    }
}
```

### Enable Debug Logging

Temporarily in `application-production.properties`:

```properties
logging.level.com.example.paymentecho=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 📊 Free Tier Limitations

**Render Free Tier:**
- ✅ 750 hours/month (enough for 24/7)
- ✅ 512 MB RAM
- ✅ Shared CPU
- ⚠️ Spins down after 15 minutes of inactivity
- ⚠️ Cold start takes ~30-60 seconds

**PostgreSQL Free Tier:**
- ✅ 90 days free trial
- ✅ 1 GB storage
- ✅ Shared CPU
- ⚠️ Limited connections

**Optimizations Applied:**
- Reduced connection pool size (5 max)
- Disabled verbose logging
- Disabled DevTools
- Optimized for low memory usage

## 🎯 Final Commands Summary

### For Gradle:

**Build:**
```bash
./gradlew clean build -x test
```

**Start:**
```bash
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

### For Maven:

**Build:**
```bash
mvn clean package -DskipTests
```

**Start:**
```bash
java -jar target/payment-echo-system-0.0.1-SNAPSHOT.jar
```

## 📚 Additional Resources

- [Render Documentation](https://render.com/docs)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
- [PostgreSQL on Render](https://render.com/docs/databases)
- [Render Free Tier](https://render.com/docs/free)

## ✅ Post-Deployment Checklist

- [ ] Application is accessible via Render URL
- [ ] Health endpoint returns `200 OK`
- [ ] Database connection works
- [ ] API endpoints respond correctly
- [ ] Swagger UI is accessible
- [ ] Logs show no errors
- [ ] Environment variables are set correctly

---

**Need Help?** Check Render logs first, then refer to troubleshooting section above.

