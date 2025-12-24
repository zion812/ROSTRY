# Digital Farm Feature Guide

**Document Type**: Feature Documentation  
**Version**: 1.0  
**Last Updated**: 2025-12-25  
**Feature Owner**: Enthusiast Domain  
**Status**: ✅ Implemented

---

## Overview

The **Digital Farm** is an interactive, gamified visualization feature for Enthusiast users. It provides a 2.5D isometric view of the user's poultry farm, showing birds through their lifecycle stages with visual zones, idle animations, and tap interactions.

### Key Features

| Feature | Description |
|---------|-------------|
| **Canvas Rendering** | Low-poly 2.5D isometric graphics using Jetpack Compose Canvas |
| **Zone Visualization** | Birds grouped by lifecycle stage (Nursery, Breeding, Free-Range, Market) |
| **Idle Animations** | Bird bobbing, cloud parallax, chick orbiting, gold star pulsing |
| **Hit Testing** | Tap detection for birds, huts, and market stand |
| **Ghost Eggs** | Visual reminder for breeding units needing egg logs |
| **Gold Star Badges** | Ready-for-sale birds marked with animated gold stars |
| **Stats Bar** | Gamified display of total birds, eggs, ready count, and coins |

---

## Architecture

### File Structure

```
ui/enthusiast/digitalfarm/
├── DigitalFarmScreen.kt        # Main Composable (entry point)
├── DigitalFarmViewModel.kt     # State management, grouping logic
├── FarmCanvasRenderer.kt       # Canvas drawing engine
```

### Domain Models

```
domain/model/
└── DigitalFarmModels.kt        # Zone enums, VisualBird, NurseryGroup, BreedingUnit
```

### Database Fields (ProductEntity)

```kotlin
// Digital Farm lifecycle fields
val motherId: String?           // Chick-to-mother relationship
val isBreedingUnit: Boolean     // Breeding group flag
val eggsCollectedToday: Int     // Daily egg count
val lastEggLogDate: Long?       // Last egg log timestamp
val readyForSale: Boolean       // Gold star display flag
val targetWeight: Float?        // Target weight threshold
```

---

## Rendering System

### Zone Layout

```
┌─────────────────────────────────────────────────────────────┐
│                         SKY ZONE                            │
│  ☁️ ☁️     ☁️                                  (clouds)     │
│  🌲 🌲 🌲 🌲 🌲                                 (trees)      │
├─────────────────────────────────────────────────────────────┤
│                    BREEDING HUT ZONE                        │
│  🏠  🏠  🏠                    (huts with rooster+hens)     │
├─────────────────────────────────────────────────────────────┤
│                    FREE-RANGE ZONE                          │
│  🐔 🐔 🐔 🐔                   (adult birds wandering)      │
├─────────────────────────────────────────────────────────────┤
│                    NURSERY ZONE                             │
│  🐣🐣🐣 🐔                     (mother + orbiting chicks)   │
├─────────────────────────────────────────────────────────────┤
│                    MARKET STAND                             │
│  🏪 🐔🐔🐔                     (birds listed for sale)      │
└─────────────────────────────────────────────────────────────┘
```

### Rendering Layers

The `FarmCanvasRenderer` draws in this order:

1. **Sky** - Gradient from blue to light blue
2. **Clouds** - Parallax scrolling (`animationTime * 0.3f`)
3. **Background Trees** - Static horizon elements
4. **Ground** - Grass gradient with dirt path
5. **Shadows** - 2.5D depth illusion for structures
6. **Back Fences** - Fence segments at farm perimeter
7. **Breeding Huts** - Colored huts with roofs
8. **Market Stand** - Striped awning, table, products
9. **Birds** - Sorted by Y-coordinate for depth
10. **Nurseries** - Mother hen + orbiting chicks
11. **Front Fences** - Completing the fence
12. **Status Indicators** - Vaccine, sick, weight icons

---

## Data Flow

### ViewModel Logic

```kotlin
@HiltViewModel
class DigitalFarmViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    // 1. Fetch products for current user
    // 2. Group by lifecycle stage → DigitalFarmState
    // 3. Calculate deterministic positions
    // 4. Expose as StateFlow for Compose
}
```

### State Model

```kotlin
data class DigitalFarmState(
    val nurseries: List<NurseryGroup> = emptyList(),
    val breedingUnits: List<BreedingUnit> = emptyList(),
    val freeRange: List<VisualBird> = emptyList(),
    val growOut: List<VisualBird> = emptyList(),
    val readyDisplay: List<VisualBird> = emptyList(),
    val marketReady: List<VisualBird> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Zone Determination Rules

| Condition | Zone |
|-----------|------|
| `motherId != null` AND `ageWeeks < 4` | NURSERY (chick) |
| `isBreedingUnit == true` | BREEDING_UNIT |
| `ageWeeks in 4..12` | FREE_RANGE |
| `ageWeeks in 12..20` | GROW_OUT |
| `readyForSale == true` OR `weight >= targetWeight` | READY_DISPLAY |
| Listed in marketplace | MARKET_STAND |

---

## Hit Testing

### Implementation

```kotlin
fun hitTest(
    tapX: Float,
    tapY: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    state: DigitalFarmState
): HitTestResult

