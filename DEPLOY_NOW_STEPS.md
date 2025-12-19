# 🚀 Deploy Now - Step by Step

## Step 1: Commit and Push to GitHub

Run these commands:

```bash
# Add all files
git add .

# Commit
git commit -m "Configure Render deployment with auto-deploy"

# Push to GitHub
git push origin main
```

## Step 2: Deploy on Render

### Quick Method (Using Blueprint):

1. **Go to Render Dashboard**: https://dashboard.render.com
2. **Sign up/Login** if needed
3. **Click "New +"** → **"Blueprint"**
4. **Connect GitHub** (authorize Render)
5. **Select Repository**: `payement-echo-system_v1`
6. **Click "Apply"**
7. **Render will automatically:**
   - Create PostgreSQL database
   - Create Web Service
   - Configure auto-deploy
   - Link database to service

### Manual Method:

1. **Create PostgreSQL Database:**
   - Click "New +" → "PostgreSQL"
   - Name: `payment-echo-db`
   - Database: `payment_echo_db`
   - Plan: **Free**
   - Click "Create Database"

2. **Create Web Service:**
   - Click "New +" → "Web Service"
   - Connect GitHub repository
   - Select: `payement-echo-system_v1`
   - **Settings:**
     - Name: `payment-echo-system`
     - Branch: `main`
     - Runtime: `Java`
     - Build Command: `./gradlew clean build -x test`
     - Start Command: `java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar`
   - **Environment Variables:**
     - `JAVA_VERSION` = `17`
     - `SPRING_PROFILES_ACTIVE` = `production`
     - `PORT` = `8080`
   - **Link Database:** Click "Link Resource" → Select `payment-echo-db`
   - **Auto-Deploy:** ✅ Yes (default)
   - Click "Create Web Service"

## Step 3: Wait for Deployment

- Build: ~2-3 minutes
- Deploy: ~30 seconds
- Total: ~3-5 minutes

Watch the **"Logs"** tab for progress.

## Step 4: Test Your App

Once deployed, your app URL will be:
`https://payment-echo-system-xxxx.onrender.com`

Test it:
```bash
# Health check
curl https://your-app.onrender.com/actuator/health

# API test
curl https://your-app.onrender.com/api/v1/payments
```

## ✅ Done!

Your app is now live and will auto-deploy on every `git push origin main`!

