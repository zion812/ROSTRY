# Firebase Setup Guide

**Version:** 1.0  
**Last Updated:** 2025-01-15  
**Audience:** Developers, DevOps

---

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Initial Firebase Project Setup](#initial-firebase-project-setup)
- [Firebase Console Configuration](#firebase-console-configuration)
- [Local Development Setup](#local-development-setup)
- [Firestore Database](#firestore-database)
- [Firebase Authentication](#firebase-authentication)
- [Firebase Storage](#firebase-storage)
- [Cloud Functions](#cloud-functions)
- [Firebase Cloud Messaging (FCM)](#firebase-cloud-messaging-fcm)
- [Security Rules](#security-rules)
- [Firebase Indexes](#firebase-indexes)
- [Environment Configuration](#environment-configuration)
- [Testing with Firebase Emulators](#testing-with-firebase-emulators)
- [Deployment](#deployment)
- [Monitoring and Analytics](#monitoring-and-analytics)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

---

## Overview

ROSTRY uses Firebase as its backend-as-a-service (BaaS) platform, providing:
- **Authentication**: Phone number authentication with custom claims
- **Firestore**: NoSQL cloud database for real-time data
- **Cloud Storage**: File and media storage
- **Cloud Functions**: Serverless backend logic
- **Cloud Messaging**: Push notifications
- **Analytics**: User behavior tracking
- **Crashlytics**: Crash reporting
- **Performance Monitoring**: App performance tracking

---

## Prerequisites

### Required Tools

1. **Node.js** (v16 or higher)
   ```bash
   node --version
   npm --version
   ```

2. **Firebase CLI**
   ```bash
   npm install -g firebase-tools
   firebase --version
   ```

3. **Java JDK** 17 (for Android development)

4. **Android Studio** (latest stable)

### Required Access

- Google Account
- Firebase Console access
- Google Cloud Console access (for Maps API)
- Admin access to Firebase project (for deployment)

---

## Initial Firebase Project Setup

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Project name: `ROSTRY` (or your preferred name)
4. Enable Google Analytics (recommended)
5. Choose or create Google Analytics account
6. Click "Create project"

### 2. Add Android App

1. In Firebase Console, click the Android icon
2. **Android package name**: `com.rio.rostry`
3. **App nickname**: `ROSTRY Android` (optional)
4. **Debug signing certificate SHA-1**: Get from keystore:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
5. Click "Register app"
6. Download `google-services.json`
7. Place `google-services.json` in `app/` directory
8. Click "Next" through remaining steps

### 3. Upgrade to Blaze Plan

**Required for**:
- Cloud Functions
- External API calls
- Higher quotas

1. Go to Firebase Console → Settings → Usage and billing
2. Upgrade to Blaze (Pay as you go) plan
3. Set up billing alerts:
   - Budget: $50/month (adjust as needed)
   - Email alerts at 50%, 90%, 100%

---

## Firebase Console Configuration

### Firebase Settings

1. **Project Settings**:
   - Navigate to: Settings → Project Settings
   - Note your Project ID
   - Configure support email
   - Add team members with appropriate roles

2. **Service Accounts**:
   - Settings → Service Accounts
   - Generate private key for server-side operations (if needed)
   - Store securely, never commit to version control

---

## Local Development Setup

### 1. Firebase CLI Login

```bash
firebase login
```

### 2. Initialize Firebase

In your project root:

```bash
firebase init
```

Select the following features:
- ☑ Firestore
- ☑ Functions
- ☑ Storage
- ☑ Emulators

Configuration:
- **Firestore Rules**: `firebase/firestore.rules`
- **Firestore Indexes**: `firebase/firestore.indexes.json`
- **Functions**: Use existing directory or create new
- **Storage Rules**: `firebase/storage.rules` (create if needed)
- **Emulators**: Select Firestore, Functions, Storage, Auth

### 3. Project Structure

```
ROSTRY/
├── app/
│   └── google-services.json          # Android app config
├── firebase/
│   ├── firestore.rules                # Security rules
│   ├── firestore.indexes.json         # Composite indexes
│   ├── firestore_enthusiast_rules.rules  # Enthusiast-specific rules
│   ├── firestore_enthusiast_indexes.json # Enthusiast indexes
│   ├── firestore_farm_rules.rules     # Farm-specific rules
│   └── firestore_farm_indexes.json    # Farm indexes
├── functions/                          # Cloud Functions (if applicable)
│   ├── package.json
│   └── index.js
└── firebase.json                       # Firebase configuration
```

---

## Firestore Database

### Create Database

1. Firebase Console → Firestore Database
2. Click "Create database"
3. Start in **production mode** (rules will be deployed separately)
4. Choose location:
   - For India: `asia-south1` (Mumbai)
   - For multi-region: `nam5` or `eur3`
5. Click "Enable"

### Database Structure

ROSTRY uses the following main collections:

```
firestore/
├── users/                      # User profiles
├── products/                   # Marketplace listings
├── orders/                     # Order history
├── transfers/                  # Fowl transfer records
├── posts/                      # Social posts
│   └── {postId}/
│       ├── comments/           # Post comments
│       └── likes/              # Post likes
├── notifications/              # User notifications
├── analytics/                  # Analytics data
└── enthusiasts/                # Enthusiast-specific data
    └── {userId}/
        ├── mating_logs/        # Breeding records
        ├── egg_collections/    # Egg collection data
        └── dashboard_snapshots/ # Dashboard state
```

### Data Models

**User Document** (`users/{userId}`):
```json
{
  "userId": "string",
  "phoneNumber": "string",
  "role": "GENERAL|FARMER|ENTHUSIAST",
  "displayName": "string",
  "email": "string (optional)",
  "profileImageUrl": "string (optional)",
  "isVerifiedSeller": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

**Product Document** (`products/{productId}`):
```json
{
  "productId": "string",
  "sellerId": "string",
  "name": "string",
  "description": "string",
  "price": "number",
  "quantity": "number",
  "category": "string",
  "breed": "string",
  "imageUrls": ["string"],
  "isActive": "boolean",
  "createdAt": "timestamp"
}
```

**Transfer Document** (`transfers/{transferId}`):
```json
{
  "transferId": "string",
  "fromUserId": "string",
  "toUserId": "string",
  "fowlId": "string",
  "status": "PENDING|APPROVED|REJECTED|COMPLETED",
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "verificationData": "map"
}
```

---

## Firebase Authentication

### Enable Phone Authentication

1. Firebase Console → Authentication
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Phone" provider
5. Configure:
   - **Test phone numbers** (for development):
     - Add: `+1234567890` with verification code `123456`
   - **SMS quota**: Default 10/day for test
   - **App Verification**: Enabled by default

### Configure Custom Claims

Custom claims are set via Cloud Functions or Admin SDK:

```javascript
// Example: Set user role via Cloud Function
admin.auth().setCustomUserClaims(userId, {
  role: 'FARMER',
  isVerifiedSeller: true
});
```

### Security

- Enable **App Check** for production
- Configure reCAPTCHA v3 for web
- Use SafetyNet Attestation API for Android
- Monitor authentication logs for suspicious activity

---

## Firebase Storage

### Create Storage Bucket

1. Firebase Console → Storage
2. Click "Get started"
3. Start in **production mode**
4. Choose location (same as Firestore for consistency)
5. Click "Done"

### Storage Structure

```
gs://<your-project>.appspot.com/
├── users/
│   └── {userId}/
│       ├── profile/            # Profile images
│       └── documents/          # User documents
├── products/
│   └── {productId}/            # Product images
├── posts/
│   └── {postId}/               # Post media
├── transfers/
│   └── {transferId}/           # Transfer verification photos
└── reports/                    # Generated reports
```

### Storage Rules

Create `firebase/storage.rules`:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isOwner(userId) {
      return request.auth.uid == userId;
    }
    
    // User profile images
    match /users/{userId}/profile/{fileName} {
      allow read: if true; // Public read
      allow write: if isAuthenticated() && isOwner(userId);
    }
    
    // Product images
    match /products/{productId}/{fileName} {
      allow read: if true; // Public marketplace
      allow write: if isAuthenticated();
    }
    
    // Transfer verification photos
    match /transfers/{transferId}/{fileName} {
      allow read: if isAuthenticated();
      allow write: if isAuthenticated();
    }
    
    // Default deny
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

---

## Cloud Functions

### Setup

1. Navigate to `functions/` directory
2. Install dependencies:
   ```bash
   cd functions
   npm install
   ```

3. Configure environment:
   ```bash
   firebase functions:config:set maps.api_key="YOUR_MAPS_API_KEY"
   ```

### Key Functions

**Transfer Initiation** (`functions/src/transfers/initiateTransfer.ts`):
- Validates transfer request
- Checks ownership
- Rate limiting
- Creates transfer document

**Transfer Status Change** (`functions/src/transfers/onTransferStatusChange.ts`):
- Triggers on status update
- Updates fowl ownership
- Sends notifications

**User Creation** (`functions/src/auth/onUserCreate.ts`):
- Initializes user document
- Sets up default preferences
- Sends welcome notification

### Deployment

```bash
# Deploy all functions
firebase deploy --only functions

# Deploy specific function
firebase deploy --only functions:initiateTransfer
```

---

## Firebase Cloud Messaging (FCM)

### Setup

1. Firebase Console → Cloud Messaging
2. Note your **Server Key** (for backend)
3. Add to Android app (already configured via `google-services.json`)

### Android Integration

Already configured in `app/src/main/AndroidManifest.xml`:

```xml
<service
    android:name=".AppFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

### Notification Channels

Defined in app code:
- `transfers`: Transfer updates
- `orders`: Order status
- `messages`: Chat messages
- `alerts`: Farm monitoring alerts

### Testing

Send test notification:
1. Firebase Console → Cloud Messaging
2. Click "Send your first message"
3. Enter notification title and text
4. Select target app
5. Send test message

---

## Security Rules

### Firestore Security Rules

ROSTRY uses comprehensive security rules split by feature:

**Main Rules**: [`firebase/firestore.rules`](../firebase/firestore.rules)
- User authentication and authorization
- Role-based access control (RBAC)
- Data validation
- Owner-based permissions

**Key Principles**:
1. **Authentication Required**: Most operations require `request.auth != null`
2. **Owner-Based Access**: Users can only modify their own data
3. **Role-Based Access**: Certain operations restricted by role
4. **Data Validation**: Validate required fields and data types
5. **Deny by Default**: Explicit deny for unmatched paths

**Helper Functions**:
```javascript
function isAuthenticated() {
  return request.auth != null;
}

function isOwner(userId) {
  return request.auth.uid == userId;
}

function hasRole(role) {
  return request.auth.token.role == role;
}

function isFarmer() {
  return hasRole('FARMER');
}

function isEnthusiast() {
  return hasRole('ENTHUSIAST') ||
    (request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ENTHUSIAST');
}
```

### Deploy Security Rules

```bash
# Deploy Firestore rules
firebase deploy --only firestore:rules

# Validate rules before deployment
firebase deploy --only firestore:rules --dry-run
```

### Test Security Rules

Use Firebase Emulator:
```bash
firebase emulators:start --only firestore
```

Run rules tests (if configured):
```bash
npm test --prefix functions
```

---

## Firebase Indexes

### What are Indexes?

Firestore requires composite indexes for queries with:
- Multiple equality filters
- Inequality filters with orderBy
- Range filters with orderBy

### Index Files

- **Main Indexes**: [`firebase/firestore.indexes.json`](../firebase/firestore.indexes.json)
- **Enthusiast Indexes**: [`firebase/firestore_enthusiast_indexes.json`](../firebase/firestore_enthusiast_indexes.json)
- **Farm Indexes**: [`firebase/firestore_farm_indexes.json`](../firebase/firestore_farm_indexes.json)

### Create Indexes

**Method 1: Firebase Console**
1. Run query in app that needs index
2. Check Logcat for index creation link
3. Click link to auto-generate index

**Method 2: Manual Creation**
1. Firebase Console → Firestore → Indexes
2. Click "Create index"
3. Configure collection, fields, and sort orders

**Method 3: Deploy from File**
```bash
firebase deploy --only firestore:indexes
```

### Example Index

```json
{
  "indexes": [
    {
      "collectionGroup": "products",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "sellerId", "order": "ASCENDING" },
        { "fieldPath": "isActive", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    }
  ]
}
```

### Monitor Indexes

- Check index status in Firebase Console
- Monitor query performance
- Remove unused indexes to reduce costs

---

## Environment Configuration

### Development Environment

**local.properties**:
```properties
MAPS_API_KEY=AIzaSy_DEV_KEY_XXXXXXXXXX
FIREBASE_PROJECT_ID=rostry-dev
```

**firebase.json** (for emulators):
```json
{
  "emulators": {
    "auth": { "port": 9099 },
    "firestore": { "port": 8080 },
    "functions": { "port": 5001 },
    "storage": { "port": 9199 },
    "ui": { "enabled": true, "port": 4000 }
  }
}
```

### Staging Environment

Separate Firebase project for staging:
- Project ID: `rostry-staging`
- Separate `google-services.json`
- Deploy staging functions separately

### Production Environment

- Project ID: `rostry-prod`
- Strict security rules
- Monitoring and alerts enabled
- Backup strategy in place

---

## Testing with Firebase Emulators

### Start Emulators

```bash
firebase emulators:start
```

Access Emulator UI: http://localhost:4000

### Configure App for Emulators

In your app (debug build only):

```kotlin
// In Application class or debug build
if (BuildConfig.DEBUG) {
    Firebase.firestore.useEmulator("10.0.2.2", 8080)
    Firebase.auth.useEmulator("10.0.2.2", 9099)
    Firebase.storage.useEmulator("10.0.2.2", 9199)
}
```

**Note**: Use `10.0.2.2` for Android Emulator, `localhost` for physical devices on same network

### Seed Test Data

Create seed script in `functions/` or manually via Emulator UI

### Run Tests Against Emulators

```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## Deployment

### Pre-Deployment Checklist

- [ ] Test all rules with emulator
- [ ] Verify indexes are created
- [ ] Update security rules for production
- [ ] Test Cloud Functions locally
- [ ] Review Firebase quotas and limits
- [ ] Backup current configuration

### Deploy Everything

```bash
# Deploy all Firebase resources
firebase deploy
```

### Deploy Specific Resources

```bash
# Security rules only
firebase deploy --only firestore:rules

# Indexes only
firebase deploy --only firestore:indexes

# Cloud Functions
firebase deploy --only functions

# Storage rules
firebase deploy --only storage
```

### Rollback

```bash
# View deployment history
firebase deploy:history

# Rollback to specific version
firebase rollback firestore:rules <deployment-id>
```

---

## Monitoring and Analytics

### Firebase Console Dashboards

1. **Usage Dashboard**: Monitor reads, writes, deletes
2. **Performance**: Track app performance metrics
3. **Crashlytics**: View crash reports
4. **Analytics**: User behavior and engagement

### Set Up Alerts

1. Firebase Console → Alerts
2. Create alert for:
   - High read/write operations
   - Function errors
   - Storage quota exceeded
   - Authentication failures

### Logging

Enable Cloud Functions logging:
```javascript
import * as functions from 'firebase-functions';

functions.logger.info('Transfer initiated', { transferId });
functions.logger.error('Transfer failed', { error });
```

View logs:
```bash
firebase functions:log
```

---

## Troubleshooting

### Common Issues

#### "Missing google-services.json"

**Solution**:
1. Download from Firebase Console
2. Place in `app/` directory
3. Clean and rebuild project

#### "Firestore permissions denied"

**Solution**:
1. Check security rules
2. Verify user is authenticated
3. Check custom claims (if using RBAC)
4. Review Firestore logs in console

#### "Cloud Function timeout"

**Solution**:
1. Increase timeout in function config
2. Optimize function code
3. Check for blocking operations
4. Review function logs

#### "Index required"

**Solution**:
1. Click the index creation link in Logcat
2. Wait 5-10 minutes for index to build
3. Or add index manually to `firestore.indexes.json`
4. For farm-specific indexes, update `firebase/firestore_farm_indexes.json` and run:
   ```bash
   firebase deploy --only firestore:indexes
   ```

#### "Certificate Pinning Failure (SSLPeerUnverifiedException)"

**Solution**:
1. Firebase Storage uses Google's CDN which serves from multiple domains (e.g., `upload.video.google.com`)
2. Certificate pinning is **disabled** for Firebase Storage domains in `HttpModule.kt` to prevent these errors
3. Firestore API endpoints (`firestore.googleapis.com`) are still pinned for security
4. If you encounter this error, ensure you are not manually pinning `firebasestorage.googleapis.com`

#### "Storage CORS errors"

**Solution**:
Configure CORS for Storage bucket:
```bash
gsutil cors set cors.json gs://<your-bucket>
```

`cors.json`:
```json
[
  {
    "origin": ["*"],
    "method": ["GET"],
    "maxAgeSeconds": 3600
  }
]
```

---

## Best Practices

### Security

1. **Never hardcode credentials**
2. **Use security rules, not client-side checks**
3. **Validate all user input on server**
4. **Enable App Check for production**
5. **Rotate API keys regularly**
6. **Monitor authentication logs**

### Performance

1. **Use indexes for all queries**
2. **Batch operations when possible**
3. **Implement pagination for large datasets**
4. **Cache frequently accessed data locally**
5. **Optimize Cloud Function cold starts**
6. **Monitor Firestore read/write counts**

### Cost Optimization

1. **Use indexes efficiently (remove unused)**
2. **Implement client-side caching**
3. **Use Realtime Database for simple data**
4. **Optimize Cloud Function execution time**
5. **Set up billing alerts**
6. **Monitor usage regularly**

### Development Workflow

1. **Use emulators for local development**
2. **Separate dev/staging/prod projects**
3. **Version control Firebase configuration**
4. **Automated testing with emulators**
5. **Code review for security rules**
6. **Document all Cloud Functions**

---

## Related Documentation

- [SECURITY.md](../SECURITY.md) - Security policy and practices
- [Security & Encryption Guide](security-encryption.md) - Application security
- [API Keys Setup](api-keys-setup.md) - API key management
- [CI/CD Pipeline](ci-cd.md) - Deployment automation
- [Architecture](architecture.md) - System architecture

---

## External Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firestore Security Rules Guide](https://firebase.google.com/docs/firestore/security/get-started)
- [Cloud Functions for Firebase](https://firebase.google.com/docs/functions)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)

---

## Support

For Firebase-related issues:
- Check [troubleshooting.md](troubleshooting.md)
- Review [Firebase Status Dashboard](https://status.firebase.google.com/)
- Firebase Support (Blaze plan required)
- Stack Overflow tag: `firebase`

---

**Firebase project setup is critical for ROSTRY functionality. Follow this guide carefully and keep configuration secure!** 🔥
