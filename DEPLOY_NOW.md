# 🚀 Quick Deploy to Render - Step by Step

## ✅ Pre-Deployment Checklist

- [x] Build verified: `./gradlew clean build -x test` ✅ SUCCESS
- [x] JAR file created in `build/libs/`
- [x] PORT environment variable support added
- [x] PostgreSQL dependency added
- [x] Production profile created
- [x] render.yaml created
- [x] Maven pom.xml created (optional)

## 📋 Deployment Steps

### Step 1: Commit and Push to GitHub

```bash
# Add all new files
git add .

# Commit changes
git commit -m "Prepare for Render deployment - Add PORT support, PostgreSQL, production config"

# Push to GitHub
git push origin main
```

### Step 2: Create PostgreSQL Database on Render

1. **Go to Render Dashboard**: https://dashboard.render.com
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name**: `payment-echo-db`
   - **Database**: `payment_echo_db`
   - **User**: `paymentuser` (or leave default)
   - **Plan**: **Free**
   - **Region**: Choose closest (e.g., `Oregon (US West)`)
4. Click **"Create Database"**
5. **Wait for database to be ready** (green status)
6. **Copy the Internal Database URL** (you'll need it)

### Step 3: Create Web Service on Render

1. **Go to Render Dashboard**
2. Click **"New +"** → **"Web Service"**
3. **Connect GitHub** (if not already connected)
   - Authorize Render to access your GitHub
   - Select your repository: `payement-echo-system_v1`

4. **Configure Service:**

   **Basic Settings:**
   - **Name**: `payment-echo-system`
   - **Region**: Same as database (e.g., `Oregon (US West)`)
   - **Branch**: `main` (or your default branch)
   - **Root Directory**: Leave empty
   - **Runtime**: `Java`
   - **Build Command**: 
     ```
     ./gradlew clean build -x test
     ```
   - **Start Command**: 
     ```
     java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
     ```

   **Environment Variables:**
   Click **"Add Environment Variable"** and add:

   | Key | Value |
   |-----|-------|
   | `JAVA_VERSION` | `17` |
   | `SPRING_PROFILES_ACTIVE` | `production` |
   | `PORT` | `8080` (usually auto-set, but add for safety) |

5. **Link Database:**
   - Scroll down to **"Link Resource"**
   - Select your PostgreSQL database: `payment-echo-db`
   - This automatically adds `DATABASE_URL` environment variable

6. **Advanced Settings (Optional):**
   - **Health Check Path**: `/actuator/health`
   - **Auto-Deploy**: `Yes` (deploys on every push)

7. **Click "Create Web Service"**

### Step 4: Monitor Deployment

1. **Watch the Logs:**
   - Render will show build progress
   - Look for: "Build successful"
   - Then: "Starting application..."

2. **Expected Build Output:**
   ```
   > Task :build
   BUILD SUCCESSFUL
   ```

3. **Expected Start Output:**
   ```
   Started PaymentEchoApplication
   ```

### Step 5: Verify Deployment

Once deployment completes (usually 3-5 minutes):

1. **Get your app URL** (shown in Render dashboard)
   - Format: `https://payment-echo-system-xxxx.onrender.com`

2. **Test Health Endpoint:**
   ```bash
   curl https://payment-echo-system-xxxx.onrender.com/actuator/health
   ```
   Expected: `{"status":"UP"}`

3. **Test API:**
   ```bash
   curl https://payment-echo-system-xxxx.onrender.com/api/v1/payments
   ```
   Expected: JSON response with payments list

4. **Test Swagger UI** (in browser):
   ```
   https://payment-echo-system-xxxx.onrender.com/swagger-ui.html
   ```

## 🎯 Exact Commands for Render Dashboard

### Build Command:
```
./gradlew clean build -x test
```

### Start Command:
```
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

### Environment Variables:
```
JAVA_VERSION=17
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

**Note:** `DATABASE_URL` is automatically added when you link the PostgreSQL service.

## 🔧 If Using Maven Instead

If you prefer Maven, use these commands:

### Build Command:
```
mvn clean package -DskipTests
```

### Start Command:
```
java -jar target/payment-echo-system-0.0.1-SNAPSHOT.jar
```

## ⚠️ Common Issues & Quick Fixes

### Issue: Build fails - "Gradle wrapper not found"
**Fix:** Ensure `gradlew` is committed to Git:
```bash
git add gradlew gradlew.bat gradle/
git commit -m "Add Gradle wrapper"
git push
```

### Issue: JAR file not found
**Fix:** Check build logs - verify JAR path matches start command

### Issue: Database connection fails
**Fix:** 
1. Verify database is running (green status)
2. Check `DATABASE_URL` is set (should be auto-set when linked)
3. Verify `SPRING_PROFILES_ACTIVE=production`

### Issue: Port binding error
**Fix:** Verify `server.port=${PORT:8080}` in application.properties (already done)

## 📊 What Happens During Deployment

1. **Render clones your GitHub repo**
2. **Runs build command**: `./gradlew clean build -x test`
3. **Creates JAR**: `build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar`
4. **Runs start command**: `java -jar build/libs/...jar`
5. **Application starts** on port from `PORT` env variable
6. **Health check** verifies `/actuator/health` responds
7. **Service goes live** at your Render URL

## ✅ Post-Deployment Verification

Run these tests:

```bash
# 1. Health check
curl https://your-app.onrender.com/actuator/health

# 2. API endpoint
curl https://your-app.onrender.com/api/v1/payments

# 3. Creditors
curl https://your-app.onrender.com/api/v1/creditors

# 4. Debtors
curl https://your-app.onrender.com/api/v1/debtors

# 5. Swagger UI (open in browser)
# https://your-app.onrender.com/swagger-ui.html
```

## 🎉 Success Indicators

- ✅ Build completes successfully
- ✅ Application starts without errors
- ✅ Health endpoint returns `200 OK`
- ✅ API endpoints respond correctly
- ✅ Database connection works
- ✅ Swagger UI loads

## 📝 Next Steps After Deployment

1. **Update README** with production URL
2. **Test all endpoints** via Swagger UI
3. **Monitor logs** for any issues
4. **Set up custom domain** (optional, paid feature)
5. **Configure backups** for database (if needed)

---

**Ready to deploy?** Follow steps 1-5 above! 🚀

