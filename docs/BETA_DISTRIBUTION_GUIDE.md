# Quick Start Guide for Beta Distribution

## 🚀 Option 1: Firebase App Distribution (Recommended)

### Prerequisites
- Firebase CLI installed
- Firebase project access
- Release keystore created

### Steps

#### 1. Generate Release Keystore (First time only)
```bash
mkdir -p keystore
keytool -genkey -v \
  -keystore keystore/rostry-release.jks \
  -alias rostry \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**⚠️ IMPORTANT:**
- Choose a strong password
- Remember the password - you'll need it for every release
- Backup the keystore file securely
- Never commit to Git (already in .gitignore)

#### 2. Create keystore.properties File
Create `keystore.properties` in project root:
```properties
storeFile=keystore/rostry-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=rostry
keyPassword=YOUR_KEY_PASSWORD
```

**⚠️ This file is gitignored - keep it secure!**

#### 3. Update build.gradle.kts (Already done in plan)
The signing configuration should load from keystore.properties

#### 4. Build Release APK
```bash
# Clean build
./gradlew clean

# Build release (will auto-bump version)
./gradlew assembleRelease

# APK location:
# app/build/outputs/apk/release/app-release.apk
```

#### 5. Install Firebase CLI
```bash
npm install -g firebase-tools

# Login
firebase login

# Select project
firebase use rostry-project-id
```

#### 6. Deploy to Firebase App Distribution
```bash
# Upload APK
firebase appdistribution:distribute \
  app/build/outputs/apk/release/app-release.apk \
  --app 1:YOUR_APP_ID:android:YOUR_APP_HASH \
  --groups "beta-testers" \
  --release-notes "Beta v1.0.0-beta.1
  - Fixed PERMISSION_DENIED error after OTP verification
  - Improved user profile creation
  - Ready for multi-user testing"
```

#### 7. Create Tester Group in Firebase Console
1. Go to Firebase Console → App Distribution
2. Click "Testers & Groups"
3. Create group "beta-testers"
4. Add tester email addresses

#### 8. Testers Receive Invitation
- Automatic email from Firebase
- Click link to install
- Simple one-click installation

---

## 🚀 Option 2: Direct APK Sharing (Simplest)

### Quick Steps

#### 1. Build Debug APK (No keystore needed)
```bash
./gradlew assembleDebug

# APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

#### 2. Rename for Clarity
```bash
cp app/build/outputs/apk/debug/app-debug.apk rostry-beta-v1.0.0.apk
```

#### 3. Share via:
- **Google Drive**: Upload and share link
- **WhatsApp**: Send directly to testers
- **Email**: Attach APK file
- **Dropbox/WeTransfer**: Any file sharing service

### Installation Instructions for Users
1. Download APK to phone
2. Settings → Security → Enable "Install unknown apps"
3. Open APK file
4. Tap Install
5. Open ROSTRY app

**⚠️ Note:** Debug builds are larger and less optimized than release builds

---

## 📝 Pre-Launch Checklist

### Firebase Configuration
- [ ] Phone authentication enabled in Firebase Console
- [ ] SHA-1 fingerprint added for your signing key
- [ ] Firestore security rules deployed
- [ ] Test phone number configured (optional)
- [ ] App Check configured

### Build Configuration
- [ ] Version bumped to 1.0.0-beta.1
- [ ] Release keystore created and secured
- [ ] ProGuard rules tested (release build)
- [ ] APK size checked (< 50MB recommended)

### Testing
- [ ] Tested auth fix with test phone number
- [ ] Verified profile creation in Firestore  
- [ ] Tested on at least 2 different devices
- [ ] Checked app works on Android 7+ (minSdk 24)

### Documentation
- [ ] USER_REGISTRATION_GUIDE.md reviewed
- [ ] Support contact information added
- [ ] Known issues documented

### Firebase Monitoring
- [ ] Firebase Analytics enabled
- [ ] Crashlytics configured
- [ ] Performance monitoring active

---

## 🧪 Testing the Build

### Test Registration Flow
```bash
# Install the APK
adb install -r app/build/outputs/apk/release/app-release.apk

# Or for debug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Clear previous app data (fresh install simulation)
adb shell pm clear com.rio.rostry

# Check logs during registration
adb logcat -s ROSTRY:* Firebase:* Auth:*
```

### Verify in Firebase Console
1. **Authentication** → Users
   - Check if new user appears after registration
   
2. **Firestore** → Data → users collection
   - Verify user document created with correct fields
   
3. **Analytics** → DebugView
   - Check if events are logging

---

## 🎯 Quick Commands Reference

```bash
# Build debug APK (fastest, for testing)
./gradlew assembleDebug

# Build release APK (optimized, for distribution)
./gradlew assembleRelease

# Install on connected device
adb install -r app/build/outputs/apk/release/app-release.apk

# Uninstall from device
adb uninstall com.rio.rostry

# Check APK size
ls -lh app/build/outputs/apk/release/app-release.apk

# Deploy Firestore rules
firebase deploy --only firestore:rules

# Upload to App Distribution
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app YOUR_APP_ID --groups "beta-testers"

# View Firebase projects
firebase projects:list

# Check current project
firebase use
```

---

## 📊 Post-Distribution Monitoring

### Firebase Console Checks (Daily)
1. **Authentication** tab
   - Monitor successful registrations
   - Check for auth errors

2. **Firestore** tab
   - Verify user documents being created
   - Check data consistency

3. **SMS Usage** (Settings → Usage)
   - Track daily SMS count
   - Alert if approaching 10/day limit

4. **Crashlytics**
   - Monitor crash-free percentage
   - Fix critical crashes immediately

5. **Analytics**
   - Track daily active users
   - Monitor user retention

---

## 🆘 Troubleshooting

### Build Fails
```bash
# Clean and retry
./gradlew clean assembleRelease

# Check for specific errors
./gradlew assembleRelease --stacktrace --info
```

### Firebase CLI Issues
```bash
# Reinstall
npm uninstall -g firebase-tools
npm install -g firebase-tools

# Re-login
firebase logout
firebase login
```

### APK Too Large
- Check ProGuard is enabled (release only)
- Enable resource shrinking
- Use APK splits for different architectures
- Remove unused dependencies

### Keystore Lost/Forgotten
- **CRITICAL:** You cannot create updates without original keystore
- Recovery: None (you'd need a new package name)
- Prevention: Backup keystore in multiple secure locations

---

**Ready to distribute?** Choose your method above and follow the steps!
