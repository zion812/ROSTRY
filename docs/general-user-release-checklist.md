# General User Production Release Checklist

**Target Release Date**: TBD  
**Version**: 1.0.0 (General User MVP)  
**Platform**: Android (API 24+)

---

## Pre-Release Validation

### Core Functionality ✅
- [x] All Sprint 1 and Sprint 2 tasks completed
- [x] Database version 18 with OutboxEntity migration
- [x] OrderRepository with state transitions implemented
- [x] SyncManager with outbox processing
- [x] OrderStatusWorker scheduled for polling
- [x] GeneralCartViewModel with offline checkout
- [x] Demo product seeder (80 products) integrated
- [x] English and Telugu localization strings added
- [x] ProGuard rules for General components configured

### Code Quality
- [ ] Unit test coverage > 80% for General ViewModels
- [ ] Integration tests pass for auth, sync, checkout flows
- [ ] UI tests pass for all acceptance criteria scenarios
- [ ] No P0/P1 issues in code review
- [ ] All compiler warnings addressed
- [ ] Lint checks pass with no critical issues
- [ ] Static analysis (detekt/ktlint) passes

### Accessibility
- [ ] Content descriptions added to all icons
- [ ] Semantic properties configured (role, heading, state)
- [ ] 48dp minimum touch targets verified
- [ ] Screen reader navigation tested
- [ ] Dynamic text sizing tested (small to XXL)
- [ ] High contrast mode tested
- [ ] Reduced motion preferences respected
- [ ] Accessibility scanner passes with 0 P0/P1 issues

### Localization
- [x] English strings externalized in values/strings.xml
- [x] Telugu translations in values-te/strings.xml
- [ ] Placeholder syntax verified (%s, %d)
- [ ] Date/time formatting respects locale
- [ ] Number formatting respects locale
- [ ] RTL layout tested (if applicable)
- [ ] No hardcoded strings in UI code

### Database & Storage
- [x] MIGRATION_17_18 tested on existing database
- [ ] Migration rollback tested
- [x] SQLCipher encryption enabled
- [ ] Room foreign key constraints validated
- [ ] Database size monitored (< 50MB for 1000 products)
- [ ] Backup and restore tested

### Firebase Configuration
- [x] Firestore Security Rules deployed
- [x] Firestore indexes created (composite queries)
- [ ] Security rules tested with unit tests
- [ ] Firebase App Check configured (Play Integrity)
- [ ] Cloud Functions deployed (if any)
- [ ] FCM notification channels configured
- [ ] Firebase Analytics events validated

---

## Performance Baselines

### App Performance
- [ ] Cold start time < 3s on mid-range device (Android 9+)
- [ ] Warm start time < 1s
- [ ] Product list scroll maintains 60 FPS on low-end device (2GB RAM)
- [ ] Image load time < 2s on 3G network
- [ ] Checkout flow completes in < 5s end-to-end
- [ ] No ANRs (Application Not Responding) in 100-session test cycle
- [ ] Memory usage < 100MB idle, < 250MB under load

### Network Performance
- [ ] API response time < 1s for product queries
- [ ] Sync completes in < 30s for 100 pending items
- [ ] Offline mode gracefully handles no connectivity
- [ ] Network failures retry with exponential backoff
- [ ] Image caching reduces bandwidth by > 70%

### Database Performance
- [ ] Product query with filters < 100ms
- [ ] Order history query (50 orders) < 200ms
- [ ] Cart operations < 50ms
- [ ] Sync state updates < 20ms
- [ ] Full database rebuild < 5s

---

## Offline-First Validation

### Offline Scenarios
- [ ] **Browse products**: Cache displays products when offline
- [ ] **Add to cart**: Cart items persist locally
- [ ] **Checkout**: Order queued in outbox with success message
- [ ] **View order history**: Recent orders display from cache
- [ ] **Edit profile**: Changes marked dirty and queued

### Reconnect Scenarios
- [ ] Outbox processes pending mutations in FIFO order
- [ ] Order syncs to Firestore within 30s of reconnect
- [ ] Conflict resolution: server timestamp wins for order status
- [ ] Multiple pending orders sync in correct sequence
- [ ] Failed sync retries with exponential backoff (max 3 attempts)
- [ ] UI shows sync progress and completion

### Edge Cases
- [ ] Network drops mid-checkout: order queued successfully
- [ ] App killed while offline: outbox persists on restart
- [ ] Concurrent edits: last-write-wins with server priority
- [ ] Large outbox (50+ items): processes incrementally
- [ ] Corrupt outbox entry: skipped with error logged

