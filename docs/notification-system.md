# Notification System

Comprehensive push and local notifications across transfer, social, and analytics domains.

## Architecture

- Notifiers: `TransferNotifier.kt` and `SocialNotifier.kt` coordinate domain-specific messages.
- Analytics-related notifications (e.g., summaries, milestones) are produced by background workers such as `AnalyticsAggregationWorker` and `ReportingWorker`, and may be triggered based on tracking signals from `AnalyticsTracker`.
- FCM integration via `AppFirebaseMessagingService.kt` handles token refresh and payload routing.
- `NotificationDao` and `NotificationEntity` persist notifications for history and state.

## Categories & Channels

- Separate channels for Transfers, Social, Analytics with priorities and badges.

## Background Integration

- Workers schedule periodic checks and synthesize reminders.

## Payloads

- Standard JSON structure: type, title, body, deep link, and data map.

## Preferences

- Per-category opt-in/out; quiet hours; system permission handling.

## Analytics

- Delivery and open tracking; trace IDs for troubleshooting.

## Testing

- Use debug endpoints and FCM console; verify deep links and channel behavior.
