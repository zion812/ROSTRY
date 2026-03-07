# Phase-by-Phase Implementation Design

This document provides detailed implementation guidance for each phase of the modularization transformation.

## Phase 0: Guardrails First

### Objectives

- Establish architectural tests to enforce module boundaries
- Create core:navigation module with navigation abstractions
- Create core:testing module with test utilities
- Set up foundation for subsequent phases

### Implementation Steps

#### Step 1: Create core:navigation Module

```bash
mkdir -p core/navigation/src/main/kotlin/com/rio/rostry/core/navigation
mkdir -p core/navigation/src/test/kotlin/com/rio/rostry/core/navigation
```

**build.gradle.kts**:
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rio.rostry.core.navigation"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime)
}
```

**NavigationRegistry.kt**: (See Components and Interfaces section)

#### Step 2: Create core:testing Module

```bash
mkdir -p core/testing/src/main/kotlin/com/rio/rostry/core/testing
```

**build.gradle.kts**:
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rio.rostry.core.testing"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    
    api(libs.junit)
    api(libs.kotest.runner.junit5)
    api(libs.kotest.assertions.core)
    api(libs.kotest.property)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
    api(libs.archunit)
}
```

**Test Utilities**:
```kotlin
// core/testing/src/main/kotlin/com/rio/rostry/core/testing/TestDispatchers.kt
package com.rio.rostry.core.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestDispatchers(
    val main: TestDispatcher = StandardTestDispatcher(),
    val io: TestDispatcher = StandardTestDispatcher(),
    val default: TestDispatcher = StandardTestDispatcher()
)

// core/testing/src/main/kotlin/com/rio/rostry/core/testing/FakeData.kt
package com.rio.rostry.core.testing

import com.rio.rostry.core.model.*
import java.time.Instant
import java.util.UUID

object FakeData {
    fun createFarmAsset(
        id: String = UUID.randomUUID().toString(),
        farmerId: String = "farmer-1",
        assetType: AssetType = AssetType.BIRD
    ) = FarmAsset(
        id = id,
        farmerId = farmerId,
        assetType = assetType,
        birthDate = Instant.now(),
        breed = "Aseel",
        gender = "Male",
        healthStatus = HealthStatus.HEALTHY,
        location = "Coop 1",
        biologicalData = emptyMap(),
        lifecycleStage = LifecycleStage.ADULT,
        parentMaleId = null,
        parentFemaleId = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
    
    fun createInventoryItem(
        id: String = UUID.randomUUID().toString(),
        farmAssetId: String = "asset-1",
        farmerId: String = "farmer-1"
    ) = InventoryItem(
        id = id,
        farmAssetId = farmAssetId,
        farmerId = farmerId,
        quantity = 1,
        unit = "piece",
        harvestDate = Instant.now(),
        storageLocation = "Warehouse A",
        qualityGrade = QualityGrade.PREMIUM,
        expiryDate = null,
        availableQuantity = 1,
        reservedQuantity = 0,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}
```

