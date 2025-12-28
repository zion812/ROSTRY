# Social UI Components

## SocialProfileScreen

The main profile view for a user (Farmer or General) in the social context.

### Enhancements
The screen has been updated to support direct messaging integration.

### Usage
```kotlin
SocialProfileScreen(
    userId = targetUserId,
    onNavigateBack = { navController.popBackStack() },
    onMessage = { recipientId -> 
        navController.navigate("messages/$recipientId") 
    }
)
```

### Messaging Integration
- **Button**: A primary action button "Message" is displayed if `userId != currentUserId`.
- **Flow**: Clicking the button triggers the `onMessage` callback.
- **State**: The button handles the creation of a conversation thread if one does not exist (optional implementation detail, usually handled by the Messaging screen).

## MessageButton

A consistent styled button for initiating conversations.

```kotlin
MessageButton(
    onClick = { onMessageClick() },
    modifier = Modifier.fillMaxWidth()
)
```
