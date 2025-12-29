# ROSTRY R&D Quick Reference

**Version:** 1.0.1
**Date:** 2025-12-29

---

## 📂 Key File Locations

| Component | Path |
|-----------|------|
| **Master Doc** | `ROSTRY_COMPLETE_RND_DOCUMENTATION.md` |
| **Architecture** | `docs/architecture.md`, `SYSTEM_BLUEPRINT.md` |
| **Free Tier Config** | `docs/FREE_TIER_ARCHITECTURE.md` |
| **Feature Docs** | `docs/` (e.g., `farm-monitoring.md`, `social-platform.md`) |
| **UI Source** | `app/src/main/java/com/rio/rostry/ui/` |
| **Data Source** | `app/src/main/java/com/rio/rostry/data/` |
| **Domain Logic** | `app/src/main/java/com/rio/rostry/domain/` |
| **DI Modules** | `app/src/main/java/com/rio/rostry/di/` |
| **Workers** | `app/src/main/java/com/rio/rostry/workers/` |

---

## 🛠 Common Gradle Commands

| Task | Command | Description |
|------|---------|-------------|
| **Build Debug** | `./gradlew assembleDebug` | Build debug APK |
| **Run Tests** | `./gradlew testDebugUnitTest` | Run unit tests |
| **Lint Check** | `./gradlew lintDebug` | Run static analysis |
| **Clean Build** | `./gradlew clean build` | Full clean and rebuild |
| **Coverage** | `./gradlew jacocoTestReport` | Generate coverage report |

---

## 🔌 API Quick Reference

### ProductRepository
- `getProducts(filter)`: `Flow<List<Product>>`
- `getProduct(id)`: `Flow<Product>`
- `createProduct(product)`: `suspend Result<Unit>`
- `updateProduct(product)`: `suspend Result<Unit>`

### OrderRepository
- `createOrder(order)`: `suspend Result<String>`
- `getOrder(id)`: `Flow<Order>`
- `updateStatus(id, status)`: `suspend Result<Unit>`

### AuthRepository
- `login(phone, otp)`: `suspend Result<User>`
- `logout()`: `suspend Result<Unit>`
- `getCurrentUser()`: `Flow<User?>`

---

## 🐛 Troubleshooting Shortcuts

- **Force Sync**: Trigger `SyncWorker` via App Settings (Debug Menu).
- **Reset DB**: Clear App Storage (OS Settings) to wipe Room DB.
- **Log Tags**: Filter Logcat by `ROSTRY_TAG` or `WorkManager`.
- **Build Error**: "KSP processing failed" -> Check Room Entity annotations.

---

## 🔗 External Resources

- [Figma Design System](https://figma.com/...) (Placeholder)
- [Firebase Console](https://console.firebase.google.com/)
- [Jira Board](https://jira.atlassian.com/...) (Placeholder)
