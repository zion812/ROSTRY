# CI/CD Pipeline Documentation

**Version:** 1.0  
**Last Updated:** 2025-01-15  
**Audience:** Developers, DevOps

---

## Table of Contents

- [Overview](#overview)
- [GitHub Actions Workflows](#github-actions-workflows)
- [Build Configuration](#build-configuration)
- [Testing Pipeline](#testing-pipeline)
- [Code Quality Checks](#code-quality-checks)
- [Deployment Strategies](#deployment-strategies)
- [Secrets Management](#secrets-management)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

---

## Overview

ROSTRY uses **GitHub Actions** for Continuous Integration and Continuous Deployment. The pipeline automates building, testing, code quality checks, and deployment.

### CI/CD Goals

- Automate repetitive tasks
- Catch issues early with automated testing
- Ensure code quality with linting and static analysis
- Fast feedback (<15 minutes pipeline execution)
- Safe deployments with automated testing
- Reproducible builds

---

## GitHub Actions Workflows

### Pull Request Workflow

**File**: `.github/workflows/pr-check.yml`

```yaml
name: Pull Request Checks
on:
  pull_request:
    branches: [ main, develop ]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'
      - run: chmod +x gradlew
      - run: ./gradlew ktlintCheck
      - run: ./gradlew detekt
  
  build:
    runs-on: ubuntu-latest
    needs: lint
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'
      - name: Create local.properties
        run: echo "MAPS_API_KEY=${{ secrets.MAPS_API_KEY }}" > local.properties
      - run: ./gradlew assembleDebug
  
  test:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'
      - name: Create local.properties
        run: echo "MAPS_API_KEY=${{ secrets.MAPS_API_KEY }}" > local.properties
      - run: ./gradlew test
      - uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: app/build/test-results/
```

### Main Branch Workflow

**File**: `.github/workflows/main.yml`

```yaml
name: Main Branch CI/CD
on:
  push:
    branches: [ main ]
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'
      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore
      - name: Build release APK
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      - name: Deploy to Firebase
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          token: ${{ secrets.FIREBASE_TOKEN }}
          groups: internal-testers
          file: app/build/outputs/apk/release/app-release.apk
```

---

## Build Configuration

### Gradle Signing

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### Build Variants

- **Debug**: Development with debugging
- **Staging**: Pre-production testing
- **Release**: Production builds with signing

---

## Testing Pipeline

### Unit Tests

```bash
./gradlew test
```

### Instrumentation Tests

```yaml
- name: Run instrumentation tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 33
    script: ./gradlew connectedAndroidTest
```

### Code Coverage

```bash
./gradlew jacocoTestReport
```

---

## Code Quality Checks

### Ktlint

```bash
./gradlew ktlintCheck
./gradlew ktlintFormat  # Auto-fix
```

### Detekt

```bash
./gradlew detekt
```

### Android Lint

```bash
./gradlew lint
```

---

## Deployment Strategies

### Firebase App Distribution

**Use Cases**: Internal testing, beta releases

```yaml
- uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    token: ${{ secrets.FIREBASE_TOKEN }}
    groups: internal-testers
    file: app/build/outputs/apk/release/app-release.apk
```

### Google Play Console

**Tracks**:
- Internal (< 100 testers)
- Alpha (invited testers)
- Beta (public testing)
- Production (general availability)

```yaml
- uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
    packageName: com.rio.rostry
    releaseFiles: app/build/outputs/bundle/release/app-release.aab
    track: internal
```

---

## Secrets Management

### Required Secrets

| Secret | Purpose |
|--------|---------|
| `MAPS_API_KEY` | Google Maps API key |
| `FIREBASE_APP_ID` | Firebase app ID |
| `FIREBASE_TOKEN` | Firebase CI token |
| `KEYSTORE_BASE64` | Base64 encoded keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Signing key alias |
| `KEY_PASSWORD` | Key password |
| `PLAY_SERVICE_ACCOUNT_JSON` | Play Console service account |

### Adding Secrets

1. Go to repository Settings → Secrets → Actions
2. Click "New repository secret"
3. Add name and value

### Encoding Keystore

```bash
base64 -i release.keystore -o keystore.base64
```

---

## Monitoring

### Slack Notifications

```yaml
- name: Notify Slack
  if: always()
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "Build ${{ job.status }} for ${{ github.repository }}"
      }
```

### Email Notifications

GitHub automatically emails workflow failures to repository watchers.

---

## Troubleshooting

### Common Issues

**Build fails with "Permission denied"**:
```yaml
- run: chmod +x gradlew
```

**Keystore not found**:
- Verify `KEYSTORE_BASE64` secret is set
- Check base64 encoding

**Tests timeout**:
```yaml
- run: ./gradlew test
  timeout-minutes: 30
```

### Debug Logging

```yaml
- run: echo "ACTIONS_STEP_DEBUG=true" >> $GITHUB_ENV
```

---

## Best Practices

### 1. Cache Dependencies

```yaml
- uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
```

### 2. Parallel Jobs

```yaml
jobs:
  lint:
    runs-on: ubuntu-latest
  test:
    runs-on: ubuntu-latest
  build:
    needs: [lint, test]
```

### 3. Conditional Deployment

```yaml
- name: Deploy
  if: github.ref == 'refs/heads/main'
  run: ./deploy.sh
```

### 4. Matrix Builds

```yaml
strategy:
  matrix:
    api-level: [28, 29, 33]
```

---

## Related Documentation

- [Deployment](deployment.md) - Deployment procedures
- [Testing Strategy](testing-strategy.md) - Testing approach
- [Firebase Setup](firebase-setup.md) - Firebase configuration
- [Database Migrations](database-migrations.md) - Database versioning
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines

---

**Automated CI/CD ensures consistent quality and safe deployments!** 🚀
