# 🚀 Auto-Deploy Setup - Deploy on Git Push

This guide shows you how to set up automatic deployment to Render whenever you push code to GitHub.

## ✅ How Auto-Deploy Works

When you push code to GitHub:
1. Render detects the push
2. Automatically starts a new build
3. Deploys the new version
4. Your app is updated automatically!

## 📋 Setup Steps

### Step 1: Initial Setup (One-Time)

#### Option A: Using Render Dashboard (Recommended)

1. **Go to Render Dashboard**: https://dashboard.render.com
2. **Create PostgreSQL Database** (if not done):
   - Click "New +" → "PostgreSQL"
   - Name: `payment-echo-db`
   - Plan: Free
   - Click "Create Database"

3. **Create Web Service**:
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select your repo: `payement-echo-system_v1`

4. **Configure Service:**
   - **Name**: `payment-echo-system`
   - **Region**: Choose closest
   - **Branch**: `main` (or your default branch)
   - **Root Directory**: Leave empty
   - **Runtime**: `Java`
   - **Build Command**: `./gradlew clean build -x test`
   - **Start Command**: `java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar`

5. **Environment Variables:**
   - `JAVA_VERSION` = `17`
   - `SPRING_PROFILES_ACTIVE` = `production`
   - `PORT` = `8080`

6. **Link Database:**
   - Click "Link Resource"
   - Select your PostgreSQL database

7. **Enable Auto-Deploy:**
   - ✅ **Auto-Deploy**: `Yes` (this is the default!)
   - This means every push to `main` branch will trigger deployment

8. **Click "Create Web Service"**

#### Option B: Using render.yaml (Infrastructure as Code)

1. **Ensure render.yaml is committed:**
   ```bash
   git add render.yaml
   git commit -m "Add Render configuration"
   git push origin main
   ```

2. **In Render Dashboard:**
   - Click "New +" → "Blueprint"
   - Connect your GitHub repository
   - Render will detect `render.yaml` and create services automatically
   - Auto-deploy is enabled by default

### Step 2: Verify Auto-Deploy is Enabled

1. Go to your Web Service in Render Dashboard
2. Click on **"Settings"** tab
3. Scroll to **"Auto-Deploy"** section
4. Verify it shows:
   - ✅ **Auto-Deploy**: `Yes`
   - **Branch**: `main` (or your default branch)

### Step 3: Test Auto-Deploy

1. **Make a small change:**
   ```bash
   # Edit any file
   echo "# Test auto-deploy" >> README.md
   
   # Commit and push
   git add README.md
   git commit -m "Test auto-deploy"
   git push origin main
   ```

2. **Watch Render Dashboard:**
   - Go to your Web Service
   - Click **"Events"** tab
   - You should see:
     - "Deploy started" (triggered by git push)
     - "Build started"
     - "Build succeeded"
     - "Deploy succeeded"

3. **Verify deployment:**
   - Check **"Logs"** tab for build progress
   - Once complete, test your app URL

## 🎯 How It Works

### Automatic Deployment Flow

```
Git Push to main branch
    ↓
Render detects push (via webhook)
    ↓
Starts new build
    ↓
Runs: ./gradlew clean build -x test
    ↓
Creates JAR file
    ↓
Runs: java -jar build/libs/...jar
    ↓
Application starts
    ↓
Health check passes
    ↓
Deployment complete ✅
```

### Manual Deployment (If Needed)

If you need to deploy manually:

1. Go to Render Dashboard
2. Click on your Web Service
3. Click **"Manual Deploy"** button
4. Select branch/commit
5. Click "Deploy"

## 🔧 Configuration Details

### Auto-Deploy Settings

**In Render Dashboard:**
- **Auto-Deploy**: `Yes` (enabled)
- **Branch**: `main` (or your default branch)
- **Pull Request Previews**: Optional (for testing PRs)

