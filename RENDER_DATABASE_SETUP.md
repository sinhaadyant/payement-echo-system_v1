# Render Database Setup Guide

## ⚠️ Important: Database Must Be Created Separately

Render Blueprint (`render.yaml`) does **not** support creating PostgreSQL databases directly. You need to create the database manually first, then link it to your web service.

## 📋 Step-by-Step Database Setup

### Step 1: Create PostgreSQL Database

1. **Go to Render Dashboard**: https://dashboard.render.com
2. **Click "New +"** → **"PostgreSQL"**
3. **Configure:**
   - **Name**: `payment-echo-db`
   - **Database**: `payment_echo_db`
   - **User**: `paymentuser` (or leave default)
   - **Plan**: **Free**
   - **Region**: Choose closest to your web service
4. **Click "Create Database"**
5. **Wait for database to be ready** (green status)

### Step 2: Create Web Service (Using Blueprint)

1. **Click "New +"** → **"Blueprint"**
2. **Connect GitHub** repository
3. **Select**: `sinhaadyant/payement-echo-system_v1`
4. **Select Branch**: `feature/deloitte-adyant-payment-enhancements`
5. **Click "Apply"**
6. Render will create the web service from `render.yaml`

### Step 3: Link Database to Web Service

1. **Go to your Web Service** in Render Dashboard
2. **Click "Settings"** tab
3. **Scroll to "Linked Resources"** section
4. **Click "Link Resource"**
5. **Select**: `payment-echo-db` (your PostgreSQL database)
6. **Click "Link"**

✅ **Done!** Render automatically adds `DATABASE_URL` environment variable.

## 🔧 Manual Setup (Alternative)

If you prefer to create everything manually:

### Create Web Service:

1. **Click "New +"** → **"Web Service"**
2. **Connect GitHub**: `sinhaadyant/payement-echo-system_v1`
3. **Branch**: `feature/deloitte-adyant-payment-enhancements`
4. **Settings:**
   - Name: `payment-echo-system`
   - Runtime: `Java`
   - Build Command: `./gradlew clean build -x test`
   - Start Command: `java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar`
5. **Environment Variables:**
   - `JAVA_VERSION` = `17`
   - `SPRING_PROFILES_ACTIVE` = `production`
   - `PORT` = `8080`
6. **Link Database:** Click "Link Resource" → Select `payment-echo-db`
7. **Click "Create Web Service"**

## ✅ Verification

After linking the database:

1. **Go to Web Service** → **"Environment"** tab
2. **Verify `DATABASE_URL` is present** (auto-added by Render)
3. **Check format**: `postgresql://user:password@host:port/database`

## 📝 Notes

- **Database URL**: Render automatically provides `DATABASE_URL` when you link the database
- **Connection**: Use "Internal Database URL" for same-region services (faster)
- **Security**: Database credentials are automatically managed by Render
- **Free Tier**: PostgreSQL free tier includes 90 days, then requires paid plan

## 🎯 Quick Summary

1. ✅ Create PostgreSQL database manually
2. ✅ Create web service (via Blueprint or manually)
3. ✅ Link database to web service
4. ✅ `DATABASE_URL` is automatically added
5. ✅ Deploy!

---

**Your `render.yaml` is now fixed and ready to use!** 🚀

