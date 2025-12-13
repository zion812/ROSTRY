# API Keys Setup Guide

**Version:** 1.0  
**Last Updated:** 2025-01-15  
**Audience:** Developers

---

## Overview

ROSTRY uses external APIs that require API keys. This guide explains how to securely configure and manage API keys for development and production.

## ⚠️ Security First

**CRITICAL RULES**:
- ❌ **NEVER** commit API keys to version control
- ❌ **NEVER** hardcode API keys in source code
- ❌ **NEVER** share API keys in public channels
- ✅ **ALWAYS** store keys in `local.properties` (gitignored)
- ✅ **ALWAYS** use separate keys for dev/staging/production
- ✅ **ALWAYS** restrict keys with package name and SHA-1 fingerprint

---

## Quick Start

### 1. Copy Template

```bash
cp local.properties.template local.properties
```

### 2. Add Your Keys

Edit `local.properties` and add:

```properties
MAPS_API_KEY=your_actual_maps_api_key_here
```

### 3. Verify .gitignore

Ensure `local.properties` is in `.gitignore` (already configured):

```gitignore
/local.properties
local.properties
```

---

## Google Maps Platform API Key

### Purpose

The Maps API key is used for:
- **Google Maps SDK**: Display maps with location features
- **Google Places API**: Location autocomplete and place search
- **Geocoding**: Convert addresses to coordinates

### Getting Your Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Enable APIs:
   - Maps SDK for Android
   - Places API
4. Navigate to **Credentials** → **Create Credentials** → **API Key**
5. Copy your API key

### Key Restrictions (IMPORTANT!)

**Application Restrictions**:
1. Select "Android apps"
2. Add package name: `com.rio.rostry`
3. Add SHA-1 certificate fingerprint:

**Get Debug SHA-1**:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Get Release SHA-1**:
```bash
keytool -list -v -keystore /path/to/your/release.keystore -alias your_key_alias
```

**API Restrictions**:
- Restrict key to selected APIs only
- Enable: Maps SDK for Android, Places API
- Disable all other APIs

### Configuration

**In local.properties**:
```properties
MAPS_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
MAPS_JS_API_KEY=AIzaSyYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY # Optional, for WebViews
```

**How it works**:

1. **Gradle** reads keys from `local.properties`.
   - Release builds will fails with a clear error if `MAPS_API_KEY` is missing or matches a placeholder.
   - `MAPS_JS_API_KEY` falls back to `MAPS_API_KEY` if not explicitly set.
   
```kotlin
   // In app/build.gradle.kts
   val mapsApiKeyProvider = providers.gradleProperty("MAPS_API_KEY")

   buildTypes {
       release {
           val releaseKey = mapsApiKeyProvider.orNull
               ?: throw GradleException("MAPS_API_KEY is not set. Add it to local.properties for release builds.")
           if (releaseKey == "<set me>" || releaseKey == "your_api_key_here" || releaseKey == "debug-placeholder") {
               throw GradleException("MAPS_API_KEY is a placeholder. Configure a real key in local.properties for release builds.")
           }
           buildConfigField("String", "MAPS_API_KEY", "\"$releaseKey\"")
           manifestPlaceholders["MAPS_API_KEY"] = releaseKey
       }
       debug {
           val debugKey = mapsApiKeyProvider.orElse("debug-placeholder").get()
           buildConfigField("String", "MAPS_API_KEY", "\"$debugKey\"")
           manifestPlaceholders["MAPS_API_KEY"] = debugKey
       }
   }
   ```

2. **BuildConfig** makes it available in code:
   ```kotlin
   val apiKey = BuildConfig.MAPS_API_KEY
   ```

3. **AndroidManifest** receives it via placeholder:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

### Usage in Code

Access the key via BuildConfig (already configured):

```kotlin
// DON'T access directly in most cases - it's injected automatically
val mapsApiKey = BuildConfig.MAPS_API_KEY
```

Maps SDK reads the key automatically from AndroidManifest.

---

## Environment-Specific Keys

### Development

Use a restricted key for development:
- Restrict to debug SHA-1 fingerprint
- Lower quota limits acceptable
- Separate from production key

**local.properties**:
```properties
MAPS_API_KEY=AIzaSy_DEV_KEY_XXXXXXXXXXXXXXXXX
```

### Staging

Use a separate key for staging environment:
```properties
MAPS_API_KEY=AIzaSy_STAGING_KEY_XXXXXXXXXX
```

### Production

