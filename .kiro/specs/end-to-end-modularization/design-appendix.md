# Design Document Appendix

## Related Documentation

This design document is part of a comprehensive set of documentation for the end-to-end modularization effort:

1. **requirements.md** - Complete requirements specification with 12 requirements across 6 phases
2. **design.md** - Main design document (this document) covering architecture, components, data models, correctness properties, error handling, and testing strategy
3. **design-summary.md** - Phase-by-phase overview with migration waves, module structure, risk mitigation, and success metrics
4. **phase-implementation.md** - Detailed implementation steps for each phase (to be expanded)

## Quick Reference

### Key Concepts

- **App Shell**: Thin integration layer containing only Application, MainActivity, and root navigation
- **Feature Module**: Vertical slice owning screens, ViewModels, UI state, and navigation
- **Domain Module**: Business logic contracts (interfaces, use cases)
- **Data Module**: Implementations of domain contracts
- **Core Module**: Shared technical infrastructure
- **Navigation Registry**: Decentralized navigation system for feature registration
- **3-Tier Asset Model**: FarmAssetEntity → InventoryItemEntity → MarketListingEntity

### Module Naming Patterns

- Core: `core:{capability}` (e.g., `core:navigation`, `core:database`)
- Domain: `domain:{business_area}` (e.g., `domain:account`, `domain:commerce`)
- Data: `data:{business_area}` (e.g., `data:account`, `data:commerce`)
- Feature: `feature:{feature_name}` (e.g., `feature:login`, `feature:marketplace`)

### Dependency Rules

1. Features → Domain + Core (never Data)
2. Data → Domain + Core (never Feature)
3. Domain → Core:Model only (framework-independent)
4. App → Feature + Core (never Domain or Data directly)

### Architecture Test Categories

1. **Dependency Rules**: Verify module dependencies follow architecture
2. **Naming Conventions**: Verify modules follow naming patterns
3. **Implementation Completeness**: Verify domain interfaces have implementations
4. **Framework Independence**: Verify domain modules don't depend on Android
5. **App Shell Purity**: Verify app module contains no feature code

### Testing Approach

- **Property Tests**: Universal properties across all inputs (100+ iterations)
- **Unit Tests**: Specific examples, edge cases, error conditions
- **Integration Tests**: Module interactions, navigation flows
- **Architecture Tests**: Structural rules, dependency constraints

## Glossary

### Terms

- **ADR-004**: Architecture Decision Record defining 3-tier asset split
- **AppNavHost**: Main navigation composition point in app shell
- **ArchUnit**: Library for architecture testing in Java/Kotlin
- **Compatibility Adapter**: Temporary bridge code for incremental migration
- **DAO**: Data Access Object for database operations
- **Hilt**: Dependency injection framework
- **Kotest**: Kotlin testing framework with property testing support
- **Migration Wave**: Grouped set of features migrated together
- **Navigation Provider**: Interface for feature modules to register navigation
- **Property-Based Testing**: Testing approach using randomized inputs
- **Room**: Android persistence library for SQLite
- **Vertical Slice**: Complete feature implementation from UI to data layer
- **WorkManager**: Android background task scheduling framework

### Acronyms

- **CI/CD**: Continuous Integration / Continuous Deployment
- **DI**: Dependency Injection
- **DTO**: Data Transfer Object
- **PBT**: Property-Based Testing
- **UI**: User Interface
- **VM**: ViewModel

## Code Examples

### Creating a New Feature Module

```bash
# 1. Create module structure
mkdir -p feature/my-feature/src/main/kotlin/com/rio/rostry/feature/myfeature
mkdir -p feature/my-feature/src/test/kotlin/com/rio/rostry/feature/myfeature

# 2. Create build.gradle.kts
cat > feature/my-feature/build.gradle.kts << 'EOF'
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.rio.rostry.feature.myfeature"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Domain dependencies
    implementation(project(":domain:account"))
    
    // Core dependencies
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    
    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Testing
    testImplementation(project(":core:testing"))
}
EOF

# 3. Add to settings.gradle.kts
echo 'include(":feature:my-feature")' >> settings.gradle.kts

# 4. Create navigation provider
cat > feature/my-feature/src/main/kotlin/com/rio/rostry/feature/myfeature/navigation/MyFeatureNavigation.kt << 'EOF'
package com.rio.rostry.feature.myfeature.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.feature.myfeature.MyFeatureScreen

class MyFeatureNavigationProvider : NavigationProvider {
    override val featureId: String = "my-feature"
    
    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable("my-feature/home") {
            MyFeatureScreen()
        }
    }
}
EOF

# 5. Register in Application
# Add to RostryApp.kt onCreate():
# navigationRegistry.register(MyFeatureNavigationProvider())
```

### Creating a New Domain Module

```bash
# 1. Create module structure
mkdir -p domain/my-domain/src/main/kotlin/com/rio/rostry/domain/mydomain
mkdir -p domain/my-domain/src/test/kotlin/com/rio/rostry/domain/mydomain

# 2. Create build.gradle.kts
cat > domain/my-domain/build.gradle.kts << 'EOF'
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    
    testImplementation(project(":core:testing"))
}
EOF

# 3. Create repository interface
cat > domain/my-domain/src/main/kotlin/com/rio/rostry/domain/mydomain/repository/MyRepository.kt << 'EOF'
package com.rio.rostry.domain.mydomain.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

interface MyRepository {
    fun observeData(): Flow<List<MyData>>
    suspend fun getData(id: String): Result<MyData>
    suspend fun saveData(data: MyData): Result<Unit>
}
EOF
```

