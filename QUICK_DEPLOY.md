# 🚀 Quick Deploy to Render - Copy & Paste Guide

## ✅ Everything is Ready!

- ✅ Build verified: JAR created (67MB)
- ✅ PORT environment variable support added
- ✅ PostgreSQL dependency added
- ✅ Production profile configured
- ✅ All configuration files created

## 📋 Deploy in 5 Steps

### Step 1: Commit & Push (Run these commands)

```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### Step 2: Create PostgreSQL Database

1. Go to: https://dashboard.render.com
2. Click **"New +"** → **"PostgreSQL"**
3. Settings:
   - Name: `payment-echo-db`
   - Database: `payment_echo_db`
   - Plan: **Free**
4. Click **"Create Database"**
5. Wait for green status ✅

### Step 3: Create Web Service

1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repo
3. **Copy these EXACT values:**

**Build Command:**
```
./gradlew clean build -x test
```

**Start Command:**
```
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

**Environment Variables:**
- `JAVA_VERSION` = `17`
- `SPRING_PROFILES_ACTIVE` = `production`
- `PORT` = `8080`

4. **Link Database:** Click "Link Resource" → Select `payment-echo-db`
5. Click **"Create Web Service"**

### Step 4: Wait for Deployment

- Build: ~2-3 minutes
- Start: ~30 seconds
- Total: ~3-5 minutes

### Step 5: Test Your App

Your app URL: `https://payment-echo-system-xxxx.onrender.com`

```bash
# Test health
curl https://your-app.onrender.com/actuator/health

# Test API
curl https://your-app.onrender.com/api/v1/payments
```

## 🎯 Exact Values for Render Dashboard

**Build Command:**
```
./gradlew clean build -x test
```

**Start Command:**
```
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar
```

**Environment Variables:**
```
JAVA_VERSION=17
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

**Note:** `DATABASE_URL` is auto-added when you link the database.

## ✅ Done!

Your app will be live at: `https://your-app-name.onrender.com`

See `RENDER_DEPLOYMENT.md` for detailed troubleshooting.
