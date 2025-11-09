# 🔧 Firebase Phone Authentication Setup Guide

## 🚨 Error 17020 - SMS Verification Failed

This guide will help you fix the Firebase Phone Authentication error code **17020** that's causing the "Sending..." to hang.

---

## ✅ **Solution Steps**

### **Step 1: Add SHA-1 Fingerprint to Firebase Console** ⭐

This is the **most common fix** for error 17020.

#### **Your Debug SHA-1 Fingerprint:**
```
SHA1: EF:08:85:E8:07:B8:1C:FD:95:B2:47:5B:A0:FA:EE:09:E4:97:19:D7
SHA-256: C8:FB:8F:7A:83:DD:26:EF:4E:04:F5:1F:E4:BC:E6:91:46:EC:4B:B3:B3:C6:01:A2:5B:39:93:FF:C0:14:C7:3A
```

#### **How to Add to Firebase:**

1. **Open Firebase Console**
   - Go to https://console.firebase.google.com/
   - Select your **ROSTRY** project

2. **Navigate to Project Settings**
   - Click the ⚙️ gear icon → **Project Settings**
   - Scroll down to **Your apps** section
   - Find your Android app (`com.rio.rostry`)

3. **Add SHA Certificate Fingerprints**
   - Click **Add fingerprint** button
   - Paste the **SHA-1**: `EF:08:85:E8:07:B8:1C:FD:95:B2:47:5B:A0:FA:EE:09:E4:97:19:D7`
   - Click **Add fingerprint** again
   - Paste the **SHA-256**: `C8:FB:8F:7A:83:DD:26:EF:4E:04:F5:1F:E4:BC:E6:91:46:EC:4B:B3:B3:C6:01:A2:5B:39:93:FF:C0:14:C7:3A`
   - Click **Save**

4. **Download Updated google-services.json**
   - After saving, download the updated `google-services.json`
   - Replace `app/google-services.json` in your project
   - **Rebuild the app**

---

### **Step 2: Enable Phone Authentication in Firebase Console**

1. **Go to Authentication**
   - In Firebase Console → **Authentication** → **Sign-in method**

2. **Enable Phone**
   - Find **Phone** provider
   - Click **Enable**
   - Save

---

### **Step 3: Add Test Phone Numbers (For Development)**

To avoid hitting SMS quotas during development:

1. **Go to Phone Numbers for Testing**
   - Firebase Console → **Authentication** → **Settings** tab
   - Scroll to **Phone numbers for testing**

2. **Add Test Number**
   - Click **Add phone number**
   - Phone number: `+919876543210` (or your test number)
   - Verification code: `123456`
   - Save

Now you can test without sending real SMS!

---

### **Step 4: Check SMS Quota**

Firebase has daily limits for SMS:

- **Free Spark Plan**: 10 SMS/day
- **Paid Blaze Plan**: Based on usage

If you've exceeded the quota:
- Wait 24 hours, OR
- Upgrade to Blaze plan, OR
- Use test phone numbers (Step 3)

---

## 🔍 **Additional Troubleshooting**

### **If Error Persists:**

1. **Clear App Data**
   ```
   Settings → Apps → ROSTRY → Storage → Clear Data
   ```

2. **Reinstall the App**
   ```
   adb uninstall com.rio.rostry
   ./gradlew installDebug
   ```

3. **Check Google Play Services**
   - Make sure Google Play Services is updated on your device
   - Go to Settings → Apps → Google Play Services

4. **Verify Internet Connection**
   - Phone auth requires a stable internet connection
   - Try switching between WiFi and Mobile Data

---

## 📱 **Testing After Setup**

1. **Run the app**
2. Navigate to **Phone Verification screen**
3. Enter test number: `+919876543210`
4. Click **Send OTP**
5. It should either:
   - Auto-fill with `123456` (test mode), OR
   - Send actual SMS (if using real number)

---

## 🛠️ **Code Improvements Applied**

We've also added a **65-second timeout** to prevent infinite loading:

```kotlin
// FirebaseAuthDataSource.kt
private suspend fun awaitVerificationCallbacks(...): AuthResult<VerificationId> = 
    withTimeout(65000) { // Won't hang forever anymore!
        callbackFlow {
            // Firebase callbacks
        }.first()
    }
```

This ensures the loading state clears even if Firebase doesn't respond.

---

## 📊 **Error Code Reference**

| Code  | Meaning                                    | Solution                          |
|-------|--------------------------------------------|-----------------------------------|
| 17020 | SMS auto-retrieval failed                  | Add SHA-1 to Firebase Console     |
| 17010 | Invalid phone number                       | Check phone format (+91...)       |
| 17042 | SMS quota exceeded                         | Wait or use test numbers          |
| 17028 | Invalid verification code                  | Check OTP entry                   |

---

## ✅ **Checklist**

- [ ] SHA-1 added to Firebase Console
- [ ] SHA-256 added to Firebase Console
- [ ] google-services.json updated
- [ ] Phone provider enabled
- [ ] Test phone number added (optional)
- [ ] App rebuilt and reinstalled
- [ ] Tested with test number

---

## 🎯 **Next Steps**

After completing the setup:
1. **Rebuild the app**: `./gradlew clean assembleDebug`
2. **Install**: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
3. **Test phone auth** with your configured test number

The "Sending..." hang should now be fixed, and you'll get proper error messages if something fails!
