# ROSTRY Enthusiast Core Blueprint — The Champion's Arena

**Version**: 1.1
**Date**: 2026-02-11
**Status**: Active Analysis & Implementation

---

## 🦅 The Enthusiast Persona

**Who they are**: Passionate breeders of premium fighting roosters (Aseel, Shamo, Thai) and ornamental birds.
**Motivation**: Prestige, Legacy, Community Recognition, Genetic Perfection.
**Pain Points**: "Spreadsheets are boring," "I can't show off my birds properly online," "Tracking lineage is a headache."
**The ROSTRY Promise**: "Your phone is now your Trophy Room."

---

## 🏛️ Core Architecture Principles

1.  **Visuals First**: Every screen must look premium. High-res photos, dark mode by default, gold/neon accents.
2.  **Gamified Workflows**: Routine tasks (weighing, feeding) provide haptic feedback, "level up" animations, and streak tracking.
3.  **Social Connectivity**: Every asset (Bird, Batch, Tournament Result) is inherently shareable as a "Card."
4.  **Deep Analytics**: While the Farmer persona gets "alerts," the Enthusiast gets "insights" (trends, genetic predictions, performance curves).

---

## 🔄 Core Loops (The "Golden Paths")

### 1. The "Champion's Morning" (Daily Routine)
*   **Trigger**: Notification "Your Champions are awake."
*   **Action**: User opens app to "Command Center."
*   **Tasks**:
    *   **Egg Check**: "Did 'Queen Slayer' lay today?" -> Slider input -> *Click* -> "Recorded! (Streak 45 Days)"
    *   **Feeding**: Quick tap "Fed Special Mix" to all active fighters.
    *   **Health Scan**: "Review 'Titan' - he looks sluggish." -> Log observation.
*   **Reward**: "Daily Care Badge" progress + Community Reputation bump.

### 2. The "Legacy Builder" (Breeding Cycle)
*   **Trigger**: Planning a new generation.
*   **Action**: Open "Pairing Lab."
*   **Steps**:
    *   Select Sire (e.g., "Black Dragon") and Dam (e.g., "White Pearl").
    *   System predicts: "High probability of: Aggression (A+), Speed (B)".
    *   "Start Pairing" -> Creates a "Breeding Batch" timeline.
*   **Tracking**:
    *   **Incubation Ring**: Visual progress bar for hatch day.
    *   **Hatch Day**: "They're here!" -> Log hatchlings -> Generate auto-IDs based on parents.

### 3. The "Showcase" (Social & Sales)
*   **Trigger**: A bird reaches prime condition or is ready for sale.
*   **Action**: "Mint Rooster Card."
*   **Steps**:
    *   Select Bird -> "Generate Card."
    *   Select Template: "Battle Ready," "Breeder Stock," "Champion Line."
    *   **Interaction**: User tilts phone to see parallax sheen on the card.
    *   **Share**: Post to "The Arena" (Public Feed) or WhatsApp/Instagram.

---

## 🛠️ Feature Inventory & Specifications

### 📱 1. Command Center (Home Tab)
*   **Asset**: `EnthusiastHomeScreen.kt`
*   **Key Components**:
    *   **Hero Header**: Dynamic background of their highest-rated bird.
    *   **Reputation Widget**: Glows based on Trust Score (verified trades, successful hatches).
    *   **Speed Dial**: Floating Action Button expands for: "Quick Egg," "Quick Feed," "New Note."
    *   **Pulse Feed**: Horizontal scroll of "Events Today" (Hatching, Vaccinations, Tournament).

### 🔍 2. The Arena (Explore Tab)
*   **Asset**: `EnthusiastExploreScreen.kt`
*   **Design**:
    *   **Immersive Feed**: Full-screen vertical swipe (TikTok style) for video/photo showcases.
    *   **Masonry Grid**: For browsing breeder catalogs (Pinterest style).
    *   **Interactions**: Double-tap to "Respect" (custom icon animation).
    *   **Filters**: "Nearby," "Verified Breeders," "Top Bloodlines."

### 🎬 3. Media Studio (Create Tab)
*   **Asset**: `EnthusiastCreateScreen.kt`
*   **Features**:
    *   **Rooster Card Generator**: Uses `SensorManager` for parallax preview. Overlays stats on photo.
    *   **Tournament Log**: Input opponent details, round times, outcome (Win/Draw/Loss). Adds to bird's "Record."
    *   **Live Broadcast Setup**: Schedule a "Coop Tour" or "Training Session."

