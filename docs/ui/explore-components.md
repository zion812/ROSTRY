# Explore UI Components

## NearbyFarmersSection

A horizontal scrolling section displaying farmer avatars with location context.

### Usage
```kotlin
NearbyFarmersSection(
    farmers = farmersList,
    userLocation = currentUserLocation,
    onFarmerClick = { farmerId -> navigateToProfile(farmerId) },
    onViewMap = { navigateToMap() }
)
```

### Props
| Prop | Type | Description |
|------|------|-------------|
| `farmers` | `List<UserEntity>` | List of farmer profiles to display |
| `userLocation` | `Location?` | Current user location for distance calc |
| `onFarmerClick` | `(String) -> Unit` | Callback when avatar is tapped |
| `onViewMap` | `() -> Unit` | Callback when "See All/Map" is tapped |

---

## FarmerAvatarCard

A compact circular card representing a farmer.

### Usage
```kotlin
FarmerAvatarCard(
    farmer = farmerData,
    onClick = { onFarmerClick(farmer.id) }
)
```

### Key Features
- Circular avatar image with fallback initials.
- Online/Verification status indicators.
- Distance badge (if location available).
- Ripple effect on interaction.