---

## Acceptance Criteria Verification

| Criterion | Status | Notes |
|-----------|--------|-------|
| Navigation opens on General home with 5 tabs | ✅ | Market, Explore, Create, Cart, Profile |
| Tab state preserved on switch and deep links | ✅ | SavedStateHandle persistence |
| Filters return correct products (breed, age, location, presets) | ✅ | Tested with 80 demo products |
| Presets apply compound constraints | ✅ | Nearby & Verified, Traceable, Budget, Premium |
| Cached results available offline | ✅ | Room database cache |
| Checkout completes via COD and demo online flow | ✅ | Both payment methods working |
| Order writes locally and syncs to Firestore | ✅ | Dirty flag + SyncManager |
| Push notification received with deep link to cart | ✅ | FCM with rostry://order/{orderId} |
| Profile updates persist offline and reconcile on reconnect | ✅ | UserRepository with dirty flag |
| Order history paginates efficiently (50 orders < 200ms) | ⏳ | Performance test pending |
| No P0/P1 crashes in Crashlytics | ⏳ | Staging test required |

---

## Security & Compliance

### Authentication & Authorization
- [ ] User sessions expire after 30 days of inactivity
- [ ] Biometric authentication supported (optional)
- [ ] Password reset flow tested
- [ ] OAuth tokens refresh automatically
- [ ] Role-based access control (RBAC) enforced

### Data Protection
- [x] SQLCipher encryption enabled for Room database
- [ ] HTTPS/TLS enforced for all network calls
- [ ] Keystore used for sensitive credentials
- [ ] PII fields guarded in Firestore rules (phone, email, address)
- [ ] User data export functionality (GDPR compliance)
- [ ] User data deletion functionality

### Input Validation & Sanitization
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention in user-generated content
- [ ] File upload size limits enforced (< 10MB per image)
- [ ] Image validation (dimensions, format, malware scan)
- [ ] Rate limiting on API endpoints (100 req/min per user)

### ProGuard Obfuscation
- [x] Minify and shrink enabled for release build
- [x] Keep rules for Compose, Hilt, Room configured
- [x] Keep rules for General ViewModels and entities
- [ ] ProGuard mapping file uploaded to Crashlytics
- [ ] Release APK decompilation tested (obfuscation verified)

---

## Testing Strategy

### Unit Tests
- [ ] **GeneralMarketViewModelTest**: 15 test cases covering filters, presets, search, cart, wishlist
- [ ] **GeneralCartViewModelTest**: 15 test cases covering cart ops, checkout, offline queuing, payment flows
- [ ] **OutboxSyncTest**: 8 test cases covering FIFO processing, retry logic, conflict resolution
- [ ] **OrderRepositoryTest**: State transition validation, dirty flag behavior
- [ ] **SyncManagerTest**: Outbox integration, incremental sync, watermark updates

### Integration Tests
- [ ] **Auth flow**: Sign up → verify phone → onboarding → home
- [ ] **Checkout flow**: Browse → add to cart → checkout → payment → confirmation
- [ ] **Offline sync**: Queue order offline → reconnect → verify Firestore update
- [ ] **Notification flow**: Order status change → FCM push → deep link navigation

### UI/E2E Tests
- [ ] **GeneralUserFlowTest**: 7 scenarios covering all acceptance criteria
- [ ] **AccessibilityTest**: Screen reader, touch targets, semantic properties
- [ ] **LocalizationTest**: English and Telugu string display
- [ ] **PerformanceTest**: Scroll jank, image load times, startup latency

### Manual Testing
- [ ] Fresh install → onboarding → first purchase
- [ ] Upgrade from previous version (if applicable)
- [ ] Low memory device (2GB RAM)
- [ ] Slow network (3G simulated)
- [ ] Airplane mode → reconnect
- [ ] Battery optimization mode
- [ ] Kill app mid-operation → resume

---

## Release Artifacts

### Build Configuration
- [ ] **versionCode** incremented (e.g., 18 → 19)
- [ ] **versionName** set to "1.0.0-general"
- [ ] **minSdkVersion** = 24 (Android 7.0)
- [ ] **targetSdkVersion** = 34 (Android 14)
- [ ] **compileSdkVersion** = 34
- [ ] Release signing config configured with production keystore
- [ ] ProGuard mapping file generated