### 📊 4. War Room (Dashboard/Analytics Tab)
*   **Asset**: `EnthusiastDashboardScreen.kt`, `EnthusiastDashboardTabs.kt`
*   **Metrics**:
    *   **FCR (Feed Conversion)**: But styled as "Fuel Efficiency."
    *   **Breeding Success Rate**: "Genetic Potency."
    *   **ROI**: "Winnings vs. Costs."
    *   **Family Tree Graph**: Interactive node graph of lineage.

### 🤝 5. Network (Transfers Tab)
*   **Asset**: `EnthusiastTransfersScreen.kt`
*   **Features**:
    *   **Secure Handshake**: QR Code generation for in-person bird handovers.
    *   **Escrow Status**: Status flow for shipped birds.
    *   **Dispute Resolution**: "Report Issue" flow with photo evidence.

### 🎨 6. Bird Studio V2 (Appearance Customization)
*   **Asset**: `BirdStudioScreen.kt`, `BirdPartRenderer.kt`
*   **Purpose**: Deep bird appearance customization for enthusiasts — the "dress up" screen for roosters.
*   **Key Components**:
    *   **6 Appearance Dimensions**: Stance (6 postures), Sheen (6 finishes), NeckStyle (6 shapes), BreastShape (5 types), SkinColor (5 tones), HeadShape (5 forms)
    *   **Category Tabs**: Body, Head, Legs, Plumage, BUILD (💪), Colors — 20+ sub-parts total
    *   **Universal Breed Presets**: 15 common breed presets (RIR, Leghorn, Brahma, Silkie, etc.) + Aseel breed-specific presets
    *   **Randomize Button**: One-tap random appearance generation
    *   **Live Canvas Preview**: Real-time rendering of all customization changes
    *   **Rendering**: Stance offset, sheen overlay, neck shapes (arched/muscular/hackle), breast proportions, head shape variants, skin tints
    *   **Persistence**: JSON serialization with backward-compatible deserialization

---

## 🧬 Data Structure Improvements

### New Entities Needed
*   **`TournamentRecord`**:
    *   `id`, `birdId`, `opponentName`, `location`, `date`, `result` (Win/Loss/Draw), `durationSeconds`, `notes`.
*   **`ShowcasePost`**:
    *   `id`, `creatorId`, `assetId` (Bird), `mediaUrl`, `type` (Card/Video/Photo), `likes`, `views`.
*   **`ReputationScore`**:
    *   `userId`, `score` (0-100), `badges` (List<String>), `level` (Novice -> Grandmaster).

### Enhanced Entities
*   **`ProductEntity` / `FarmAssetEntity`**:
    *   Add `customAttributes`: JSON blob for "Eye Color," "Shank Color," "Comb Type" (crucial for enthusiasts).
    *   Add `achievements`: List of tournament wins.
*   **`BirdAppearance` (V2)**:
    *   6 new enums: `Stance`, `Sheen`, `NeckStyle`, `BreastShape`, `SkinColor`, `HeadShape`
    *   Backward-compatible JSON serialization via `toAppearanceJson()` / `parseAppearanceFromJson()`
    *   `displayName()` extension functions for all enums

---

## 🚀 Implementation Roadmap

### Phase 1: Visual Overhaul (Sprint 5)
*   [ ] Implement "Glassmorphic" theme tokens.
*   [ ] Build `RoosterCard` composable with accelerometer support.
*   [ ] Refactor Home Screen to "Command Center" layout.

### Phase 2: Core Loop Gamification (Sprint 6)
*   [ ] Create "Quick Action" speed dial with haptics.
*   [ ] Build "Incubation Ring" widget.
*   [ ] Implement "Interactive Egg Log" (Slider interface).

### Phase 3: The Arena & Social (Sprint 7)
*   [ ] Build Masonry Grid for Explore.
*   [ ] Implement "Showcase" data flow.
*   [ ] Add "Reputation" tracking logic.

---

## 🛡️ Technical Considerations
*   **Performance**: High-res images + blur effects = expensive. Use `RenderScript` or optimized Compose `Blur` modifiers carefully. Scale down list images.
*   **Sensors**: `SensorManager` listeners must be registered/unregistered in `DisposableEffect` to avoid battery drain.
*   **Offline**: All specific data (Tournament records) must be Room-cached.

---

**End of Blueprint**
