---
Version: 2.3
Last Updated: 2026-02-11
Audience: Developers, Product Owners
Status: Active
---

# Digital Farm Feature Guide

**Document Type**: Feature Documentation  
**Version**: 2.3  
**Last Updated**: 2026-02-11  
**Feature Owner**: Enthusiast Domain + Farmer Domain (Lite Mode)  
**Status**: ✅ Fully Implemented (10 Phases Complete)

---

## Overview

The **Digital Farm** is an interactive, gamified visualization feature for Enthusiast users. It provides a 2.5D isometric view of the user's poultry farm, showing birds through their lifecycle stages with visual zones, idle animations, and tap interactions.

### Key Features

| Feature | Description |
|---------|-------------|
| **Canvas Rendering** | Low-poly 2.5D isometric graphics using Jetpack Compose Canvas |
| **Zone Visualization** | Birds grouped by lifecycle stage (Nursery, Breeding, Free-Range, Market) |
| **Day/Night Cycle** | Dynamic sky with morning, afternoon, evening, and night rendering |
| **Weather Effects** | Rain particles, wind-animated grass, overcast clouds |
| **Flocking Algorithm** | Birds move in natural groups with wave-based animation |
| **Age-Based Visuals** | Chicks yellow → adult plumage with size progression |
| **Breeding Indicators** | Fertility hearts, DNA helix badges, hen count badges |
| **Building Placement** | 8 placeable building types with customization |
| **Resource Management** | Feed, water, medicine bars with restock alerts |
| **Daily Tasks** | Gamification tasks with coin rewards |
| **Performance Charts** | Line, bar, pie charts for farm metrics |
| **Pedigree Tree** | Ancestry visualization with parent connections |
| **Competitions** | Leaderboards, challenges, achievements |
| **Farm Sharing** | Public showcase with reactions and followers |
| **Offline Mode** | Cached snapshots for offline viewing |

---

## New in Version 2.0 (2025-12-27)

### Phase 2: Enhanced Visualization
- `TimeOfDay` enum (MORNING, AFTERNOON, EVENING, NIGHT)
- `WeatherType` enum (SUNNY, CLOUDY, RAINY, WINDY)
- Sky gradients with sun/moon/stars rendering
- Rain particle system with splash effects
- Wind-animated grass blades
- Flocking algorithm for natural bird movement
- Age-based size modifiers (50% → 100%)
- Color lerping (yellow chicks → adult plumage)

### Phase 3: Role-Specific Features
- Hen count badges (pink indicators)
- Fertility indicators (pulsing hearts)
- DNA helix genetic trait badges
- `PedigreeTree` model with 7 node positions
- `FarmInventoryItem` for supply tracking

### Phase 4: Interactive Features
- `renderZoneDropTargets()` for drag-and-drop
- 8 `FarmBuildingType`: COOP, BROODER, WATER_FOUNTAIN, FEEDER, NESTING_BOX, PERCH, DUST_BATH, SHADE_SHELTER
- `DailyTask` system with 7 task types
- `drawAchievementBadge()` for milestones

### Phase 5: Integration & Optimization
- View culling utilities (`shouldRenderElement`)
- Weather/time StateFlows in ViewModel
- `OfflineFarmSnapshot` for cached viewing

### Phase 6: Advanced Features
- `drawMiniLineChart()` for trends
- `drawMiniBarChart()` for zone distribution
- `drawMiniPieChart()` for flock composition
- `FarmCompetition` with 6 competition types
- `LeaderboardEntry` for rankings
- `SharedFarm` with reactions and followers

### Phase 7: Product Visibility Management
- **Visibility Toggle**: Farmers can set products as "Private" (farm management only) or "Public" (marketplace).
- **Context-Aware Filtering**: `ProductEntity.isPublic` flag controls visibility in the general marketplace feed while keeping items visible in the digital farm view.
- **Inventory Integration**: Visual indicators in the inventory screen show public status.

### Phase 8: Farmer Lite Mode (2026-01-04)

Performance-optimized rendering mode for Farmer persona on low-end devices ($100 Android phones).

**Philosophy**: "Utility, Not Vanity" — Interactive Status Board, not a simulator.

| Feature | Lite Mode (Farmer) | Premium Mode (Enthusiast) |
|---------|-------------------|---------------------------|
| **Particles** | Disabled | Rain, wind, clouds |
| **Day/Night Cycle** | Static "Golden Hour" | Dynamic time-based |
| **Flocking** | Disabled | Wave-based movement |
| **Render Rate** | STATIC (on-change) | DYNAMIC (60fps) |
| **Grouping** | BY_BATCH (avatars) | INDIVIDUAL (birds) |

**New Components:**
- `RenderRate` enum: STATIC, DYNAMIC
- `GroupingMode` enum: BY_BATCH, INDIVIDUAL
- `drawBatchAvatars()`: Zone-level batch visualization with status halos
- `ZoneHit` result: Lite mode zone tap detection
- `FarmFallbackListView`: OOM recovery list view

