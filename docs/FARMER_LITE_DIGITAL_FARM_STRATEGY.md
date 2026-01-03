# Farmer "Lite" Digital Farm Strategy

**Goal**: Deliver a high-utility, low-performance-cost Digital Farm experience for Farmer persona users on entry-level devices.
**Philosophy**: "Utility, Not Vanity" — An Interactive Status Board, not a simulator.

---

## 1. The "Lite" Renderer Configuration

We will introduce a `DigitalFarmConfig` optimization layer to control rendering operations.

### Configuration Extensions
Extend `DigitalFarmConfig` with performance flags:

```kotlin
data class DigitalFarmConfig(
    // Existing fields...
    val mode: DigitalFarmMode,
    
    // Performance Flags
    val enableParticles: Boolean = false,       // Rain, wind, dust
    val enableDayNightCycle: Boolean = false,   // Dynamic sky gradients
    val enableFlocking: Boolean = false,        // Bird movement/bobbing
    val refreshRate: RenderRate = RenderRate.STATIC, // STATIC (on change) vs DYNAMIC (60fps)
    val groupingMode: GroupingMode = GroupingMode.BY_BATCH // BATCH (avatars) vs INDIVIDUAL (birds)
)

enum class RenderRate { STATIC, DYNAMIC }
enum class GroupingMode { BY_BATCH, INDIVIDUAL }
```

### Farmer Default Config
```kotlin
val FARMER_LITE = DigitalFarmConfig(
    mode = DigitalFarmMode.FARMER,
    enableParticles = false,
    enableDayNightCycle = false,          // Use static "Golden Hour" lighting
    enableFlocking = false,               // Static sprites
    refreshRate = RenderRate.STATIC,      // Only redraw on data update or interaction
    groupingMode = GroupingMode.BY_BATCH, // 1 Avatar = 1 Batch
    // ... existing fields ...
)
```

### Renderer Modifications (`FarmCanvasRenderer`)
- **Sky**: If `!enableDayNightCycle`, draw single cached bitmap or static gradient (Golden Hour).
- **Weather**: If `!enableParticles`, skip `drawRainEffect`, `drawWindEffect`, `drawClouds` (animation loop).
- **Birds**: If `groupingMode == BY_BATCH`, render `BatchAvatar` sprites instead of iterating `VisualBird` lists.

---

## 2. Visual Aggregation (The "Fetcher" Logic)

Farmers manage **Batches** (e.g., "Batch A - 50 Broilers"), not 50 individual entities on screen.

### Batch Avatars
Instead of 50 chickens,render a single "Batch Avatar" in the zone.

- **Visual**: A large rooster/hen sprite standing on a crate or feed bag.
- **Badge**: "x50" pill in top-right.
- **Status Halo**: Colored ring behind the avatar.
  - 🟢 **Green**: Healthy, on track.
  - 🔴 **Red**: High mortality (>5%) or Vaccine overdue.
  - 🟡 **Yellow**: Feed low or FCR warning.

### Data Strategy
Switch from `ProductDao` (individual) to `BatchSummaryDao` (aggregates).

- **Source**: `BatchSummaryEntity` (already exists in DB).
- **Fetcher**:
  ```kotlin
  // LiteDigitalFarmViewModel.kt
  batchSummaryDao.observeForFarmer(userId).collect { summaries ->
     // Map summaries to Zone-based avatars
  }
  ```
- **Advantages**: 
  - Query returns ~5-10 rows (batches) instead of 500-1000 rows (birds).
  - drastically reduces object allocation and layout calculations.

---

## 3. Farmer-Specific Interactions

Replace expensive zoom/pan and drag-and-drop with efficient touch targets.

### Zone Tap -> Bottom Sheet
- **Interaction**: Tap anywhere on "Coop 1".
- **Result**: Open `CoopDetailsBottomSheet`.
- **Content**: List of Batches in that coop, generic actions ("Add Feed to Coop").
- **Benefit**: No need for precise bird selection on small screens.

### Long Press -> Quick Menu
- **Interaction**: Long press "Nursery" or "Batch Avatar".
- **Result**: Haptic feedback + Context Menu.
- **Actions**:
  - 💊 Vaccinate All
  - 💀 Log Mortality
  - 🍽️ Add Feed

### No Drag-and-Drop
- **Replacement**: "Move" button in the Bottom Sheet.
- **Flow**: Tap Batch -> Move -> Select Target Zone (Dialog) -> Confirm.

---

## 4. Reliability & Offline Caching

### Snapshotting (The "Instant Load" Trick)
1. **Capture**: On `onPause()`, capture `FarmCanvas` to `BitMap`.
2. **Save**: Compress to `farm_snapshot.png` in app cache.
3. **Restore**: On `onCreate()`, immediately load and display `farm_snapshot.png` in an `Image` view on top of the Canvas.
4. **Fade**: Once data loads and Canvas renders first frame, fade out the snapshot.

### OOM Fallback
Wrap `FarmCanvas` in a generic `SafeRenderWrapper`.
```kotlin
try {
   FarmCanvas(...)
} catch (e: OutOfMemoryError) {
   // Disable Canvas, show "List Mode"
   FarmListView(...)
}
```

---

## 5. Implementation Plan

### Phase 2: Configuration & Data
1. Modify `DigitalFarmConfig` in `domain/model`.
2. Update `FarmCanvasRenderer` to support `VisualBatch` rendering.
3. Create `LiteDigitalFarmViewModel` using `BatchSummaryDao`.

### Phase 3: Interactions
1. Create `BatchListBottomSheet` composable.
2. Implement Long-Press context menu logic.
3. Add Snapshot capture/restore logic in Screen `DisposableEffect`.

### Phase 4: Integration
1. Update `AppNavHost` to point `Routes.FarmerNav.DIGITAL_FARM` to the new Lite-enabled screen logic.
2. Verify on emulator with "Low Ram" profile.