sealed class HitTestResult {
    data class BirdHit(val bird: VisualBird) : HitTestResult()
    data class NurseryHit(val nursery: NurseryGroup) : HitTestResult()
    data class BreedingHutHit(val unit: BreedingUnit) : HitTestResult()
    data object MarketStandHit : HitTestResult()
    data object Nothing : HitTestResult()
}
```

### Hit Zones

| Element | Detection Region |
|---------|-----------------|
| Bird | 4% of canvas around position |
| Breeding Hut | 6% × 8% around hut center |
| Nursery | 6% × 6% around nest position |
| Market Stand | 65-85% X, 65-85% Y |

---

## Animations

### Infinite Transitions

```kotlin
val animationTime by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
        animation = tween(100000, easing = LinearEasing)
    )
)
```

### Animation Effects

| Element | Animation | Formula |
|---------|-----------|---------|
| Bird bob | Vertical oscillation | `sin(animTime * 2f + offset) * 2f` |
| Head bob | Head movement | `sin(animTime * 3f + offset) * 1f` |
| Chick orbit | Circular path | `cos/sin(animTime * 0.5f + offset)` |
| Gold star | Pulse + glow | `sin(animTime * 4f) * 0.1f + 1f` |
| Clouds | Parallax scroll | `(animTime * 10f) % width` |
| Ghost eggs | Float + fade | `sin(animTime * 2f) * 3f`, `0.5f + sin * 0.3f` |

---

## UI Components

### Stats Bar

```
┌─────────────────────────────────────────────┐
│  🐔 45  │  🥚 12  │  ⭐ 8  │  ₹ 2,500      │
│  Birds  │  Eggs   │  Ready │  Coins        │
└─────────────────────────────────────────────┘
```

### Bird Stats Bubble

When a bird is tapped:

```
┌─────────────────────────────────────────────┐
│  🐔            Weight: 2.5 kg          ❌  │
│                                             │
│  📅 8 weeks        🏷️ Broiler               │
│                                             │
│  ⭐ Ready for Sale!                        │
│                                             │
│  [Details]          [List for Sale]         │
└─────────────────────────────────────────────┘
```

### Ghost Eggs Reminder

For breeding units needing egg logs:

```
┌─────────────────────────────────────────────┐
│  🥚  Time to Log Eggs!         [Log Now]   │
│      3 breeding units need logging          │
└─────────────────────────────────────────────┘
```

---

## Navigation

### Route

```kotlin
object EnthusiastNav {
    const val DIGITAL_FARM = "enthusiast/digital_farm"
}
```

### Actions

| Tap Target | Navigation Action |
|------------|-------------------|
| Bird | `onNavigateToProduct(productId)` |
| Market Stand | `onNavigateToListProduct(productId)` |
| Breeding Hut | `onNavigateToLogEggs(unitId)` |

---

## Database Migration

### Migration 53 → 54

```kotlin
val MIGRATION_53_54 = object : Migration(53, 54) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN motherId TEXT")
        db.execSQL("ALTER TABLE products ADD COLUMN isBreedingUnit INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE products ADD COLUMN eggsCollectedToday INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE products ADD COLUMN lastEggLogDate INTEGER")
        db.execSQL("ALTER TABLE products ADD COLUMN readyForSale INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE products ADD COLUMN targetWeight REAL")
        
        // Indexes for query optimization
        db.execSQL("CREATE INDEX IF NOT EXISTS index_products_motherId ON products (motherId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_products_isBreedingUnit ON products (isBreedingUnit)")
    }
}
```

---

## Performance Considerations

### Optimization Strategies

| Strategy | Implementation |
|----------|----------------|
| **Zone Grouping** | Birds grouped by zone instead of individual rendering |
| **Reusable Paths** | Path objects precomputed in `FarmCanvasRenderer` |
| **Batched Drawing** | Draw operations organized by layer |
| **Minimal Allocation** | Avoid object creation in draw loop |
| **Y-Sort Caching** | Birds sorted once per state update |

### Low-End Device Support

- Canvas operations are GPU-accelerated
- Animation frame rate adapts to device capability
- State updates debounced to avoid excessive recomposition

---

## Testing

### Unit Tests

- `DigitalFarmViewModelTest` - Grouping logic, zone determination
- `FarmCanvasRendererTest` - Hit testing accuracy

### UI Tests

- Canvas renders without crash
- Tap interactions trigger correct callbacks
- Animation transitions smoothly

---

## Future Enhancements

| Feature | Priority | Description |
|---------|----------|-------------|
| Time-lapse mode | Medium | View farm growth over time |
| Share/Screenshot | Low | Export farm image |
| Weather effects | Low | Rain, sun, day/night cycle |
| Sound effects | Low | Ambient farm sounds |
| Achievements | Medium | Gamification badges |

---

## Related Documentation

- `enthusiast-role-guide.md` - Enthusiast feature overview
- `farm-monitoring.md` - Farm management features
- `gamification.md` - Achievement system
- `traceability.md` - Lineage tracking

---

*The Digital Farm brings your poultry operations to life. Watch your birds grow!* 🐔🌾