**Files:**
- [DigitalFarmConfig.kt](../app/src/main/java/com/rio/rostry/domain/model/DigitalFarmConfig.kt) - Performance flags
- [FarmCanvasRenderer.kt](../app/src/main/java/com/rio/rostry/ui/enthusiast/digitalfarm/FarmCanvasRenderer.kt) - Conditional rendering
- [FARMER_LITE_DIGITAL_FARM_STRATEGY.md](./FARMER_LITE_DIGITAL_FARM_STRATEGY.md) - Full strategy

### Phase 9: Search, Zone Labels & Bird IDs (2026-02-11)

Search and identification system for finding and tracking individual birds on the canvas.

**New Components:**
- `drawZoneLabels()`: Renders colored banners per zone with emoji + name + bird count
- `birdCode` field on `VisualBird`: Canvas shows physical ID tags below birds when zoom > 1.8x
- **Search Highlighting**: Matched birds get pulsing cyan glow ring; non-matched dimmed to 25% opacity via `saveLayer`
- **Zone Filter**: Combined text search + zone filter for precise bird location
- **Enhanced Bird Popup**: Shows `birdCode` under emoji in header

### Phase 10: Bird Studio V2 — Advanced Appearance Customization (2026-02-11)

Comprehensive expansion of the Bird Studio with 6 new appearance dimensions and visual rendering.

| Feature | Description |
|---------|-------------|
| **Stance** | Normal, Upright, Low, Game Ready, Crouching, Display — affects body Y-offset |
| **Sheen** | Matte, Satin, Glossy, Metallic, Iridescent, Silky — post-render shimmer overlays |
| **NeckStyle** | Short, Medium, Long, Arched, Muscular, Hackle Heavy — `drawNeck()` shapes |
| **BreastShape** | Round, Flat, Broad, Deep, Puffed — body ellipse proportion modifiers |
| **SkinColor** | Yellow, White, Slate, Dark, Pink — body tint overlay |
| **HeadShape** | Round, Elongated, Broad, Serpentine, Compact — head circle/oval variants |

**New Components:**
- `drawNeck()`: Renders neck between body and head with style-specific visuals (arched Bezier, muscular pipe, hackle feather lines)
- `drawSheen()`: Post-render shimmer overlays (radial gradients for metallic/glossy, iridescent green-purple shift, silky fur dots)
- **HeadShape rendering**: Updated `drawHead()` to use `when(headShape)` for circle-to-oval variants
- **Universal Breed Presets**: 15 common breed presets (RIR, Leghorn, Brahma, Silkie, etc.)
- **Randomize Button**: One-tap random appearance generation
- **BUILD Category**: New Studio category (💪) with Stance/Neck/Breast/Skin sub-parts

**Files:**
- [BirdAppearance.kt](../app/src/main/java/com/rio/rostry/domain/model/BirdAppearance.kt) - 6 new enums + data class fields + JSON serialization
- [BirdStudioScreen.kt](../app/src/main/java/com/rio/rostry/ui/enthusiast/studio/BirdStudioScreen.kt) - Studio UI with new categories and presets
- [BirdPartRenderer.kt](../app/src/main/java/com/rio/rostry/ui/enthusiast/digitalfarm/BirdPartRenderer.kt) - Canvas rendering for all new properties

---

## Architecture

### 2.5D Isometric Engine Architecture

The Digital Farm utilizes a sophisticated 2.5D isometric rendering engine implemented in `FarmCanvasRenderer.kt`. This engine creates a pseudo-3D effect by using 2D graphics with perspective projection, giving the appearance of depth without the computational cost of full 3D rendering.

#### Core Rendering Components

**FarmCanvasRenderer.kt** (~2040 lines) - The primary rendering engine that handles:
- Isometric coordinate transformations
- Z-ordering for proper depth sorting
- Canvas drawing operations for all visual elements
- Performance optimization for smooth rendering

**Coordinate System**
- Uses isometric projection with 30-degree angles
- Z-axis represents depth, affecting draw order
- Screen coordinates converted to world coordinates for interaction

**Rendering Pipeline**
1. **Transform**: Convert world coordinates to screen coordinates
2. **Sort**: Depth-sort elements by Z-coordinate
3. **Draw**: Render elements in correct order
4. **Animate**: Update animations and particle effects

#### File Structure