### APK/AAB
- [ ] Release AAB built successfully
- [ ] AAB size < 50MB
- [ ] Debug symbols uploaded to Play Console
- [ ] ProGuard mapping uploaded to Crashlytics
- [ ] APK tested on 5+ real devices (different OEMs, Android versions)

### Play Store Listing
- [ ] App title: "ROSTRY - Poultry Marketplace"
- [ ] Short description (< 80 chars)
- [ ] Full description with features, screenshots
- [ ] 5+ screenshots (phone + tablet) showcasing General flows
- [ ] Feature graphic (1024x500)
- [ ] App icon (512x512, adaptive)
- [ ] Privacy policy URL published
- [ ] Content rating completed (PEGI, ESRB)
- [ ] Target audience set (18+)

---

## Deployment Strategy

### Staging Phase
- [ ] Distribute to internal team via Firebase App Distribution (10 testers)
- [ ] Monitor Crashlytics dashboard for 24 hours
- [ ] Collect feedback via in-app surveys
- [ ] Fix P0/P1 issues identified

### Beta Phase
- [ ] Distribute to General user cohort via Firebase (50 beta testers)
- [ ] Monitor crash-free sessions > 99.5% for 48 hours
- [ ] Monitor Firebase Performance traces (startup, network, screen rendering)
- [ ] Validate analytics events (listing views, add to cart, checkout, filters)
- [ ] Fix P0/P1 issues, P2 issues if time permits

### Release Candidate (RC)
- [ ] All acceptance criteria tests pass
- [ ] Crash-free sessions > 99.5%
- [ ] No open P0/P1 bugs
- [ ] Performance baselines met
- [ ] Security audit passed
- [ ] Accessibility audit passed

### Production Rollout
- [ ] **Day 1**: Release to 10% of users (internal track or staged rollout)
- [ ] **Day 3**: Monitor metrics, increase to 50% if stable
- [ ] **Day 7**: Full rollout to 100% if no regressions
- [ ] Monitor ANR rate < 0.5%, crash rate < 1%
- [ ] Prepare rollback plan (downgrade to previous version if needed)

---

## Post-Release Monitoring

### Metrics to Track (First 7 Days)
- [ ] **Crashes**: < 1% crash-free sessions
- [ ] **ANRs**: < 0.5% ANR rate
- [ ] **User engagement**: DAU, session duration, retention (D1, D7, D30)
- [ ] **Conversion funnel**: Browse → Add to Cart → Checkout → Purchase (track drop-off)
- [ ] **Offline usage**: % of users with pending outbox, avg sync time
- [ ] **Performance**: P95 startup time, P95 screen load time, network request latency

### Dashboards
- [ ] Crashlytics: Real-time crash monitoring
- [ ] Firebase Performance: Trace analysis (network, database, UI rendering)
- [ ] Firebase Analytics: Custom events and user properties
- [ ] Play Console: Vitals (ANR, crash, slow rendering)
- [ ] Custom dashboard: Offline sync metrics, outbox processing time

### Incident Response
- [ ] On-call engineer assigned for first week
- [ ] Rollback procedure documented and tested
- [ ] Hotfix release process defined (< 4 hours for P0)
- [ ] Communication plan for critical issues (email, in-app banner)

---

## Rollback Plan

### Triggers for Rollback
- Crash rate > 5% for any user segment
- ANR rate > 2%
- Data loss or corruption reported
- Payment processing failures > 10%
- Firestore quota exceeded
- Security vulnerability discovered

### Rollback Procedure
1. Pause staged rollout in Play Console immediately
2. Revert to previous stable version (APK/AAB)
3. Communicate issue to users via email/push notification
4. Analyze crash logs and identify root cause
5. Prepare hotfix with targeted fix
6. Test hotfix thoroughly before re-release
7. Post-mortem document with timeline and lessons learned

---

## Sign-Off

| Role | Name | Date | Signature |
|------|------|------|-----------|
| **Engineering Lead** | | | |
| **QA Lead** | | | |
| **Product Manager** | | | |
| **Security Reviewer** | | | |
| **Release Manager** | | | |

---

## Next Steps After Release

1. **Week 1**: Monitor metrics daily, fix P0 hotfixes
2. **Week 2-4**: Gather user feedback, plan Sprint 3 features
3. **Month 2**: Implement Sprint 3 (enhanced explore, post composer, profile editing)
4. **Month 3**: Performance optimization, advanced filtering, recommendation engine
5. **Month 6**: Community features, gamification, referral program

---

**Last Updated**: 2025-10-01  
**Document Owner**: Engineering Team  
**Status**: Draft - Pending Review
