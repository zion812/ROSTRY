# Farmer Location Verification System Upgrade Plan

## 1. Reliability Improvements (Priority High)
The current system suffers from potential data loss during process death (e.g., taking a photo) due to asynchronous state persistence and complex JSON serialization.

### 1.1 Atomic State Management
- **Current Issue:** State is saved asynchronously via `persistState` collecting a flow. Use input/photo capture can kill the app *before* the flow collection triggers.
- **Fix:** Implement a `updateState` helper function in `VerificationViewModel` that synchronously updates both the in-memory `StateFlow` and the `SavedStateHandle`.

### 1.2 Type-Safe Persistence
- **Current Issue:** `Place` objects and Lists are manually serialized to JSON strings using Gson. This is brittle and prone to parsing errors.
- **Fix:** Create a `VerificationUiState` data class that implements `Parcelable`.
    - Create a lightweight `FarmLocation` Parcelable class (lat, lng, address string) instead of storing the heavy Google `Place` object.
    - Store the entire `VerificationUiState` directly in `SavedStateHandle` using `@Parcelize`.

### 1.3 Upload Robustness
- **Current Issue:** Uploads are "fire and forget" via `MediaUploadManager`.
- **Fix:** Ensure the `MediaUploadManager` queue persistence is robust (already verified in other tasks, but good to double-check integration).

## 2. Scalability & Flexibility (Priority Medium)

### 2.1 Decoupling Requirements
- **Current Issue:** Verification requirements (e.g., "Land Record", "Selfie") are hardcoded in the `UpgradeType` enum. Changing requirements requires an app update.
- **Fix:** Introduce a `VerificationRequirementProvider` interface.
    - Default implementation reads from `UpgradeType`.
    - Future implementation can read from Firebase Remote Config or a backend API.
    - This allows dynamic adjustment of verification criteria per region or user segment without app releases.

### 2.2 Dynamic UI Rendering
- **Current Issue:** The UI explicitly lists "Photos" and "Documents".
- **Fix:** Render the upload UI dynamically based on the list of requirements from the Provider.
    - Each requirement should define its own label, accepted mime-types, and max size.

## 3. UI/UX Refinements

### 3.1 Hardcoded Strings
- **Current Issue:** Labels like "Verification Request" and "Farm Location" are hardcoded.
- **Fix:** Extract all user-facing strings to `strings.xml`.

### 3.2 Feedback Loops
- **Improvement:** improving error messages for specific failure cases (e.g. "GPS location mismatch with Photo location").

---

## Execution Steps

1.  **Create Data Models**: Define `FarmLocation` (Parcelable) and `VerificationUiState` (Parcelable).
2.  **Refactor ViewModel**: Rewrite `VerificationViewModel` to use the new Parcelable state and atomic updates. Remove Gson usage.
3.  **Refactor Screen**: Update `FarmerLocationVerificationScreen` to consume the new state structure.
