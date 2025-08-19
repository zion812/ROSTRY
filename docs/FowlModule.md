# Fowl Module Implementation

This document describes the implementation of the Fowl module in the ROSTRY application.

## Overview

The Fowl module allows users to manage their poultry, including:
- Creating and managing individual fowl profiles
- Tracking lineage and parentage
- Recording health events and treatments
- Monitoring status changes (growing, breeder, for sale, etc.)

## Data Model

### Fowl
```kotlin
data class Fowl(
    val fowlId: String = "",
    val ownerUserId: String = "",
    val name: String = "",
    val breed: String = "",
    val birthDate: Long = 0L,
    val parentIds: List<String> = emptyList(),
    val healthRecords: List<HealthRecord> = emptyList(),
    val status: String = "growing",
    val photoUrl: String = "",
    val lineageNotes: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
```

### HealthRecord
```kotlin
data class HealthRecord(
    val type: String = "", // vaccination, treatment, etc.
    val date: Long = 0L,
    val notes: String = ""
)
```

## Firestore Structure

```
/fowls (collection)
  /{fowlId} (document)
    - fowlId: String
    - ownerUserId: String
    - name: String
    - breed: String
    - birthDate: Timestamp
    - parentIds: List<String>
    - healthRecords: List<HealthRecord>
    - status: String
    - photoUrl: String
    - lineageNotes: String
    - createdAt: Timestamp
    - updatedAt: Timestamp
```

## Key Features Implemented

1. **CRUD Operations**: Create, read, update, and delete fowl records
2. **Health Tracking**: Add and view health records for each fowl
3. **Lineage Management**: Track parentage and lineage information
4. **Status Management**: Update fowl status (growing, breeder, for sale, etc.)
5. **Real-time Updates**: Fetch and display fowl data from Firestore

## Components

### Repository
`FowlRepository` handles all Firestore operations:
- `addFowl(fowl: Fowl): Result<String>` - Create a new fowl
- `updateFowl(fowl: Fowl): Result<Unit>` - Update an existing fowl
- `deleteFowl(fowlId: String): Result<Unit>` - Delete a fowl
- `getFowlsForUser(ownerUserId: String): Result<List<Fowl>>` - Get all fowls for a user
- `getFowl(fowlId: String): Result<Fowl>` - Get a specific fowl
- `addHealthRecord(fowlId: String, healthRecord: HealthRecord): Result<Unit>` - Add a health record to a fowl

### ViewModel
`FowlViewModel` manages the UI state and business logic:
- `fetchFowls(ownerUserId: String)` - Load fowls for a user
- `addFowl(fowl: Fowl)` - Add a new fowl
- `updateFowl(fowl: Fowl)` - Update an existing fowl
- `deleteFowl(fowlId: String, ownerUserId: String)` - Delete a fowl
- `addHealthRecord(fowlId: String, healthRecord: HealthRecord, ownerUserId: String)` - Add a health record

### UI Screens
1. `FowlListScreen` - Displays a list of fowls with basic information
2. `AddEditFowlScreen` - Form for adding or editing fowl details
3. `FowlDetailScreen` - Detailed view of a single fowl with all information
4. `AddHealthRecordScreen` - Form for adding health records to a fowl

## Security Rules

The Firestore security rules ensure that users can only access their own fowl data:

```javascript
match /fowls/{fowlId} {
  // Allow read/write access only to the user who owns this fowl
  allow read, write: if request.auth != null && request.auth.uid == resource.data.ownerUserId;
  
  // Allow create access to authenticated users
  allow create: if request.auth != null;
}
```

## Testing

Unit tests have been implemented for:
- `FowlRepository` - Tests for Firestore operations
- `FowlViewModel` - Tests for business logic and state management

UI tests have been implemented for:
- `FowlListScreen` - Tests for displaying fowls and handling different states

## Future Enhancements

1. **Media Support**: Add photo upload and management for fowls
2. **Advanced Lineage Visualization**: Implement family tree views
3. **Notifications**: Add reminders for health events and vaccinations
4. **Offline Support**: Implement local caching for offline access
5. **Search and Filter**: Add capabilities to search and filter fowls