### Creating a New Data Module

```bash
# 1. Create module structure
mkdir -p data/my-domain/src/main/kotlin/com/rio/rostry/data/mydomain
mkdir -p data/my-domain/src/test/kotlin/com/rio/rostry/data/mydomain

# 2. Create build.gradle.kts
cat > data/my-domain/build.gradle.kts << 'EOF'
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.rio.rostry.data.mydomain"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(project(":domain:my-domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    testImplementation(project(":core:testing"))
}
EOF

# 3. Create repository implementation
cat > data/my-domain/src/main/kotlin/com/rio/rostry/data/mydomain/repository/MyRepositoryImpl.kt << 'EOF'
package com.rio.rostry.data.mydomain.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.mydomain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val dao: MyDao,
    private val api: MyApi
) : MyRepository {
    override fun observeData(): Flow<List<MyData>> {
        return dao.observeAll()
    }
    
    override suspend fun getData(id: String): Result<MyData> {
        return try {
            val data = dao.getById(id)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun saveData(data: MyData): Result<Unit> {
        return try {
            dao.insert(data)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
EOF

# 4. Create Hilt module
cat > data/my-domain/src/main/kotlin/com/rio/rostry/data/mydomain/di/MyDomainDataModule.kt << 'EOF'
package com.rio.rostry.data.mydomain.di

import com.rio.rostry.data.mydomain.repository.MyRepositoryImpl
import com.rio.rostry.domain.mydomain.repository.MyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MyDomainDataModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(impl: MyRepositoryImpl): MyRepository
}
EOF
```

## Troubleshooting

### Common Issues

#### Issue: Circular Dependency Error

**Symptom**: Gradle build fails with "Circular dependency between the following tasks"

**Cause**: Two modules depend on each other directly or indirectly

**Solution**:
1. Identify the circular dependency in the error message
2. Extract shared functionality to a common module
3. Have both modules depend on the common module instead

#### Issue: Hilt Component Not Found

**Symptom**: Runtime error "No implementation found for [Interface]"

**Cause**: Missing @Binds annotation or module not installed

**Solution**:
1. Verify @Binds method exists in data module
2. Verify @Module is annotated with @InstallIn(SingletonComponent::class)
3. Verify data module is included in app dependencies
4. Clean and rebuild project

#### Issue: Navigation Destination Not Found

**Symptom**: Runtime error "Unknown destination" when navigating

**Cause**: NavigationProvider not registered or route mismatch

**Solution**:
1. Verify NavigationProvider is registered in Application.onCreate()
2. Verify route string matches exactly in provider and navigation call
3. Check that buildGraph() is called during app startup
4. Add logging to verify provider registration

#### Issue: Database Migration Failed

**Symptom**: App crashes on startup with "Migration path not found"

**Cause**: Missing migration definition for schema change

**Solution**:
1. Define migration in core:database module
2. Add migration to database builder
3. Test migration with previous database version
4. For development, use fallbackToDestructiveMigration()

## Performance Considerations

### Build Performance

- **Module Caching**: Gradle caches unchanged modules, speeding up incremental builds
- **Parallel Builds**: Enable with `org.gradle.parallel=true` in gradle.properties
- **Configuration Cache**: Enable with `org.gradle.configuration-cache=true`
- **Build Scans**: Use `--scan` flag to identify bottlenecks

### Runtime Performance

- **Lazy Loading**: Feature modules can be loaded on-demand
- **Code Splitting**: Smaller modules enable better code splitting
- **Dependency Injection**: Hilt optimizes DI graph at compile time
- **Navigation**: Decentralized navigation has minimal overhead

### Memory Considerations

- **Module Count**: ~40 modules adds minimal memory overhead
- **Dependency Graph**: Hilt optimizes dependency graph
- **Navigation Registry**: Lightweight registry with minimal memory footprint

## Future Enhancements

### Dynamic Feature Modules

After modularization is complete, consider implementing dynamic feature modules for:
- Admin features (rarely used by most users)
- Advanced analytics (optional feature)
- Premium features (conditional delivery)

### Gradle Version Catalogs

Centralize dependency versions using Gradle version catalogs:
```toml
# gradle/libs.versions.toml
[versions]
compose = "1.5.3"
hilt = "2.48"

[libraries]
androidx-compose-ui = { module = "androidx.compose.ui:ui", version.ref = "compose" }
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
```

### Convention Plugins

Create convention plugins for consistent module configuration:
- `rostry.android.library` - Standard Android library configuration
- `rostry.android.feature` - Feature module configuration
- `rostry.kotlin.library` - Pure Kotlin library configuration

### Automated Module Generation

Create scripts to generate new modules with proper structure:
```bash
./scripts/create-feature-module.sh my-feature domain:account
./scripts/create-domain-module.sh my-domain
./scripts/create-data-module.sh my-domain
```

