# ROSTRY: Developer's Guide & Technical Blueprint

This document provides a comprehensive overview of the ROSTRY Android application, detailing its architecture, components, and implementation. It is intended to serve as a complete guide for developers working on the project.

---

### 1. Getting Started

**1.1. Prerequisites**
- Android Studio (latest stable version)
- Java Development Kit (JDK) 11 or higher
- A Firebase project

**1.2. Project Setup**
1.  Clone the repository from the source control.
2.  Open the project in Android Studio.
3.  **Firebase Configuration**: Follow the steps in the "Firebase Setup" section below to connect the app to your Firebase project.
4.  Build the project. Gradle will automatically download all the required dependencies.
5.  Run the application on an emulator or a physical device.

---

### 2. Navigation and UI Implementation

**2.1. Navigation Architecture**
- Utilizes Jetpack Navigation Compose for in-app navigation
- Implements a nested navigation graph structure with `auth_graph` and `main_graph`
- Handles deep linking and back stack management
- Supports different navigation flows based on user authentication state

**2.2. Authentication Flow**
- Implements a complete authentication system with Firebase Auth
- Handles user registration, login, and profile setup
- Maintains user session state across app restarts
- Validates user input and provides appropriate feedback

**2.3. Screen Implementation Status**
- ✅ Implemented Screens:
  - Fowl Registration
  - Fowl List
  - Fowl Details
  - Fowl Record Creation
  - Login
  - Registration
  - Profile Setup

- ⏳ Placeholder Screens (Basic UI implemented, functionality pending):
  - Profile Screen
  - Settings Screen
  - Farm Dashboard
  - Weather Information

**2.4. UI Components**
- Uses Material 3 design system
- Implements custom theme with app-specific colors and typography
- Reusable UI components in the `ui.components` package
- Responsive layouts that adapt to different screen sizes

### 3. Firebase Setup

This project relies on Firebase for authentication, database, storage, and analytics.

1.  **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2.  **Register Your App**: In the project dashboard, add a new Android app. Use the `applicationId` found in `app/build.gradle.kts` (`com.rio.rostry`).
3.  **Download `google-services.json`**: Download the `google-services.json` configuration file provided by Firebase.
4.  **Add Config File to Project**: Place the downloaded `google-services.json` file in the `app/` directory of the project.
5.  **Enable Firebase Services**:
    *   **Authentication**: Go to the "Authentication" section and enable the "Email/Password" sign-in method.
    *   **Firestore**: Go to the "Firestore Database" section and create a new database. Start in **test mode** for initial development (you should secure it with rules later).
    *   **Storage**: Go to the "Storage" section and set it up.

---

### 3. Technical Stack

(Content from the original blueprint)

---

### 4. Project Structure

(Content from the original blueprint)

---

### 5. Application Architecture (MVVM)

(Content from the original blueprint)

---

### 6. Data Layer Deep Dive

(Content from the original blueprint)

---

### 7. Dependency Injection (Hilt)

(Content from the original blueprint)

---

### 8. UI Layer and State Management

(Content from the original blueprint)

---

### 9. Coding Style and Conventions

*   **Language**: Follow the official [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).
*   **Compose**: Adhere to the best practices outlined in the [Jetpack Compose documentation](https://developer.android.com/jetpack/compose/documentation).
*   **Naming**: 
    *   `ViewModels` should be named after the screen they serve, suffixed with `ViewModel` (e.g., `FowlListViewModel`).
    *   `Composable` screens should be suffixed with `Screen` (e.g., `FowlListScreen`).
*   **Logging**: Use the `Timber` library for all logging. Avoid using `android.util.Log` directly.

---

### 10. Testing Strategy

*   **Unit Tests**: Place unit tests for `ViewModels` and `Repositories` in the `src/test/java` directory. Use a testing framework like JUnit and a mocking library like Mockito if needed.
*   **UI Tests**: Place Jetpack Compose UI tests in the `src/androidTest/java` directory. Use the `androidx.compose.ui.test` APIs to interact with and verify UI components.
*   **Instrumentation Tests**: For tests requiring an Android context (like testing the Room database), place them in `src/androidTest/java`.

---

### 11. Key Feature Implementation: Fowl Registration

(Content from the original blueprint)
