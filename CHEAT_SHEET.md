---
Version: 1.1
Last Updated: 2026-03-01
Audience: Developers
Status: Active
---

# 🛠️ Developer Cheat Sheet

Copy-paste friendly commands & patterns.

---

## 🔨 Build
| Purpose | Command |
|---------|---------|
| Clean build | `./gradlew clean build` |
| Assemble debug APK | `./gradlew assembleDebug` |
| Assemble release AAB | `./gradlew assembleRelease` |

## ✅ Testing
| Type | Command |
|------|---------|
| Unit tests | `./gradlew test` |
| Instrumented tests | `./gradlew connectedAndroidTest` |
| Lint (ktlint + detekt) | `./gradlew ktlintCheck detekt` |

## 📐 Common Patterns
```kotlin
// New screen template
@Composable
fun MyNewScreen() {
    val viewModel: MyViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    // UI here
}
```

```kotlin
// Repository skeleton
class FooRepositoryImpl @Inject constructor(
    private val api: FooApi,
    private val dao: FooDao,
) : FooRepository {
    override fun getFoo(id: String): Flow<Result<Foo>> = flow {
        // implementation
    }
}
```

## 📂 Useful Paths
| What | Path |
|------|------|
| ViewModels | `app/src/main/java/com/rio/rostry/ui/<feature>/` |
| Repositories | `app/src/main/java/com/rio/rostry/data/repository/` |
| Entities | `app/src/main/java/com/rio/rostry/data/database/entity/` |

## 🧰 Troubleshooting Quick Fixes
| Problem | Fix |
|---------|-----|
| Build fails | `./gradlew clean` |
| Gradle sync fails | *Invalidate Caches & Restart* |
| Firebase errors | Check `google-services.json` |
| Maps error | Verify `MAPS_API_KEY` in `local.properties` |
