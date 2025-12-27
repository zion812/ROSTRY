# Digital Farm Feature Guide

**Document Type**: Feature Documentation  
**Version**: 2.0  
**Last Updated**: 2025-12-27  
**Feature Owner**: Enthusiast Domain  
**Status**: ✅ Fully Implemented (6 Phases Complete)

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

---

## Architecture

### File Structure

```
ui/enthusiast/digitalfarm/
├── DigitalFarmScreen.kt        # Main Composable (entry point)
├── DigitalFarmViewModel.kt     # State management, grouping logic
├── FarmCanvasRenderer.kt       # Canvas drawing engine (~2000 lines)
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

---

## Related Documentation

- `enthusiast-role-guide.md` - Enthusiast feature overview
- `farm-monitoring.md` - Farm management features
- `gamification.md` - Achievement system
- `traceability.md` - Lineage tracking

---

*The Digital Farm brings your poultry operations to life. Watch your birds grow!* 🐔🌾