**In render.yaml:**
```yaml
services:
  - type: web
    name: payment-echo-system
    autoDeploy: true  # Automatically deploy on git push
    # ... other config
```

### Branch Configuration

**Default Behavior:**
- Pushes to `main` branch → Auto-deploy
- Pushes to other branches → No deploy (unless configured)

**To deploy from different branch:**
1. Go to Service Settings
2. Change "Branch" to your branch name
3. Or create separate service for that branch

## 📝 Deployment Workflow

### Daily Development Workflow

```bash
# 1. Make changes locally
# ... edit files ...

# 2. Test locally
./gradlew clean build -x test
java -jar build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar

# 3. Commit changes
git add .
git commit -m "Add new feature"

# 4. Push to GitHub
git push origin main

# 5. Render automatically deploys! 🚀
# (Check Render dashboard to monitor)
```

### Best Practices

1. **Always test locally first:**
   ```bash
   ./gradlew clean build -x test
   ```

2. **Use meaningful commit messages:**
   ```bash
   git commit -m "Add payment filtering feature"
   ```

3. **Monitor deployment:**
   - Check Render logs after push
   - Verify health endpoint after deployment

4. **Use branches for features:**
   - Create feature branches
   - Merge to main when ready
   - Main branch triggers auto-deploy

## 🔔 Notifications

### Email Notifications

Render can send email notifications:
- When deployment starts
- When deployment succeeds
- When deployment fails

**To enable:**
1. Go to Render Dashboard
2. Click your profile → Settings
3. Enable email notifications

### Webhook Notifications (Advanced)

You can set up webhooks to notify external services:
1. Go to Service Settings
2. Scroll to "Webhooks"
3. Add webhook URL
4. Configure events (deploy started, succeeded, failed)

## 🐛 Troubleshooting Auto-Deploy

### Issue: Auto-deploy not triggering

**Check:**
1. **Webhook status:**
   - Go to Service Settings
   - Check "GitHub" section
   - Verify webhook is connected

2. **Branch name:**
   - Ensure you're pushing to the configured branch
   - Default is `main` or `master`

3. **Render Dashboard:**
   - Check "Events" tab for any errors
   - Look for webhook delivery failures

**Fix:**
```bash
# Reconnect GitHub (if needed)
# In Render Dashboard:
# Settings → GitHub → Disconnect → Reconnect
```

### Issue: Build fails on auto-deploy

**Check logs:**
1. Go to Service → "Logs" tab
2. Look for build errors
3. Common issues:
   - Missing dependencies
   - Build command errors
   - Environment variable issues

**Fix:**
- Test build locally first
- Fix errors before pushing
- Check build command is correct

### Issue: Deployment succeeds but app doesn't work

**Check:**
1. **Application logs:**
   - Go to "Logs" tab
   - Look for runtime errors

2. **Health check:**
   ```bash
   curl https://your-app.onrender.com/actuator/health
   ```

3. **Environment variables:**
   - Verify all required vars are set
   - Check `DATABASE_URL` is correct

## ✅ Verification Checklist

After setup, verify:

- [ ] Auto-Deploy is enabled in Render Dashboard
- [ ] GitHub repository is connected
- [ ] Webhook is active (check GitHub repo settings)
- [ ] Test push triggers deployment
- [ ] Build completes successfully
- [ ] Application starts correctly
- [ ] Health endpoint responds

## 🎉 You're All Set!

Now every time you push to GitHub:

```bash
git push origin main
```

Render will automatically:
1. ✅ Detect the push
2. ✅ Build your application
3. ✅ Deploy the new version
4. ✅ Update your live app

**No manual steps needed!** 🚀

---

## 📚 Additional Resources

- [Render Auto-Deploy Docs](https://render.com/docs/auto-deploy)
- [Render Webhooks](https://render.com/docs/webhooks)
- [GitHub Webhooks](https://docs.github.com/en/developers/webhooks-and-events/webhooks)

