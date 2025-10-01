# ROSTRY User Experience Guidelines

These guidelines ensure consistent, accessible, and emotionally engaging experiences across the ROSTRY app. They document UX patterns, components, and best practices implemented throughout the application.

**Last Updated**: 2025-10-01  
**Version**: 2.0

## Table of Contents

- [Core Principles](#core-principles)
- [Multi-Step Wizards](#multi-step-wizards)
- [Filter Presets](#filter-presets)
- [Guided Flows](#guided-flows)
- [Component Library](#component-library)
- [Design Patterns](#design-patterns)
- [Accessibility](#accessibility)
- [Implementation Checklists](#implementation-checklists)

---

## Core Principles

### 1. Empathy First
- **Farm Context**: Consider users may be outdoors, wearing gloves, or multitasking
- **Connectivity**: Design for offline-first; handle poor network gracefully
- **Time Constraints**: Respect that farmers have limited time during peak seasons
- **Language**: Use simple, direct language; avoid jargon

### 2. Clarity Over Cleverness
- Direct, actionable language
- Clear visual hierarchy
- Predictable navigation patterns
- Explicit error messages with solutions

### 3. Delight With Purpose
- Celebrate meaningful milestones (first sale, vaccination completion, breeding success)
- Use micro-interactions that communicate state
- Provide positive feedback for user actions
- Avoid unnecessary animations that slow down workflows

---

## Multi-Step Wizards

Break complex forms into manageable steps for reduced cognitive load.

### Pattern: 4-Step Wizard

**Example**: `FarmerCreateScreen` - Product Creation Wizard

#### Structure
```
BASICS → DETAILS → MEDIA → REVIEW
```

#### Implementation
```kotlin
@Composable
fun FarmerCreateScreen(
    viewModel: FarmerCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val wizardState by viewModel.wizardState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Product") },
                navigationIcon = { BackButton(onClick = onNavigateBack) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Progress indicator
            WizardProgressIndicator(
                currentStep = wizardState.currentStep.ordinal + 1,
                totalSteps = 4
            )
            
            // Step content
            when (wizardState.currentStep) {
                WizardStep.BASICS -> BasicInfoStep(...)
                WizardStep.DETAILS -> DetailsStep(...)
                WizardStep.MEDIA -> MediaStep(...)
                WizardStep.REVIEW -> ReviewStep(...)
            }
            
            // Navigation buttons
            WizardNavigationButtons(
                canGoBack = wizardState.currentStep != WizardStep.BASICS,
                canGoNext = wizardState.isCurrentStepValid,
                isLastStep = wizardState.currentStep == WizardStep.REVIEW,
                onBack = { viewModel.previousStep() },
                onNext = { viewModel.nextStep() },
                onSubmit = { viewModel.submitWizardListing() }
            )
        }
    }
}
```

#### ViewModel Pattern
```kotlin
@HiltViewModel
class FarmerCreateViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    
    private val _wizardState = MutableStateFlow(WizardState())
    val wizardState: StateFlow<WizardState> = _wizardState.asStateFlow()
    
    fun nextStep() {
        if (validateStep(_wizardState.value.currentStep)) {
            _wizardState.update { it.copy(currentStep = it.currentStep.next()) }
        }
    }
    
    fun previousStep() {
        _wizardState.update { it.copy(currentStep = it.currentStep.previous()) }
    }
    
    private fun validateStep(step: WizardStep): Boolean {
        // Step-specific validation
        return when (step) {
            WizardStep.BASICS -> validateBasicInfo()
            WizardStep.DETAILS -> validateDetails()
            WizardStep.MEDIA -> true // Optional
            WizardStep.REVIEW -> true
        }
    }
}
```

#### Key Features
- **Progress Indicator**: Shows current step (e.g., "Step 2 of 4")
- **Step Validation**: Each step validates before advancing
- **Back Navigation**: User can go back without losing data
- **Review Step**: Summary of all entered data before submission
- **Inline Errors**: Show validation errors immediately
- **Save State**: Preserve wizard state across configuration changes

#### When to Use
- Forms with 8+ fields
- Complex multi-section data entry
- Conditional field visibility
- Forms requiring file uploads
- Forms needing final review before submission

---

## Filter Presets

Quick one-tap filter combinations for common use cases.

### Pattern: Compact Filter Bar with Presets

**Example**: `GeneralMarketRoute` - Marketplace Filters

#### Implementation
```kotlin
@Composable
fun CompactFilterBar(
    activeFilterCount: Int,
    presets: List<FilterPreset>,
    onFiltersClick: () -> Unit,
    onPresetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filter button with badge
        BadgedBox(
            badge = {
                if (activeFilterCount > 0) {
                    Badge { Text(activeFilterCount.toString()) }
                }
            }
        ) {
            OutlinedButton(onClick = onFiltersClick) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Filters")
            }
        }
        
        // Preset chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presets) { preset ->
                FilterChip(
                    selected = false,
                    onClick = { onPresetClick(preset.id) },
                    label = { Text(preset.name) }
                )
            }
        }
    }
}
```

#### Preset Definitions
```kotlin
enum class QuickPreset(val displayName: String, val description: String) {
    NEARBY_VERIFIED("Nearby & Verified", "Within 25km, verified sellers only"),
    TRACEABLE_ONLY("Traceable Only", "Products with full traceability"),
    BUDGET_FRIENDLY("Budget Friendly", "Under ₹500, sorted by price"),
    PREMIUM("Premium", "Verified, traceable, high-quality products")
}
```

#### ViewModel Integration
```kotlin
fun applyQuickPreset(presetId: String) {
    val preset = when (presetId) {
        "NEARBY_VERIFIED" -> MarketFilters(
            nearbyEnabled = true,
            verifiedOnly = true,
            radiusKm = 25
        )
        "TRACEABLE_ONLY" -> MarketFilters(traceableOnly = true)
        "BUDGET_FRIENDLY" -> MarketFilters(
            maxPrice = 500.0,
            sortBy = SortOption.PRICE_LOW_TO_HIGH
        )
        "PREMIUM" -> MarketFilters(
            verifiedOnly = true,
            traceableOnly = true,
            minPrice = 1000.0
        )
        else -> MarketFilters()
    }
    _uiState.update { it.copy(filters = preset) }
    applyFilters()
}
```

#### Key Features
- **Badge Display**: Shows count of active filters
- **One-Tap Application**: Instant filter application
- **Visual Feedback**: Chip selection indicates active preset
- **Clear Labels**: Descriptive names for each preset
- **Easy Removal**: Tap "Filters" to see and modify individual filters

#### When to Use
- Lists with 5+ filterable attributes
- Common filtering patterns emerge from user behavior
- Users repeatedly apply same filter combinations
- Reducing decision fatigue is important

---

## Guided Flows

Step-by-step guidance through complex business processes.

### Pattern: Guided Transfer Flow

**Example**: `TransferCreateScreen` - Fowl Transfer Initiation

#### Flow Structure
```
1. Transfer Type Selection
   ↓
2. Product Selection (Bottom Sheet)
   ↓
3. Recipient Selection (Search Sheet)
   ↓
4. Confirmation & Review
   ↓
5. Submission
```

#### Implementation
```kotlin
@Composable
fun TransferCreateScreen(
    viewModel: TransferCreateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    if (!state.confirmationStep) {
        // Step 1-3: Form
        TransferFormStep(
            selectedType = state.transferType,
            selectedProduct = state.selectedProduct,
            selectedRecipient = state.selectedRecipient,
            amount = state.amount,
            notes = state.notes,
            validationErrors = state.validationErrors,
            onTypeSelect = { viewModel.setTransferType(it) },
            onProductClick = { viewModel.showProductPicker() },
            onRecipientClick = { viewModel.showRecipientPicker() },
            onAmountChange = { viewModel.updateAmount(it) },
            onNotesChange = { viewModel.updateNotes(it) },
            onProceedToConfirmation = { viewModel.proceedToConfirmation() }
        )
    } else {
        // Step 4: Confirmation
        ConfirmationStep(
            transferType = state.transferType,
            product = state.selectedProduct,
            recipient = state.selectedRecipient,
            amount = state.amount,
            notes = state.notes,
            isLoading = state.isLoading,
            onConfirm = { viewModel.confirmAndSubmit() },
            onBack = { viewModel.backToForm() }
        )
    }
    
    // Bottom sheets
    if (state.showProductPicker) {
        ProductPickerSheet(
            products = state.availableProducts,
            onProductSelect = { viewModel.selectProduct(it) },
            onDismiss = { viewModel.hideProductPicker() }
        )
    }
    
    if (state.showRecipientPicker) {
        RecipientPickerSheet(
            searchQuery = state.recipientSearchQuery,
            searchResults = state.searchResults,
            onSearchQueryChange = { viewModel.searchRecipients(it) },
            onRecipientSelect = { viewModel.selectRecipient(it) },
            onDismiss = { viewModel.hideRecipientPicker() }
        )
    }
}
```

#### Key Features
- **Visual Step Indicators**: User knows where they are in the process
- **Picker Sheets**: Modal bottom sheets for selection instead of dropdowns
- **Confirmation Step**: Review all details before final submission
- **Inline Validation**: Real-time error feedback
- **Loading States**: Disabled buttons during submission
- **Auto-Navigation**: Close screen on success

#### When to Use
- Multi-entity operations (selecting from multiple resources)
- High-stakes actions requiring confirmation
- Processes with conditional steps
- Forms where users need to search for values
- Actions that can't be easily undone

---

## Component Library

Reusable UX components for consistent experience.

### 1. Form Validation Helpers

**File**: `utils/FormValidationHelpers.kt`

#### Usage
```kotlin
val result = FormValidator()
    .field("title", title)
        .required()
        .minLength(3)
        .maxLength(100)
    .field("price", priceText)
        .required()
        .numeric()
        .positiveNumber()
    .field("email", email)
        .required()
        .email()
    .validate()

if (!result.isValid) {
    // Show errors: result.errors
}
```

#### Available Validators
- `required()`, `minLength()`, `maxLength()`
- `numeric()`, `positiveNumber()`, `inRange()`
- `email()`, `phone()`, `dateFormat()`
- `oneOf()` - value must be in list

### 2. Loading States

**File**: `ui/components/LoadingStates.kt`

#### Skeleton Loaders
```kotlin
// Generic skeleton
SkeletonLoader(
    modifier = Modifier.fillMaxWidth().height(80.dp)
)

// Specialized skeletons
ProductCardSkeleton()
ListItemSkeleton()
MetricCardSkeleton()
PostCardSkeleton()
FormFieldSkeleton()
```

#### Empty States
```kotlin
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    message = "Start adding products to see them here",
    actionLabel = "Add Product",
    onActionClick = { /* navigate to create */ }
)
```

#### Error States
```kotlin
ErrorState(
    title = "Failed to load products",
    message = error.message ?: "Something went wrong",
    onRetry = { viewModel.loadProducts() }
)
```

### 3. Onboarding Tooltips

**File**: `ui/components/OnboardingTooltips.kt`

#### Usage
```kotlin
TooltipAnchor(
    tooltipId = "marketplace_filters",
    title = "Filter Products",
    description = "Tap here to filter by location, breed, age, and more",
    placement = TooltipPlacement.BOTTOM
) {
    Button(onClick = { showFilters() }) {
        Text("Filters")
    }
}
```

#### Predefined Tooltip IDs
- `"marketplace_filters"` - Marketplace filter button
- `"explore_search_syntax"` - Search tips
- `"farm_monitoring_quick_actions"` - Farm dashboard actions
- `"transfer_product_picker"` - Transfer product selection
- `"community_discover_tab"` - Community recommendations
- `"product_creation_wizard"` - First-time wizard guidance

### 4. Success Animations

**File**: `ui/components/SuccessAnimations.kt`

#### Celebration Dialog
```kotlin
CelebrationDialog(
    visible = showSuccess,
    title = "Listing Published!",
    message = "Your product is now live on the marketplace",
    animation = AnimationType.CONFETTI,
    onDismiss = { showSuccess = false }
)
```

#### Animation Types
- `AnimationType.CHECKMARK` - Simple checkmark
- `AnimationType.CONFETTI` - Celebratory confetti
- `AnimationType.FIREWORKS` - Fireworks effect
- `AnimationType.CUSTOM` - Custom Lottie animation

#### Haptic Feedback
```kotlin
HapticFeedback.success() // Success vibration
HapticFeedback.error() // Error vibration
HapticFeedback.selection() // Selection tap
HapticFeedback.impact() // Button press
```

### 5. Help Components

**File**: `ui/components/HelpComponents.kt`

#### Field with Help
```kotlin
FieldWithHelp(
    value = title,
    onValueChange = { title = it },
    label = "Product Title",
    helpTitle = "Choosing a Title",
    helpContent = HelpContent.PRODUCT_TITLE
)
```

#### Inline Help
```kotlin
InlineHelp(
    text = "This helps buyers find your product more easily",
    type = HelpType.TIP
)
```

#### Help Dialog
```kotlin
HelpButton(
    title = "What is Traceability?",
    content = HelpContent.TRACEABILITY,
    actionLabel = "Learn More",
    actionUrl = "https://rostry.com/help/traceability"
)
```

---

## Design Patterns

### Progressive Disclosure

Reveal complexity gradually as needed.

**Example**: Show basic fields first, advanced fields on demand.

```kotlin
var showAdvanced by remember { mutableStateOf(false) }

Column {
    // Basic fields always visible
    OutlinedTextField(value = title, ...)
    OutlinedTextField(value = price, ...)
    
    // Advanced fields on demand
    if (showAdvanced) {
        OutlinedTextField(value = sku, ...)
        OutlinedTextField(value = barcode, ...)
    }
    
    TextButton(onClick = { showAdvanced = !showAdvanced }) {
        Text(if (showAdvanced) "Hide Advanced" else "Show Advanced")
    }
}
```

### Contextual Help

Provide help exactly when and where users need it.

**Good**:
- Help icon next to complex field labels
- Inline tips below fields
- "Why we need this" explanations

**Bad**:
- Generic help button in toolbar
- Help only in separate help center
- No explanation for unusual requests

### Inline Validation

Validate as users type, not just on submit.

```kotlin
OutlinedTextField(
    value = email,
    onValueChange = { 
        email = it
        emailError = validateEmail(it)
    },
    isError = emailError != null,
    supportingText = emailError?.let { { Text(it) } }
)
```

### Success Feedback

Always confirm successful actions.

**Levels**:
1. **Minor**: Toast message
2. **Moderate**: Snackbar with undo
3. **Major**: Full-screen celebration dialog

### Empty States

Guide users to their first action.

**Components**:
- Icon (illustrative)
- Title (what's missing)
- Message (why it's empty)
- Action button (what to do)

### Loading States

Show skeleton loaders instead of spinners for better perceived performance.

---

## Accessibility

### Dynamic Text Sizing

Support Android's text scaling preferences.

```kotlin
Text(
    text = title,
    style = MaterialTheme.typography.titleLarge,
    // Font will scale with system settings
)
```

### High Contrast

Use Material Theme colors that adapt to high contrast mode.

```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
)
```

### Motion Sensitivity

Respect reduced motion preferences.

```kotlin
val reduceMotion = LocalAccessibilityManager.current.reduceMotion

AnimatedVisibility(
    visible = showDialog,
    enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically(),
    exit = if (reduceMotion) fadeOut() else fadeOut() + slideOutVertically()
)
```

### Touch Targets

Minimum 48dp × 48dp for all interactive elements.

```kotlin
IconButton(
    onClick = { },
    modifier = Modifier.size(48.dp) // Minimum touch target
) {
    Icon(Icons.Default.Delete, contentDescription = "Delete")
}
```

### Content Descriptions

Provide meaningful descriptions for screen readers.

```kotlin
Icon(
    imageVector = Icons.Default.Verified,
    contentDescription = "Verified seller" // Not "Verified icon"
)
```

---

## Implementation Checklists

### Adding a New Screen

- [ ] Add route to `ui/navigation/Routes.kt`
- [ ] Integrate in `AppNavHost.kt`
- [ ] Create ViewModel with Hilt `@HiltViewModel`
- [ ] Define UI state with data class
- [ ] Implement loading/empty/error states
- [ ] Add accessibility content descriptions
- [ ] Test with TalkBack enabled
- [ ] Test with large text sizes
- [ ] Add analytics tracking for key actions
- [ ] Document in relevant docs/ file

### Adding Complex Form

- [ ] Consider wizard pattern if 8+ fields
- [ ] Implement inline validation
- [ ] Add FormValidationHelpers for consistency
- [ ] Include help components for complex fields
- [ ] Show loading state during submission
- [ ] Display success animation on completion
- [ ] Save form state in SavedStateHandle
- [ ] Add tooltip for first-time users
- [ ] Test keyboard navigation
- [ ] Test with screen reader

### Adding List/Feed

- [ ] Use Paging 3 for large lists
- [ ] Implement skeleton loaders
- [ ] Create empty state with action
- [ ] Add pull-to-refresh
- [ ] Include error state with retry
- [ ] Add filter/sort if applicable
- [ ] Consider filter presets for common cases
- [ ] Test with slow network
- [ ] Test offline behavior
- [ ] Add list item animations

---

**See also**:
- `CODE_STYLE.md` for code-level implementation details
- `architecture.md` for overall system structure
- `CONTRIBUTING.md` for contribution guidelines
