# Maps and Places Integration

This document describes the integration of Google Maps SDK for Android, the Places SDK (New), and Firebase App Check Debug Provider in the ROSTRY app.

## Setup Instructions

- Obtain a Google Maps Platform API key with the following APIs enabled:
  - Maps SDK for Android
  - Places API (New)
- Put your key in `local.properties` as `MAPS_API_KEY=YOUR_KEY` or `MAPS_JS_API_KEY=YOUR_JS_KEY`. The build uses `BuildConfig.MAPS_API_KEY` (or `MAPS_JS_API_KEY` for WebViews).
- `app/build.gradle.kts` handles reading these properties and injecting them into `BuildConfig` and Manifest.
- Ensure `MAPS_API_KEY` is configured for both debug and release builds to avoid build errors.

## Firebase App Check (Debug Provider)

- App Check is initialized in `RostryApp.onCreate()` using the Debug provider for development builds.
- Steps:
  - Run the app in debug.
  - Grab the debug token from Logcat (printed at first App Check usage or by SDK logs) and register it in Firebase Console > App Check > Debug tokens.
  - Start with Monitoring mode, then switch to Enforce once stable.

## Manifest & Config

- `AndroidManifest.xml` contains:
  - `<uses-permission android:name="android.permission.INTERNET"/>`
  - `<meta-data android:name="com.google.android.geo.API_KEY" android:value="${MAPS_API_KEY}"/>`

## Initialization

- `RostryApp.kt` performs:
  - `FirebaseApp.initializeApp(this)`
  - App Check Debug provider installation for debug builds
  - Places SDK initialization using `Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.MAPS_API_KEY)`

## DI & Services

- `di/LocationModule.kt` provides:
  - `PlacesClient`
  - `LocationService` (fallback/basic logic)
  - `LocationSearchService` (Places-powered operations)
- `marketplace/location/LocationService.kt` retains legacy behavior and adds fallback utilities.
- `marketplace/location/LocationSearchService.kt` exposes:
  - `autocomplete(query)`
  - `getPlaceDetails(placeId)`
  - `withinRadiusWithFallback(...)`
- `utils/location/PlacesUtils.kt` contains conversions, formatting, error handling and debug hints.

## Usage Guidelines

- Prefer Places-powered flows when available. If a call fails (e.g., App Check not accepted), gracefully fall back to basic coordinate flows.
- Validate delivery coverage using `LocationService.withinRadius(...)` or `LocationSearchService.withinRadiusWithFallback(...)`.

## Development Workflow

- Test on debug builds with the Debug provider.
- Monitor App Check metrics in Firebase Console.
- Restrict the API key to your app’s SHA-1/SHAs and Android app package where possible.

## Production Considerations

- Replace the Debug provider with Play Integrity provider when distributing via Play Store, or a custom provider for other channels.
- Enforce App Check after validating monitoring data.
- Keep your API key secure and restricted.

## Troubleshooting

- Places calls fail with PERMISSION_DENIED:
  - Ensure App Check token is registered and enforcement is set appropriately.
  - Verify API key restrictions include your package/SHA and the Places API is enabled.
- SDK initialization errors:
  - Check that `MAPS_API_KEY` is correctly injected to `BuildConfig` and manifest meta-data.
- Zero suggestions from Autocomplete:
  - Verify network, region restrictions, and query.

