# ROSTRY Login Troubleshooting Guide

## Common Login Issues and Solutions

### 1. Firebase Configuration Issues

**Problem**: Firebase is not properly configured
**Solution**: 
- Ensure `google-services.json` is in the `app/` directory
- Verify that the package name in `google-services.json` matches your app's package name (`com.rio.rostry`)
- Check that Firebase dependencies are correctly added in `build.gradle` files

### 2. Network Connectivity Issues

**Problem**: App cannot connect to Firebase services
**Solution**:
- Ensure device has internet connectivity
- Check if firewall or proxy is blocking Firebase connections
- Verify that Firebase services are not blocked in your region

### 3. Authentication State Not Updating

**Problem**: User logs in but app doesn't navigate to home screen
**Solution**:
- Check that the `isLoggedIn` state in `AuthViewModel` is properly updated
- Verify that the navigation logic in `RostryNavGraph` is correct
- Ensure that `LaunchedEffect` in the home screen is properly fetching fowls

### 4. Invalid Credentials

**Problem**: "Invalid email or password" error
**Solution**:
- Verify that you're using the correct email and password
- Check if the user account exists in Firebase Authentication
- Ensure that email/password authentication is enabled in Firebase Console

### 5. Firestore Permissions

**Problem**: User can log in but cannot access data
**Solution**:
- Check Firestore security rules in `firestore.rules`
- Ensure that the user has proper permissions to read/write data
- Verify that the user's UID matches the document owner

## Debugging Tools

### Firebase Test Activity

To test Firebase connectivity:
1. Install the app
2. Open the app drawer
3. Launch "Firebase Test" activity
4. Tap "Test Firebase Connection"
5. Check the results

### Debug Login Screen

To use the debug login screen:
1. Modify `RostryNavGraph.kt` to use `debug_login` as the start destination:
   ```kotlin
   startDestination = if (isLoggedIn) "home" else "debug_login"
   ```
2. This will show detailed logs during the login process

## Logcat Debugging

To view detailed logs during login:
1. Open Android Studio
2. Go to Logcat tab
3. Filter by "ROSTRY" or "DebugAuth"
4. Look for error messages or unexpected behavior

## Common Error Messages

### "FirebaseApp is not initialized"
**Cause**: Firebase was not properly initialized
**Solution**: Ensure `FirebaseApp.initializeApp(this)` is called in `ROSTRYApplication` and `MainActivity`

### "Invalid credentials" or "User not found"
**Cause**: Incorrect email/password or user doesn't exist
**Solution**: Verify credentials or create a new account

### "Network error"
**Cause**: No internet connection or Firebase services unreachable
**Solution**: Check internet connection and try again

## Testing Steps

1. **Verify Firebase Setup**:
   - Launch Firebase Test Activity
   - Run connection test

2. **Test Account Creation**:
   - Use SignUp screen to create a new account
   - Check Firebase Authentication console to verify account creation

3. **Test Login**:
   - Use the credentials from step 2 to log in
   - Check if navigation to home screen occurs

4. **Check Data Access**:
   - Add a fowl to verify Firestore access
   - Check Firestore console to verify data was saved

## Contact Support

If issues persist:
1. Capture Logcat output during the issue
2. Note the exact error message
3. Document steps to reproduce the issue
4. Contact the development team with this information