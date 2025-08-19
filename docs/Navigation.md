# ROSTRY Navigation System

This document describes the navigation system implemented in the ROSTRY app.

## Overview

The ROSTRY app uses a type-safe navigation system built on top of Jetpack Compose Navigation. The system is designed to:

1. Provide compile-time safety for navigation routes
2. Centralize route definitions
3. Simplify argument passing between screens
4. Enable deep linking
5. Support bottom navigation with proper back stack management

## Key Components

### AppDestination

The `AppDestination` sealed class hierarchy defines all possible destinations in the app. Each destination can have associated arguments and deep links.

```kotlin
sealed class AppDestination(val route: String) {
    object Home : AppDestination("home")
    data class FowlDetail(val fowlId: String) : AppDestination("fowl_detail/{$fowlIdArg}") {
        companion object {
            const val fowlIdArg = "fowlId"
        }
        
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(fowlIdArg) { type = NavType.StringType }
            )
            
        override val deepLinks: List<NavDeepLink>
            get() = listOf(
                navDeepLink { uriPattern = "rostry://fowl/{fowlId}" }
            )
    }
    // ... other destinations
}
```

### Route Building

The `AppRoutes` object provides utility functions to build routes with arguments:

```kotlin
object AppRoutes {
    fun fowlDetail(fowlId: String) = "fowl_detail/$fowlId"
    // ... other route builders
}
```

### Navigation Arguments

The `NavArgs` object and extension functions provide type-safe access to navigation arguments:

```kotlin
object NavArgs {
    const val FOWL_ID = "fowlId"
}

fun SavedStateHandle.fowlId(): String = this.get<String>(NavArgs.FOWL_ID) ?: ""
```

## Usage

### Navigating to a Destination

To navigate to a destination with arguments:

```kotlin
// From a composable
navController.navigate(AppRoutes.fowlDetail(fowl.fowlId))

// From a ViewModel
// Use SavedStateHandle to retrieve arguments
val fowlId = savedStateHandle.fowlId()
```

### Handling Arguments in a Screen

In a composable screen, arguments are handled through the back stack entry:

```kotlin
composable(
    AppDestination.FowlDetail("").route,
    arguments = AppDestination.FowlDetail("").arguments,
    deepLinks = AppDestination.FowlDetail("").deepLinks
) { backStackEntry ->
    val fowlId = backStackEntry.arguments?.getString(AppDestination.FowlDetail.fowlIdArg) ?: ""
    // Use fowlId to load data
}
```

### Handling Arguments in a ViewModel

In a ViewModel, arguments are retrieved using the SavedStateHandle:

```kotlin
class FowlDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repo: FowlRepository = FowlRepository()
) : ViewModel() {
    
    init {
        val fowlId = savedStateHandle.fowlId()
        // Use fowlId to load data
    }
}
```

## Deep Linking

Deep links are defined for each destination in the `AppDestination` sealed class:

```kotlin
data class FowlDetail(val fowlId: String) : AppDestination("fowl_detail/{$fowlIdArg}") {
    // ... other code
    
    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink { uriPattern = "rostry://fowl/{fowlId}" }
        )
}
```

The deep links are then used in the NavGraph:

```kotlin
composable(
    AppDestination.FowlDetail("").route,
    arguments = AppDestination.FowlDetail("").arguments,
    deepLinks = AppDestination.FowlDetail("").deepLinks
) { backStackEntry ->
    // Handle the deep link
}
```

The AndroidManifest.xml is configured to handle the deep links:

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.ROSTRY">
    <!-- ... other intent filters -->
    
    <!-- Deep link intent filters -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="rostry" />
    </intent-filter>
</activity>
```

## Bottom Navigation

The bottom navigation is implemented with proper back stack management:

```kotlin
navController.navigate(item.route) {
    // Pop up to the start destination of the graph to
    // avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    // Avoid multiple copies of the same destination when
    // reselecting the same item
    launchSingleTop = true
    // Restore state when reselecting a previously selected item
    restoreState = true
}
```

## Testing

The navigation system includes unit tests to verify route building, destination definitions, and deep link configurations:

```kotlin
class NavigationTest {
    
    @Test
    fun testRouteBuilding() {
        // Test that route builders work correctly
        val fowlId = "test-fowl-id"
        assertEquals("fowl_detail/$fowlId", AppRoutes.fowlDetail(fowlId))
    }
    
    @Test
    fun testDestinationRoutes() {
        // Test that destination routes are correctly defined
        assertEquals("fowl_detail/{fowlId}", AppDestination.FowlDetail("").route)
    }
    
    @Test
    fun testDestinationDeepLinks() {
        // Test that destination deep links are correctly defined
        val fowlDetailDestination = AppDestination.FowlDetail("")
        assertEquals(1, fowlDetailDestination.deepLinks.size)
        assertEquals("rostry://fowl/{fowlId}", fowlDetailDestination.deepLinks[0].uriPattern)
    }
}
```

## Best Practices

1. **Always use type-safe routes** - Define all routes in the `AppDestination` sealed class
2. **Use AppRoutes for building routes** - This ensures consistency and reduces errors
3. **Retrieve arguments through SavedStateHandle** - This provides a clean separation between UI and data logic
4. **Handle errors gracefully** - Always check for null or invalid arguments
5. **Use deep links for external navigation** - Enable sharing and push notifications to route users to specific content
6. **Test navigation thoroughly** - Include unit tests for route building and deep link configurations

## Future Enhancements

1. **Add more deep links** - Extend deep linking to more destinations
2. **Implement navigation analytics** - Track user navigation patterns
3. **Add navigation transitions** - Implement smooth transitions between screens
4. **Enhance error handling** - Improve error handling for navigation failures
5. **Add navigation logging** - Log navigation events for debugging