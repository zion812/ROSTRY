# 🎯 IMMEDIATE ACTION ITEMS

## Quick Wins (Can Fix Now)

### 1. Replace Hard-Coded Delivery Radius Values
**Time**: 15 minutes  
**Impact**: HIGH - Consistency across codebase

**Files to Update**:
- `HubAssignmentService.kt`: Change `100.0` to `BusinessConstants.MAX_DELIVERY_RADIUS_KM`
- `LocationService.kt`: Change `50.0` to `BusinessConstants.DEFAULT_DELIVERY_RADIUS_KM`
- `AppConfiguration.kt`: Use constant instead of hard-coded value

### 2. Replace Logistics Fee Hard-Code
**Time**: 10 minutes  
**Impact**: HIGH - Pricing consistency

**Files to Update**:
- `PaymentRepository.kt` (2 instances): Replace `50.0` with `BusinessConstants.LOGISTICS_FEE_RUPEES`
- `PaymentRepositoryImpl.kt`: Same replacement

### 3. Create StorageConfig for Firebase URLs
**Time**: 30 minutes  
**Impact**: HIGH - Environment management

**Action**: Create `core/common/src/main/java/com/rio/rostry/core/common/config/StorageConfig.kt`

### 4. Fix Placeholder Avatar URLs
**Time**: 20 minutes  
**Impact**: MEDIUM - Better UX

**Action**: Replace `via.placeholder.com` with local drawable resources

### 5. Add API Key Validation
**Time**: 30 minutes  
**Impact**: CRITICAL - Prevent crashes

**Action**: Add startup validation in `RostryApp.kt`

---

## Critical Stub Implementations (Priority Order)

### Priority 1: AdminProductRepositoryImpl
**Time**: 2-3 hours  
**Impact**: CRITICAL - Admin features broken

**Methods to Implement**:
1. `getAllProductsAdmin()` - Query all products with admin fields
2. `getFlaggedProducts()` - Filter by adminFlagged = true
3. `clearFlag()` - Update adminFlagged field

### Priority 2: TraceabilityRepositoryImpl
**Time**: 3-4 hours  
**Impact**: HIGH - Traceability features incomplete

**Methods to Implement**:
1. `getTransferChain()` - Query transfer history
2. `getComplianceAlerts()` - Check for missing data
3. `getSiblings()` - Query by parent IDs

### Priority 3: RecommendationEngine
**Time**: 4-6 hours  
**Impact**: HIGH - No product recommendations

**Methods to Implement**:
1. `getFrequentlyBoughtTogether()` - Co-occurrence analysis
2. `getPersonalizedRecommendations()` - User behavior tracking
3. `getSimilarProducts()` - Content-based filtering

---

## Files Requiring Immediate Attention

1. `AdminProductRepositoryImpl.kt` - All methods stubbed
2. `TraceabilityRepositoryImpl.kt` - 3 critical methods stubbed
3. `CommunityEngagementService.kt` - Social features missing
4. `ProfitabilityRepositoryImpl.kt` - Advanced queries needed
5. `WorkflowOrchestrator.kt` - Placeholder implementation

---

**Total Estimated Time for Quick Wins**: 2 hours  
**Total Estimated Time for Critical Stubs**: 10-15 hours  
**Recommended Sprint**: 1 week for all immediate items
