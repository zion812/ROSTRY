---
Version: 2.1
Last Updated: 2025-12-29
Audience: Developers, DevOps, Release Managers
Status: Active
---

# Deployment & Release Management

**Version:** 2.1  
**Last Updated:** 2025-12-29  
**Audience:** Developers, DevOps, Release Managers

---

Comprehensive procedures for preparing, testing, and shipping ROSTRY releases to production.

## Table of Contents

- [Overview](#overview)
- [Pre-Deployment](#pre-deployment)
- [Build Configuration](#build-configuration)
- [Environments](#environments)
- [Deployment Procedures](#deployment-procedures)
- [Release Checklists](#release-checklists)
- [Rollback Procedures](#rollback-procedures)
- [Monitoring](#monitoring)
- [Post-Deployment Verification](#post-deployment-verification)
- [Hotfix Process](#hotfix-process)
- [Version Management](#version-management)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [Automation](#automation)

---

## Overview

### Release Cycle

- **Frequency**: Bi-weekly releases to production
- **Schedule**: Wednesdays 10:00 AM (to allow time for monitoring)
- **Code Freeze**: Tuesday 5:00 PM (day before release)

### Deployment Targets

1. **Firebase App Distribution** - Internal testing, QA validation
2. **Google Play Internal Track** - Team testing (<100 testers)
3. **Google Play Beta** - Public beta testing
4. **Google Play Production** - General availability with staged rollout

---

## Pre-Deployment

### 1. Environment Verification

**Check Prerequisites**:
```bash
# Verify JDK
java -version  # Should be 17

# Verify Android SDK
sdkmanager --list_installed | grep "build-tools"

# Verify Firebase CLI
firebase --version

# Verify Git clean state
git status
```

### 2. Dependency Updates

**Review and Update**:
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Review security advisories
./gradlew dependencyCheckAnalyze
```

**Update Carefully**:
- Review changelogs for breaking changes
- Test thoroughly after updates
- Update one major dependency at a time

### 3. Security Audit

**Code Security**:
- [ ] No hardcoded secrets or API keys
- [ ] All sensitive data encrypted
- [ ] Firebase security rules reviewed
- [ ] ProGuard rules verified

**Run Security Scans**:
```bash
# OWASP dependency check
./gradlew dependencyCheckAnalyze

# Lint security issues
./gradlew lint
```

### 4. Performance Testing

**Baseline Performance**:
- [ ] App startup time <2 seconds
- [ ] Smooth scrolling (60 FPS)
- [ ] Memory usage <150MB
- [ ] Battery consumption acceptable

**Run Benchmarks**:
```bash
./gradlew connectedBenchmarkAndroidTest
```

### 5. Backups

**Before Any Deployment**:
- [ ] Backup Firestore data
- [ ] Backup Firebase Storage
- [ ] Tag current release in Git
- [ ] Document database schema version
- [ ] Deploy latest Firestore indexes from `firebase/firestore.indexes.json`

### 6. Firestore Index Deployment
Ensure all composite indexes are deployed before releasing features that depend on complex queries (e.g., Evidence Orders, Community Hub).

```bash
# Deploy only indexes
firebase deploy --only firestore:indexes
```

Verify index status in Firebase Console: **Firestore Database** > **Indexes**. Status must be **Enabled**.

---

## Build Configuration

### Build Variants

**Configuration**: `app/build.gradle.kts`

```kotlin
android {
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
        }
        
        create("staging") {
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            isDebuggable = true
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Signing Configuration

**Setup Signing** (never commit to Git):

**keystore.properties** (gitignored):
```properties
storeFile=/path/to/rostry-release.keystore
storePassword=***
keyAlias=rostry_release
keyPassword=***
```

**build.gradle.kts**:
```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] ?: "")
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }
}
```

### ProGuard/R8 Rules

**app/proguard-rules.pro**:
```proguard
# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep data models
-keep class com.rio.rostry.data.** { *; }
```

### Version Management

**app/build.gradle.kts**:
```kotlin
android {
    defaultConfig {
        versionCode = 10  // Increment for every release
        versionName = "1.2.0"  // Semantic versioning
    }
}
```

**Version Code Strategy**:
- Increment by 1 for each release
- Never reuse version codes
- Never decrease version codes

---

## Environments

### Development

**Purpose**: Local development and debugging

**Configuration**:
- Firebase Project: `rostry-dev`
- API Endpoint: `https://dev-api.rostry.com`
- Database: Local Room with test data
- Logging: Verbose

### Staging

**Purpose**: Pre-production testing and QA validation

**Configuration**:
- Firebase Project: `rostry-staging`
- API Endpoint: `https://staging-api.rostry.com`
- Database: Staging Firestore
- Logging: Debug level

**Access**: Internal team + QA testers

### Production

**Purpose**: Live user-facing environment

**Configuration**:
- Firebase Project: `rostry-prod`
- API Endpoint: `https://api.rostry.com`
- Database: Production Firestore
- Logging: Error level only

**Access**: All users

### Environment Switching

**BuildConfig Approach**:
```kotlin
buildTypes {
    debug {
        buildConfigField("String", "API_BASE_URL", "\"https://dev-api.rostry.com\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"rostry-dev\"")
    }
    release {
        buildConfigField("String", "API_BASE_URL", "\"https://api.rostry.com\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"rostry-prod\"")
    }
}
```

---

## Deployment Procedures

### Firebase App Distribution

**Use Case**: Internal testing, QA validation, beta testing

**Step-by-Step**:

1. **Build Release APK**:
```bash
./gradlew assembleRelease
```

2. **Upload to Firebase**:
```bash
firebase appdistribution:distribute \
  app/build/outputs/apk/release/app-release.apk \
  --app 1:123456789:android:abc123 \
  --groups "internal-testers" \
  --release-notes "Release v1.2.0 - Bug fixes and improvements"
```

3. **Via Android Studio**:
- **Build** → **Generate Signed Bundle / APK**
- Select **APK**
- Choose **release** key
- **Finish**
- Use Firebase Console to upload

4. **Automated (GitHub Actions)**:
```yaml
- name: Deploy to Firebase
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    token: ${{ secrets.FIREBASE_TOKEN }}
    groups: internal-testers
    file: app/build/outputs/apk/release/app-release.apk
```

**Complete Firebase Guide**: [firebase-setup.md](firebase-setup.md)

### Google Play Console Deployment

#### Internal Track

**Purpose**: Team testing (<100 testers)

**Steps**:

1. **Build App Bundle**:
```bash
./gradlew bundleRelease
```

2. **Upload to Play Console**:
- Go to [Play Console](https://play.google.com/console)
- Select ROSTRY app
- **Release** → **Testing** → **Internal testing**
- **Create new release**
- Upload `app/build/outputs/bundle/release/app-release.aab`
- Add release notes
- **Review release** → **Start rollout to Internal testing**

3. **Via Command Line** (with Play Console API):
```bash
# Using fastlane or similar tool
bundle exec fastlane deploy_internal
```

#### Beta Track

**Purpose**: Public beta testing (open or closed)

**Steps**:
1. Promote from Internal or create new release
2. **Release** → **Testing** → **Open testing** or **Closed testing**
3. Upload AAB
4. Add release notes
5. **Review release** → **Start rollout**

**Beta Testers**:
- Open link: Share opt-in URL
- Closed: Email-based invitations

#### Production Track

**Purpose**: General availability for all users

**Steps**:

1. **Final Checks**:
- [ ] All tests pass
- [ ] Beta period complete (1-2 weeks)
- [ ] No critical bugs
- [ ] Stakeholder approval

2. **Create Production Release**:
- **Release** → **Production**
- Promote from Beta or upload new AAB
- Version must be higher than current production

3. **Staged Rollout** (Recommended):
- Start with 5% of users
- Monitor for 24 hours
- If stable, increase to 10% → 25% → 50% → 100%

4. **Release Notes**:
```
Version 1.2.0

What's New:
• Added advanced analytics dashboard
• Improved farm monitoring performance
• Bug fixes and stability improvements

Bug Fixes:
• Fixed crash on empty product list
• Resolved sync issues on slow networks
```

5. **Submit for Review**:
- **Review release** → **Start rollout to Production**
- Play Store review: 1-7 days

---

## Release Checklists

### Pre-Release Checklist

**Code Quality**:
- [ ] All code merged to `main` branch
- [ ] Code freeze enforced (no new changes)
- [ ] All PRs reviewed and approved
- [ ] No TODO/FIXME comments in critical paths

**Testing**:
- [ ] Unit tests pass (100%)
- [ ] Integration tests pass
- [ ] UI tests pass
- [ ] Manual smoke testing complete
- [ ] Performance benchmarks meet targets
- [ ] Accessibility testing complete

**Documentation**:
- [ ] CHANGELOG.md updated
- [ ] Release notes drafted
- [ ] API documentation updated (if applicable)
- [ ] User-facing documentation updated

**Build**:
- [ ] Version code incremented
- [ ] Version name updated (semantic versioning)
- [ ] Signing keys accessible
- [ ] ProGuard rules tested
- [ ] App bundle builds successfully

**Compliance**:
- [ ] Privacy policy updated (if needed)
- [ ] Terms of service reviewed
- [ ] Required permissions justified
- [ ] Third-party licenses updated

### Release Day Checklist

**Morning** (2-3 hours before release):
- [ ] Final smoke test on staging
- [ ] Verify monitoring dashboards operational
- [ ] Ensure team availability for support
- [ ] Backup production data

**Release**:
- [ ] Build and upload to Play Console
- [ ] Start staged rollout (5%)
- [ ] Monitor Crashlytics for first 30 minutes
- [ ] Check user reports/reviews

**Post-Release** (First 24 hours):
- [ ] Monitor crash rate (<1%)
- [ ] Monitor ANR rate (<0.5%)
- [ ] Check Firebase Performance metrics
- [ ] Review user feedback
- [ ] Gradual increase rollout if stable

### Post-Release Checklist

**Week 1**:
- [ ] Complete rollout to 100%
- [ ] Monitor metrics daily
- [ ] Respond to user reviews
- [ ] Track feature adoption

**Week 2**:
- [ ] Review analytics data
- [ ] Plan next release
- [ ] Document lessons learned

---

## Rollback Procedures

### When to Rollback

**Triggers**:
- Crash rate >2%
- ANR rate >1%
- Critical security vulnerability
- Data corruption issues
- Major feature completely broken

### Play Store Rollback

**Immediate Actions**:

1. **Halt Rollout**:
- Go to Play Console → Production → Release
- **Halt rollout** (if staged rollout in progress)

2. **Push Previous Version**:
- Create new release with previous version
- Must increment version code but can use same version name with suffix
- Example: `1.2.0` → `1.2.0-rollback` (version code still increments)

**Note**: Cannot truly "rollback" on Play Store, must push previous APK as new release

### Firebase App Distribution Rollback

**Distribute Previous Build**:
```bash
firebase appdistribution:distribute \
  previous-builds/app-release-v1.1.0.apk \
  --app $FIREBASE_APP_ID \
  --groups "all-users"
```

### Communication Plan

**Immediate** (within 1 hour):
1. Notify engineering team
2. Post status update on status page
3. Draft user communication

**Within 4 hours**:
1. Email affected users (if identifiable)
2. Post on social media
3. Update app store description

**Post-Mortem** (within 1 week):
1. Root cause analysis
2. Document lessons learned
3. Implement preventive measures

---

## Monitoring

### Crashlytics

**Setup**: `FirebaseCrashlytics.getInstance()`

**Monitor**:
- Crash-free users rate (target: >99%)
- Top crashes by occurrence
- ANRs (Application Not Responding)

**Alerts**:
```kotlin
if (crashFreeRate < 99.0) {
    alertTeam("Crash rate spike detected")
}
```

### Firebase Performance

**Key Metrics**:
- App startup time
- Screen rendering time
- Network request latency
- Custom traces for critical flows

**Example**:
```kotlin
val trace = Firebase.performance.newTrace("checkout_flow")
trace.start()
// Checkout logic
trace.stop()
```

### Analytics

**Track**:
- Daily Active Users (DAU)
- Monthly Active Users (MAU)
- Retention rates (D1, D7, D30)
- Feature adoption
- Conversion funnels

**Firebase Analytics**:
```kotlin
firebaseAnalytics.logEvent("purchase_completed") {
    param("item_id", productId)
    param("value", price)
}
```

### Play Console Vitals

**Monitor**:
- Android vitals score
- Crash rate
- ANR rate
- Battery usage
- Wake locks

**Target**: Stay in "Good Behavior" threshold

---

## Post-Deployment Verification

### Smoke Tests (First 30 Minutes)

**Critical Flows**:
1. **Authentication**:
   - [ ] User can register
   - [ ] User can login
   - [ ] Session persists

2. **Core Features**:
   - [ ] Product listing loads
   - [ ] Can create product
   - [ ] Can initiate transfer
   - [ ] Push notifications work

3. **Data Sync**:
   - [ ] Offline changes sync
   - [ ] Real-time updates work

### First 24 Hours Monitoring

**Metrics to Watch**:
- Crash rate (<1%)
- ANR rate (<0.5%)
- API error rates
- Database query performance
- User complaints/reviews

**Actions if Issues Detected**:
- Minor: Schedule hotfix
- Major: Halt rollout
- Critical: Rollback immediately

### User Communication

**Channels**:
1. In-app changelog
2. Email to active users
3. Social media announcement
4. Blog post for major releases

**Template**:
```
🎉 ROSTRY v1.2.0 is now available!

What's New:
• Advanced Analytics Dashboard
• Improved Performance
• Bug Fixes

Update now to get the latest features!
```

---

## Hotfix Process

### When to Hotfix

**Criteria**:
- Critical bug affecting majority of users
- Security vulnerability
- Data loss/corruption
- Payment processing issues
- Cannot wait for regular release cycle

### Expedited Process

**Timeline**: 2-4 hours from discovery to deployment

**Steps**:

1. **Immediate Triage** (15 minutes):
   - Confirm severity
   - Identify root cause
   - Assign developer

2. **Fix & Test** (1-2 hours):
   - Create hotfix branch from `main`
   - Implement minimal fix
   - Write test to prevent regression
   - Test thoroughly

3. **Build & Deploy** (30 minutes):
   - Increment version code
   - Add `.1` to version name (e.g., `1.2.0` → `1.2.1`)
   - Build release
   - Upload to Play Console

4. **Fast-Track Review**:
   - Mark as "Bug fix" in Play Console
   - Contact Google if urgent (critical security)

5. **Monitor** (24 hours):
   - Watch metrics closely
   - Prepare rollback if needed

**Hotfix Branch Naming**:
```
hotfix/v1.2.1-fix-crash-on-empty-cart
```

---

## Version Management

### Semantic Versioning

**Format**: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes, major features
- **MINOR**: New features, backwards compatible
- **PATCH**: Bug fixes, minor improvements

**Examples**:
- `1.0.0` → `1.1.0`: Added analytics dashboard
- `1.1.0` → `1.1.1`: Fixed crash bug
- `1.9.0` → `2.0.0`: Complete UI redesign

### Version Code Strategy

**Formula**: `MAJOR * 10000 + MINOR * 100 + PATCH`

Examples:
- v1.0.0 → versionCode 10000
- v1.2.3 → versionCode 10203
- v2.0.0 → versionCode 20000

**Implementation**:
```kotlin
val versionMajor = 1
val versionMinor = 2
val versionPatch = 0

android {
    defaultConfig {
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "$versionMajor.$versionMinor.$versionPatch"
    }
}
```

### CHANGELOG.md

**Format**:
```markdown
# Changelog

## [1.2.0] - 2025-01-15

### Added
- Advanced analytics dashboard
- Farm performance reporting

### Changed
- Improved sync performance
- Updated UI for better accessibility

### Fixed
- Crash on empty product list
- Memory leak in image loading

### Security
- Updated Firebase SDK to fix vulnerability
```

---

## Security

### Signing Key Management

**Best Practices**:
- Store keystore file securely (not in Git)
- Use password manager for passwords
- Backup keystore in multiple secure locations
- Document key recovery process

**Key Backup Locations**:
1. Encrypted cloud storage
2. Team password manager
3. Physical secure location

**If Key Lost**: Cannot update app on Play Store (must publish new app)

### API Key Rotation

**Schedule**: Every 6 months or after suspected compromise

**Process**:
1. Generate new API key in Google Cloud Console
2. Update `local.properties` template
3. Deploy new version with new key
4. Wait for majority adoption
5. Revoke old key

### Security Scanning

**Pre-Release**:
```bash
# Dependency vulnerabilities
./gradlew dependencyCheckAnalyze

# Code security issues
./gradlew lint

# Secrets detection
git secrets --scan
```

### Compliance

**Requirements**:
- GDPR compliance (EU users)
- COPPA compliance (if <13 users)
- Play Store data safety
- Privacy policy updated

**Data Safety Form**: Update in Play Console after data collection changes

---

## Troubleshooting

### Build Fails

**Issue**: Signing configuration not found
```bash
# Verify keystore.properties exists
ls keystore.properties

# Verify paths are correct
cat keystore.properties
```

**Issue**: ProGuard breaks app
```bash
# Test with ProGuard locally
./gradlew assembleRelease
adb install app/build/outputs/apk/release/app-release.apk

# Check ProGuard mapping
cat app/build/outputs/mapping/release/mapping.txt
```

### Upload Fails

**Issue**: Version code already exists
- Increment version code in `build.gradle.kts`
- Rebuild

**Issue**: Signature mismatch
- Verify using correct keystore
- Check key alias and passwords

### Post-Deployment Issues

**Issue**: High crash rate
1. Check Crashlytics for top crashes
2. Reproduce locally
3. Prepare hotfix
4. Consider rollback if severe

**Issue**: Users can't update
- Verify version code is higher
- Check Play Store rollout status
- Clear Play Store cache on device

---

## Automation

### GitHub Actions Workflow

**File**: `.github/workflows/release.yml`

```yaml
name: Release to Play Store

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      
      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore
      
      - name: Build Release AAB
        run: ./gradlew bundleRelease
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      
      - name: Upload to Play Console
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
          packageName: com.rio.rostry
          releaseFiles: app/build/outputs/bundle/release/app-release.aab
          track: internal
          status: completed
```

**Complete CI/CD Guide**: [ci-cd.md](ci-cd.md)

### Fastlane Integration

**Gemfile**:
```ruby
source "https://rubygems.org"
gem "fastlane"
```

**fastlane/Fastfile**:
```ruby
lane :deploy_internal do
  gradle(task: "bundleRelease")
  upload_to_play_store(
    track: 'internal',
    aab: 'app/build/outputs/bundle/release/app-release.aab'
  )
end

lane :deploy_production do
  gradle(task: "bundleRelease")
  upload_to_play_store(
    track: 'production',
    rollout: '0.05'  # 5% staged rollout
  )
end
```

**Usage**:
```bash
bundle exec fastlane deploy_internal
bundle exec fastlane deploy_production
```

---

**For deployment questions or issues, consult this guide or contact the DevOps team.**

**Related Documentation**:
- [CI/CD Pipeline](ci-cd.md)
- [Firebase Setup](firebase-setup.md)
- [Security & Encryption](security-encryption.md)
- [CHANGELOG.md](../CHANGELOG.md)
