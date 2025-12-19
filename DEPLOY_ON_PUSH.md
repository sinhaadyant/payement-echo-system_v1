# 🚀 Deploy on Git Push - Quick Setup

## ✅ Auto-Deploy is Already Configured!

Your `render.yaml` includes `autoDeploy: true`, which means Render will automatically deploy whenever you push to GitHub.

## 📋 One-Time Setup

### Step 1: Push Current Code

```bash
# Add all files
git add .

# Commit
git commit -m "Configure Render auto-deploy"

# Push to GitHub
git push origin main
```

### Step 2: Create Services on Render

**Option A: Using render.yaml (Easiest)**

1. Go to: https://dashboard.render.com
2. Click **"New +"** → **"Blueprint"**
3. Connect your GitHub repository
4. Render will detect `render.yaml` and create:
   - PostgreSQL database
   - Web service
   - Auto-deploy enabled ✅

**Option B: Manual Setup**

1. Create PostgreSQL database
2. Create Web Service
3. **Enable Auto-Deploy:**
   - In service settings
   - ✅ **Auto-Deploy**: `Yes` (default)
   - **Branch**: `main`

## 🎯 How It Works

### After Setup:

```bash
# 1. Make changes
# ... edit code ...

# 2. Commit
git add .
git commit -m "Update payment API"

# 3. Push
git push origin main

# 4. Render automatically deploys! 🚀
# (Check Render dashboard to see progress)
```

### What Happens:

1. ✅ You push to GitHub
2. ✅ Render detects the push (via webhook)
3. ✅ Render starts building
4. ✅ Render deploys automatically
5. ✅ Your app is updated!

## 🔍 Verify Auto-Deploy

### Check in Render Dashboard:

1. Go to your Web Service
2. Click **"Settings"** tab
3. Look for **"Auto-Deploy"** section:
   - Should show: ✅ **Yes**
   - Branch: `main`

### Test It:

```bash
# Make a small change
echo "<!-- Updated -->" >> README.md

# Commit and push
git add README.md
git commit -m "Test auto-deploy"
git push origin main

# Watch Render Dashboard → Events tab
# You should see deployment start automatically!
```

## 📝 Configuration Files

### render.yaml
```yaml
autoDeploy: true  # ✅ Auto-deploy enabled
```

### GitHub Webhook (Auto-configured)
- Render automatically sets up webhook
- Listens for pushes to `main` branch
- Triggers deployment automatically

## ✅ That's It!

Once you've set up Render services, every `git push origin main` will automatically deploy your app!

**No manual steps needed after initial setup.** 🎉

---

**Next Steps:**
1. Push code: `git push origin main`
2. Create services on Render (one-time)
3. Enjoy automatic deployments! 🚀

