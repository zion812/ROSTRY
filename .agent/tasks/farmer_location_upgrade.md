# Task: Enhance Farmer Upgrade with Location Verification

## Objective
Fix the "abnormal" gap in the General-to-Farmer upgrade flow where the "Farm Location" is only captured as text, leaving the geospatial fields (`farmLocationLat`, `farmLocationLng`) null and the location unverified.

## Current State Analysis
- **UI (`UpgradeFarmerSheet`)**: Asks for "Address" (Text), Chicken Count, Type, Year. No GPS/Map integration.
- **ViewModel (`GeneralProfileViewModel`)**: `upgradeToFarmer` function does not accept or process coordinates.
- **Database (`UserEntity`)**: Has fields `farmLocationLat`, `farmLocationLng`, `locationVerified`, but they remain unused during upgrade.

## Proposed "Reliable & Scalable" Flow
1. **User Action**: User opens "Become a Farmer" sheet.
2. **Location Capture**:
    - User types address (for display).
    - User clicks "Verify Location" / "Use Current Location".
    - App requests `ACCESS_FINE_LOCATION`.
    - App captures current High-Accuracy GPS coordinates.
    - App displays "Location Captured: [Lat, Lng]" or a green checkmark.
3. **Submission**:
    - App sends Address Text + Lat + Lng to ViewModel.
4. **Processing**:
    - ViewModel saves all fields to `UserEntity`.
    - `locationVerified` flag can be set to `true` (since we captured it via GPS) or `false` (pending admin check), but initially capturing it is the critical step.

## Implementation Steps

### Step 1: Update Logic (ViewModel)
- Modify `GeneralProfileViewModel.upgradeToFarmer` signature to accept `farmLat: Double?` and `farmLng: Double?`.
- Update the `UserEntity` copy mechanism to include these values.

### Step 2: Update UI (UpgradeFarmerSheet)
- Add State for `lat`, `lng`.
- Add Permission Launcher for Location.
- Add "Get Location" Button (using `FusedLocationProviderClient`).
- Pass new values to `onUpgrade`.

### Step 3: Verification
- Verify `UserEntity` updates in Firestore/Room via logging.

## Scalability Note
This establishes a "Truth" for the farm's location, enabling features like:
- "Nearby Farms" for consumers.
- Geofencing for logistics.
- "Verified Location" badges.
