# ROSTRY - Technical Blueprint

## 1. Introduction

This document provides a comprehensive technical overview of the ROSTRY Android application. It is intended as an end-to-end guide for developers, covering the project's architecture, features, data management, and backend services. ROSTRY is a specialized platform for fowl enthusiasts and farmers to manage, trade, and track poultry.

---

## 2. Technical Stack

- **UI**: Jetpack Compose (with Material 3) for a modern, declarative UI.
- **Architecture**: Model-View-ViewModel (MVVM).
- **Asynchronous Programming**: Kotlin Coroutines & Flows for managing background tasks and data streams.
- **Dependency Injection**: Hilt for managing dependencies throughout the app.
- **Navigation**: Jetpack Navigation Compose for handling in-app screen transitions.
- **Local Storage**: Room Persistence Library for offline data caching.
- **Backend**: Firebase Suite
  - **Authentication**: Firebase Auth for user management.
  - **Database**: Firestore for real-time data storage.
  - **File Storage**: Firebase Storage for user-uploaded images.
  - **Serverless Logic**: Firebase Cloud Functions for secure, server-side operations.
- **Image Loading**: Coil for efficient image loading from URLs.

---

## 3. Application Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern, promoting a separation of concerns:

- **View (UI Layer)**: Composable functions (`...Screen.kt`) that observe state from ViewModels and render the UI. They are responsible for displaying data and forwarding user events.
- **ViewModel**: Acts as a bridge between the UI and the Data Layer. It holds and manages UI-related state, handles user interactions, and calls repositories to fetch or update data.
- **Model (Data Layer)**: Composed of Repositories, DAOs (Data Access Objects), and data model classes. It's responsible for all data operations, abstracting the data sources (local database and remote backend) from the rest of the app.

---

## 4. Project Structure

```
app/src/main/java/com/rio/rostry/
├── data/                 # Data layer
│   ├── local/            # Room DB: DAOs, Database class
│   ├── models/           # Data models (Entities, Firestore objects)
│   └── repo/             # Repositories (single source of truth)
├── di/                   # Dependency Injection (Hilt modules)
│   └── AppModule.kt
├── navigation/           # Navigation graph setup
│   └── NavGraph.kt
├── ui/                   # UI Layer (Jetpack Compose)
│   ├── auth/             # Auth screens (Login, Register)
│   ├── fowl/             # Fowl management screens
│   ├── main/             # Main app container and core screens (Profile, Settings)
│   ├── market/           # Marketplace features
│   ├── messaging/        # Chat and conversation screens
│   ├── theme/            # App theme, colors, typography
│   └── transfer/         # Fowl transfer screens
├── utils/                # Utility classes and helpers
└── ...
```

---

## 5. Data Layer In-Depth

### 5.1. Local Persistence (Room)

- **Database**: `RostryDatabase.kt` defines the database, its entities, and provides access to DAOs.
- **Entities**: Data classes annotated with `@Entity` (e.g., `User.kt`, `Fowl.kt`, `Transfer.kt`) that represent tables in the database.
- **DAOs (Data Access Objects)**: Interfaces (e.g., `UserDao.kt`, `FowlDao.kt`) that define the SQL queries for interacting with the database tables.

### 5.2. Remote Backend (Firebase)

- **Firestore**: The primary remote database. Data is structured in collections like `users`, `fowls`, `transfers`, and `marketplace_listings`.
- **Firebase Storage**: Used for storing user-generated content, specifically profile images under the `profile_images/{userId}` path.
- **Cloud Functions**: Server-side logic for security-sensitive operations. A key example is `initiateTransfer`, which validates and processes fowl transfer requests securely.

### 5.3. Repositories

The repository pattern is used to abstract data sources. Each repository (e.g., `UserRepositoryImpl.kt`, `FowlRepositoryImpl.kt`) is the single source of truth for a specific domain of data. It decides whether to fetch data from the local cache (Room) or the remote source (Firestore).

---

## 6. UI and State Management

- **Screens**: Each feature has its own set of composable screens located in its respective `ui` sub-package.
- **ViewModels**: Each screen is backed by a `ViewModel` (e.g., `AuthViewModel`, `FowlViewModel`) that exposes UI state as a `StateFlow`.
- **State Consumption**: Screens observe the `StateFlow` from the ViewModel using `collectAsState()` to reactively update the UI when the data changes.
- **Navigation**: The entire navigation flow is defined in `navigation/NavGraph.kt`. It includes routes for authentication, main app features, and handles passing arguments between screens.

---

## 7. Feature Breakdown

### 7.1. Authentication & Profile Management
- **Flow**: `LoginScreen` -> `RegisterScreen` -> `ProfileSetupScreen`.
- **Backend**: Firebase Authentication handles user creation and sign-in.
- **Profile**: User data (name, bio, profile image URL) is stored in a `users` collection in Firestore. The `ProfileScreen` displays this data, and the `EditProfileScreen` allows for updates. `StorageRepository` handles profile image uploads.

### 7.2. Fowl Management
- **CRUD Operations**: Users can register new fowls (`FowlRegistrationScreen`), view a list of their fowls (`FowlListScreen`), and see detailed information (`FowlDetailScreen`).
- **History**: `FowlHistoryScreen` allows users to view and add records (e.g., health, breeding) for each fowl.

### 7.3. Marketplace
- **Functionality**: Users can list fowls for sale, browse listings, and manage a cart and wishlist.
- **Components**: `MarketplaceScreen`, `MarketplaceViewModel`, and sub-packages for `cart`, `sell`, `wishlist`, etc.

### 7.4. Secure Fowl Transfer
- **Security**: This is a critical feature handled with enhanced security. Instead of writing directly to Firestore from the client, the app calls a Firebase Cloud Function (`initiateTransfer`).
- **Process**: The Cloud Function validates the request (e.g., checks ownership, prevents duplicate transfers) before creating a transfer document in Firestore.
- **UI**: The `transfer` package contains screens for initiating, managing, and verifying transfers.

### 7.5. Messaging
- **Real-time Chat**: `ConversationScreen` lists user conversations, and `ChatScreen` provides a real-time messaging interface for users, likely for discussing marketplace listings.

---

## 8. Dependency Injection (Hilt)

- **Setup**: Hilt is configured in `RostryApplication.kt` and the primary module is `di/AppModule.kt`.
- **Provided Dependencies**: `AppModule` provides singleton instances of all major components, including:
  - Firebase services (`FirebaseAuth`, `FirebaseFirestore`, etc.).
  - The Room database (`RostryDatabase`).
  - All DAOs and Repositories.
- **Usage**: Dependencies are injected into Android classes (like ViewModels) using the `@Inject` annotation.