Use a highly restricted production key:
- Strict package name restriction
- Release SHA-1 fingerprint only
- Billing alerts enabled
- Regular rotation schedule

```properties
MAPS_API_KEY=AIzaSy_PROD_KEY_XXXXXXXXXX
```

---

## Security Best Practices

### 1. API Key Restrictions

✅ **Always configure**:
- Application restrictions (package + SHA-1)
- API restrictions (only required APIs)
- Billing alerts and quotas

### 2. Key Rotation

Rotate keys periodically:
- Development: Every 6 months
- Production: Every 3 months or after security incident
- Immediately after: suspected compromise, team member departure

### 3. Monitoring

Monitor API usage:
- Set up billing alerts
- Review usage reports weekly
- Investigate unusual spikes
- Track errors and failed requests

### 4. Separate Keys Per Environment

Never reuse keys across environments:
- Development key ≠ Production key
- Each environment has its own restrictions
- Easier to track issues and costs

---

## Troubleshooting

### Error: "API key not set"

**Symptom**: Maps don't load, places autocomplete fails

**Solution**:
1. Verify `local.properties` exists
2. Check `MAPS_API_KEY` is set correctly
3. Clean and rebuild: `./gradlew clean build`
4. Invalidate caches: File → Invalidate Caches / Restart

### Error: "This API project is not authorized"

**Symptom**: API calls return 403 errors

**Solution**:
1. Verify API is enabled in Google Cloud Console
2. Check API key restrictions match your app
3. Verify SHA-1 fingerprint is correct
4. Wait 5 minutes for changes to propagate

### Error: "The provided API key is expired"

**Solution**:
1. Generate new API key in Google Cloud Console
2. Update `local.properties` with new key
3. Rebuild project

### Maps show "For development purposes only" watermark

**Symptom**: Watermark on production maps

**Cause**: Using an unrestricted or improperly configured key

**Solution**:
1. Add billing to Google Cloud project
2. Restrict key properly (package + SHA-1)
3. Enable required APIs

---

## CI/CD Integration

### GitHub Actions

Store secrets in repository settings:

```yaml
# .github/workflows/build.yml
- name: Create local.properties
  run: |
    echo "MAPS_API_KEY=${{ secrets.MAPS_API_KEY }}" > local.properties
```

**Never log secrets**:
```yaml
# ❌ DON'T DO THIS
- run: cat local.properties

# ✅ DO THIS
- run: echo "local.properties configured"
```

### Firebase App Distribution / Play Console

Use environment-specific keys:
- Internal testing: Development key
- Alpha/Beta: Staging key  
- Production: Production key

Configure via Build Variants if needed.

---

## Adding New API Keys

### 1. Add to Template

Edit `local.properties.template`:
```properties
NEW_API_KEY=your_new_api_key_here
```

### 2. Configure Gradle

Edit `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "NEW_API_KEY", 
    "\"${project.findProperty("NEW_API_KEY") ?: "default_value"}\"")
```

### 3. Update Documentation

Add to this guide and `SECURITY.md`.

### 4. Update .gitignore

Verify `.gitignore` covers `local.properties`.

---

## Checklist

### Initial Setup
- [ ] Copy `local.properties.template` to `local.properties`
- [ ] Add all required API keys
- [ ] Verify `local.properties` is in `.gitignore`
- [ ] Test app builds and runs
- [ ] Verify Maps and Places features work

### Key Configuration
- [ ] Create separate keys for dev/staging/production
- [ ] Restrict all keys with package name
- [ ] Add SHA-1 fingerprints for all keys
- [ ] Enable only required APIs
- [ ] Set up billing alerts

### Security
- [ ] Never commit keys to version control
- [ ] Document key rotation schedule
- [ ] Set up usage monitoring
- [ ] Configure CI/CD secrets properly
- [ ] Review access logs regularly

---

## Related Documentation

- [SECURITY.md](../SECURITY.md) - Security policy and practices
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
- [README.md](../README.md) - Project setup
- [Google Maps Platform Documentation](https://developers.google.com/maps/documentation)
- [Android Security Best Practices](https://developer.android.com/training/articles/security-tips)

---

## Support

For help with API keys:
- Check [troubleshooting.md](troubleshooting.md)
- Review [Google Maps Platform docs](https://developers.google.com/maps/documentation)
- Ask in [GitHub Discussions](../../discussions)
- Contact: security@rostry.example.com

---

**Remember: API keys are sensitive credentials. Treat them like passwords!** 🔐
