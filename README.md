# ROSTRY AgriTech Marketplace

An offline-first AgriTech marketplace for connecting farmers and buyers in rural areas.

## Project Overview

ROSTRY is a comprehensive Android application built with modern architecture patterns to provide a robust platform for agricultural commerce. The app is designed with offline-first capabilities to ensure functionality in areas with limited connectivity.

## Technology Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Separation of concerns and testability
- **Hilt** - Dependency injection
- **Room** - Local database
- **Firebase** - Backend services (Auth, Firestore, Storage, Functions)
- **WorkManager** - Background task scheduling
- **Timber** - Logging framework

## Architecture

The app follows a clean architecture pattern with the following layers:

1. **Presentation Layer** - Compose UI, ViewModels
2. **Domain Layer** - Business logic, Use cases
3. **Data Layer** - Repositories, Data sources (Room, Firebase)

## Setup Instructions

### Prerequisites

1. Android Studio Ladybug or later
2. Kotlin 2.0+
3. Firebase account

### Firebase Configuration

1. Create a new Firebase project at https://console.firebase.google.com/
2. Add an Android app to your Firebase project with package name `com.rio.rostry`
3. Download the `google-services.json` file
4. Place the `google-services.json` file in the `app/` directory

### Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run the application

## Project Structure

```
app/
├── src/main/java/com/rio/rostry/
│   ├── data/
│   │   ├── local/       # Room DAOs and Database
│   │   ├── model/       # Data models
│   │   ├── remote/      # Firebase services
│   │   └── repository/  # Repository implementations
│   ├── di/              # Hilt dependency injection modules
│   ├── domain/
│   │   ├── model/       # Domain models
│   │   ├── repository/  # Repository interfaces
│   │   └── usecase/     # Business logic
│   ├── presentation/
│   │   ├── ui/          # Compose UI components
│   │   └── viewmodel/   # ViewModels
│   ├── util/            # Utility classes
│   └── work/            # WorkManager workers
```

## Features

- User authentication and management
- Product listing and management
- Order processing
- Payment transfers
- Coin reward system
- Offline-first architecture with sync capabilities
- Push notifications
- Background task scheduling

## Offline-First Approach

The app is designed to work seamlessly offline:

1. All data is stored locally using Room database
2. Changes made offline are queued for sync
3. Background sync using WorkManager
4. Conflict resolution strategies for data consistency

## Testing

The project includes unit tests using JUnit and MockK:

```bash
./gradlew test
```

## ProGuard Configuration

The app includes ProGuard rules for release builds in `app/proguard-rules.pro`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License.