```
ui/enthusiast/digitalfarm/
├── DigitalFarmScreen.kt        # Main Composable (entry point)
├── DigitalFarmViewModel.kt     # State management, grouping logic
├── FarmCanvasRenderer.kt       # Canvas drawing engine (~2000 lines)
├── BirdPartRenderer.kt         # Bird appearance renderer (~1200 lines) — Stance, Sheen, Neck, HeadShape
├── IsometricProjection.kt      # Coordinate transformation utilities
├── DepthSorting.kt             # Z-ordering algorithms
├── AnimationController.kt      # Animation management
└── ParticleSystem.kt           # Weather and effect particles

ui/enthusiast/studio/
├── BirdStudioScreen.kt         # Bird Studio V2 UI (~1060 lines) — Categories, Presets, Options
└── BirdStudioViewModel.kt      # Studio state and persistence
```

### Domain Models

```
domain/model/
└── DigitalFarmModels.kt        # 500+ lines of models
    ├── TimeOfDay, WeatherType
    ├── VisualBird, NurseryGroup, BreedingUnit
    ├── FarmBuildingType, PlaceableBuilding
    ├── DailyTask, DailyTaskType
    ├── FarmResource, ResourceType
    ├── PedigreeTree, PedigreeNode
    ├── FarmCompetition, LeaderboardEntry
    ├── SharedFarm, FarmReaction
    └── OfflineFarmSnapshot
```

### Weather Effects Implementation

The weather system creates immersive environmental effects:

**Rain Particle System**
- Particle generation with physics simulation
- Splash effects when particles hit ground
- Performance optimization to maintain frame rate

**Wind Animation**
- Directional wind affecting grass and foliage
- Particle movement influenced by wind direction
- Smooth animation transitions

**Overcast Clouds**
- Dynamic cloud rendering based on weather
- Light filtering effects for realistic atmosphere
- Parallax scrolling for depth perception

### Flocking Algorithm

Natural group movement using sine-wave oscillation:
- Birds move in coordinated groups
- Wave-based animation for organic motion
- Collision avoidance between birds
- Pathfinding around obstacles

### Zone-Based Layout System

Organizes the farm into distinct areas:
- **Nursery Zone**: Young birds with mother hen
- **Breeding Zone**: Adult breeding pairs
- **Free Range Zone**: Mature birds roaming
- **Grow-Out Zone**: Birds approaching market weight
- **Ready Display Zone**: Market-ready birds
- **Market Zone**: Birds ready for sale

### Building Placement System

Eight types of placeable buildings:
- Coop
- Brooder
- Water Fountain
- Feeder
- Nesting Box
- Perch
- Dust Bath
- Shade Shelter

Each building type has specific visual representation and functionality.

---

## Rendering Layers

The `FarmCanvasRenderer` draws in this order:

1. **Sky** - Time-based gradient (morning golden → night dark blue)
2. **Stars** - 15 animated stars during night
3. **Sun/Moon** - Celestial body based on TimeOfDay
4. **Clouds** - Parallax scrolling
5. **Overcast** - Weather overlay for cloudy/rainy
6. **Background Trees** - Static horizon elements
7. **Ground** - Grass gradient with dirt path
8. **Wind Effects** - Animated grass blades
9. **Shadows** - 2.5D depth illusion
10. **Back Fences** - Fence segments
11. **Buildings** - Breeding huts with badges
12. **Placeable Buildings** - 8 customizable types
13. **Birds** - Y-sorted with flocking animation
14. **Nurseries** - Mother hen + orbiting chicks
15. **Front Fences** - Completing the fence
16. **Status Indicators** - Vaccine, sick, weight icons
17. **Rain Effect** - Top layer for rain

---

## New Rendering Functions

| Function | Purpose |
|----------|---------|
| `drawSky(timeOfDay)` | Time-based sky gradient |
| `drawRainEffect()` | Rain particles with splash |
| `drawWindEffect()` | Wind-animated grass |
| `drawPlaceableBuilding()` | 8 building types |
| `drawResourceBar()` | Feed/water/medicine bars |
| `drawDailyTaskProgress()` | Task completion circles |
| `drawMiniLineChart()` | Egg production trends |
| `drawMiniBarChart()` | Zone distribution |
| `drawMiniPieChart()` | Flock composition |
| `drawPedigreeNode()` | Ancestry visualization |
| `drawLeaderboardEntry()` | Rankings display |
| `drawCompetitionCard()` | Challenge cards |
| `drawOfflineIndicator()` | Offline status badge |
| `drawZoneLabels()` | Zone banners with emoji + name + count |
| `drawBirdCode()` | Physical ID tags below birds |
| `drawSearchHighlight()` | Cyan glow ring for matched birds |
| `drawNeck()` | Neck between body and head (style-specific) |
| `drawSheen()` | Post-render shimmer overlays |
| `drawHead()` (updated) | HeadShape-aware circle/oval rendering |

---

## Related Documentation

- `enthusiast-role-guide.md` - Enthusiast feature overview
- `farm-monitoring.md` - Farm management features
- `gamification.md` - Achievement system
- `traceability.md` - Lineage tracking

---

*The Digital Farm brings your poultry operations to life. Watch your birds grow!* 🐔🌾
