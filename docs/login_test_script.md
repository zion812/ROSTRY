# ROSTRY Login Test Script

This script will help you test the login flow and identify any issues.

## Prerequisites
1. Android Studio with the ROSTRY project open
2. An Android emulator or device connected
3. Internet connection

## Test Steps

### 1. Verify Build
```bash
# Navigate to project directory
cd C:\\Users\\rowdy\\AndroidStudioProjects\\ROSTRY

# Build the project
gradlew.bat assembleDebug
```

### 2. Install the App
```bash
# Install the debug APK
adb install app\\build\\outputs\\apk\\debug\\app-debug.apk
```

### 3. Test Firebase Connection
1. Open the app drawer on your device
2. Launch "Firebase Test" activity
3. Tap "Test Firebase Connection"
4. Verify that the test succeeds

### 4. Test Account Creation
1. Launch the main ROSTRY app
2. Tap "Don't have an account? Sign Up"
3. Fill in the form with:
   - Name: Test User
   - Email: test@example.com
   - Password: password123
4. Tap "Sign Up"
5. Verify that you see a success message or are redirected to profile setup

### 5. Test Login
1. After signing up, or if you already have an account:
2. Enter your email and password
3. Tap "Login"
4. Observe if:
   - Loading indicator appears
   - You are redirected to the home screen
   - Any error messages appear

### 6. Check Logs
1. In Android Studio, open the Logcat tab
2. Filter by "ROSTRY" or "DebugAuth"
3. Look for any error messages during the login process

## Common Issues and Solutions

### Issue: "Invalid email or password"
Solution: 
- Double-check your credentials
- Try creating a new account
- Check Firebase Authentication console to verify the account exists

### Issue: Stuck on loading screen
Solution:
- Check Logcat for errors
- Verify internet connectivity
- Try force closing and reopening the app

### Issue: No navigation after successful login
Solution:
- Check that AuthViewModel's isLoggedIn state is updating
- Verify navigation logic in RostryNavGraph

## Need Help?
If you're still experiencing issues:
1. Capture the Logcat output during the failed login attempt
2. Note the exact error message you see
3. Document the steps you took leading up to the issue
4. Share this information with the development team