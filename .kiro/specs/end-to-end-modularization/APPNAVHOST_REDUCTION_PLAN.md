# AppNavHost Reduction Plan

## Current State
- **File**: `app/src/main/java/com/rio/rostry/ui/navigation/AppNavHost.kt`
- **Current Size**: 3,559 lines
- **Target Size**: <500 lines
- **Reduction Needed**: ~3,000 lines (84% reduction)

## Problem Analysis
AppNavHost currently contains hundreds of `composable(Routes.XXX)` definitions that should be delegated to NavigationProviders in feature modules. The NavigationRegistry pattern is already implemented and working, but AppNavHost hasn't been refactored to use it properly.

## Strategy

### Phase 1: Identify What to Keep in AppNavHost
AppNavHost should ONLY contain:
1. **Auth Flow** - Login, onboarding, splash (handled by auth feature modules)
2. **Role Scaffold** - RoleNavScaffold wrapper with bottom navigation
3. **Deep Link Handling** - Initial deep link routing
4. **Session Management** - Session validity checks
5. **NavigationRegistry Integration** - Call to `navigationRegistry.buildGraphs()`

### Phase 2: Identify What to Remove from AppNavHost
Remove all feature-specific composable() definitions that should be in NavigationProviders:
- Farmer navigation (home, market, create, community, profile, assets, etc.)
- Enthusiast navigation (home, explore, create, dashboard, transfers, breeding, etc.)
- Monitoring navigation (tasks, performance, vaccination, mortality, growth, breeding, etc.)
- Marketplace navigation (listings, orders, etc.)
- Social navigation (feed, messages, groups, etc.)
- Profile navigation (profile, edit profile, storage quota, etc.)
- Admin navigation (dashboard, moderation, etc.)
- Analytics navigation (reports, profitability, etc.)

### Phase 3: Execution Steps

#### Step 1: Analyze NavigationProviders
Check which routes are already handled by existing NavigationProviders:
- feature/farm-dashboard
- feature/asset-management
- feature/listing-management
- feature/achievements
- feature/events
- feature/expert
- feature/feedback
- feature/insights
- feature/leaderboard
- feature/notifications
- feature/onboarding
- feature/support
- feature/login

#### Step 2: Create Missing NavigationProviders
For routes not yet handled, create NavigationProviders in appropriate feature modules:
- feature/farmer-tools (for farmer home, market, create, community, profile)
- feature/enthusiast-tools (for enthusiast home, explore, create, dashboard, transfers)
- feature/monitoring (for monitoring screens)
- feature/marketplace (for marketplace screens)
- feature/social-feed (for social screens)
- feature/profile (for profile screens)
- feature/admin-dashboard (for admin screens)
- feature/analytics (for analytics screens)

#### Step 3: Remove Composable Definitions
Remove all composable() definitions from AppNavHost that are now handled by NavigationProviders.

#### Step 4: Remove Unused Imports
Remove all imports from old app.ui.* packages that are no longer needed.

#### Step 5: Verify
- Verify AppNavHost is under 500 lines
- Verify navigation still works
- Run architecture tests

## Next Actions
1. Start with Step 1: Analyze existing NavigationProviders
2. Proceed incrementally through each step
3. Test after each major change
