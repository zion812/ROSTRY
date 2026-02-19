# Task: Enhance Farmer Upgrade with Location Verification

## Objective
Fix the "abnormal" gap in the General-to-Farmer upgrade flow where the "Farm Location" is only captured as text, leaving the geospatial fields (`farmLocationLat`, `farmLocationLng`) null and the location unverified.

## Current State Analysis
- **UI (`UpgradeFarmerSheet`)**: Asks for "Address" (Text), Chicken Count, Type, Year. No GPS/Map integration.
- **ViewModel (`GeneralProfileViewModel`)**: `upgradeToFarmer` function does not accept or process coordinates.
- **Database (`UserEntity`)**: Has fields `farmLocationLat`, `farmLocationLng`, `locationVerified`, but they remain unused during upgrade.

## Status: COMPLETE

## Completed Work
1. **ViewModel (`GeneralProfileViewModel`)**:
   - `upgradeToFarmer` function updated to accept `farmLat: Double?` and `farmLng: Double?`.
   - `UserEntity` update logic modified to persist `farmLocationLat`, `farmLocationLng` and set `locationVerified` to true if coordinates are provided.
   - Comprehensive error handling and logging added.

2. **UI (`UpgradeFarmerSheet`)**:
   - Implemented `FusedLocationProviderClient` integration.
   - Added permission request for `ACCESS_FINE_LOCATION`.
   - Added UI controls (Icon Button) to capture current location.
   - Displays captured coordinates or "Tap to verify" prompt.
   - Validates form and passes coordinates to `onUpgrade`.

3. **Navigation (`GeneralProfileRoute`)**:
   - Updated `UpgradeFarmerSheet` callback signature to include `lat` and `lng`.
   - Passes coordinates correctly from UI to ViewModel.

4. **Dependencies & Permissions**:
   - Verified `play-services-location` dependency in `build.gradle.kts`.
   - Verified `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions in `AndroidManifest.xml`.

## Verification check
- `./gradlew assembleDebug` passed successfully.
- Code review confirms logical flow from UI -> ViewModel -> Repository -> Database.
