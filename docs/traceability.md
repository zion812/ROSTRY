Title: Traceability
Version: 1.0
Last Updated: 2025-10-29
Audience: Developers, Contributors

## Table of Contents
- [Lineage Tracking](#lineage-tracking)
- [Lifecycle Events](#lifecycle-events)
- [Data Models](#data-models)
- [UI Components](#ui-components)
- [Implementation Guide](#implementation-guide)

```kotlin
@Entity(
    tableName = "family_tree",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["parentProductId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["childProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("productId"),
        Index("parentProductId"),
        Index("childProductId"),
        Index(value = ["productId", "parentProductId", "childProductId"], unique = true)
    ]
)
data class FamilyTreeEntity(
    @PrimaryKey val nodeId: String,
    val productId: String,
    val parentProductId: String?,
    val childProductId: String?,
    val relationType: String? = null, // e.g., parent, child, sibling, etc.
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
```

**Implementation Status**: FamilyTreeEntity is fully implemented in the database schema. UI components (FamilyTreeView, LineagePreviewScreen) and repository methods are in active development.

Key components:
- **nodeId**: Unique identifier for each tree node
- **productId**: Links to the specific poultry product
- **parentProductId/childProductId**: Define parent-child relationships
- **relationType**: Specifies relationship type (parent, child, sibling)
- **Timestamps**: Track creation and modification times
- **Soft Delete**: Maintains data integrity with deletion flags

### FamilyTreeView.kt: UI Component for Visualizing Family Trees

The `FamilyTreeView` composable provides interactive visualization of lineage relationships:

```kotlin
@Composable
fun FamilyTreeView(
    rootProductId: String,
    onNodeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation for tree visualization
    // Supports zoom, pan, and node interaction
    // Displays generations with connecting lines
    // Shows relationship types and metadata
}
```

Features include:
- **Interactive Navigation**: Click nodes to explore lineage
- **Zoom and Pan**: Navigate large family trees
- **Visual Relationships**: Lines connecting parents to offspring
- **Generation Layout**: Hierarchical display by generations
- **Metadata Display**: Show key information on hover/select

### Parent-Child Relationships

The system supports complex relationship modeling:
- **Direct Parentage**: Mother-father to offspring links
- **Multi-Generational**: Track ancestry across multiple generations
- **Half-Siblings**: Handle complex breeding scenarios
- **Adopted Relationships**: Manual relationship assignments

### Multi-Generation Tracking

Family trees can span multiple generations:
- **Depth Limiting**: Configurable maximum generations to display
- **Lazy Loading**: Load deeper generations on demand
- **Performance Optimization**: Efficient queries for large trees
- **Visualization Scaling**: Adjust layout for different tree sizes

### Breeding Pair Associations

Integration with breeding records:
- **Pair Linking**: Associate offspring with specific breeding pairs
- **Success Metrics**: Track breeding pair performance
- **Genetic Tracking**: Follow trait inheritance patterns
- **Historical Records**: Maintain breeding history over time

## Lineage Tracking

Lineage tracking captures and maintains the complete ancestry and progeny information for each poultry product.

### How Lineage Data is Captured

Data capture occurs through multiple channels:
- **Breeding Events**: Automatic recording when offspring are registered
- **Manual Entry**: Farmer input for existing or imported poultry
- **Transfer Integration**: Update lineage during ownership changes
- **Health Records**: Associate medical history with lineage

### Breeding Record Integration

Seamless integration with breeding management:
- **Pair Identification**: Link offspring to specific breeding pairs
- **Hatching Records**: Timestamp and location of birth events
- **Genetic Data**: Capture breed, strain, and trait information
- **Performance Tracking**: Monitor growth and health metrics

### Genetic Trait Tracking

Track inheritable characteristics:
- **Physical Traits**: Feather color, size, conformation
- **Performance Metrics**: Growth rate, feed efficiency, egg production
- **Health Predispositions**: Disease resistance, longevity
- **Behavioral Traits**: Temperament, social behavior

### Health History Inheritance

Maintain health records across generations:
- **Disease History**: Track inherited or familial health conditions
- **Vaccination Records**: Follow vaccination schedules by lineage
- **Treatment History**: Document medical interventions
- **Mortality Patterns**: Identify familial health trends

### Performance Metrics Across Generations

Track and analyze performance data:
- **Growth Rates**: Compare weight gain across generations
- **Reproductive Success**: Monitor breeding pair effectiveness
- **Economic Metrics**: Track profitability by lineage
- **Quality Scores**: Maintain grading and certification data

## Lifecycle Events

Lifecycle events provide a chronological record of all significant events in a poultry product's life.

### Birth/Hatching Records

Document the beginning of life:
- **Hatching Date and Time**: Precise timestamp of birth
- **Location**: Farm, incubator, or hatchery details
- **Breeding Pair**: Parent identification
- **Initial Health Assessment**: Birth weight, condition
- **Genetic Information**: Breed, strain, expected traits

### Vaccination History

Track immunization records:
- **Vaccine Type**: Specific vaccines administered
- **Administration Date**: When vaccination occurred
- **Dosage Information**: Amount and method of delivery
- **Batch Numbers**: Vaccine lot tracking
- **Next Due Dates**: Schedule future vaccinations

### Growth Milestones

Record developmental progress:
- **Weight Measurements**: Regular growth tracking
- **Age Milestones**: Key developmental stages
- **Feather Development**: Plumage growth and quality
- **Behavioral Development**: Maturity indicators

### Breeding Events

Document reproductive activities:
- **Breeding Pair Formation**: When pairs are established
- **Mating Records**: Successful breeding events
- **Egg Production**: Fertility and hatchability data
- **Offspring Registration**: New generation tracking

### Transfer/Ownership Changes

Track movement and ownership:
- **Transfer Records**: Complete audit trail of ownership changes
- **Location Updates**: Movement between farms or facilities
- **Purpose Documentation**: Reason for transfer (sale, breeding, etc.)
- **Verification**: Confirmation of legitimate transfers

### End-of-Life Records

Document final lifecycle events:
- **Culling Reasons**: Why the bird was removed from production
- **Processing Information**: If applicable for market
- **Cause of Death**: Health or other factors
- **Final Disposition**: Sale, consumption, or disposal

## Data Models

The traceability system relies on several interconnected data models to maintain comprehensive lineage information.

### FamilyTreeEntity Structure

As detailed above, the core entity includes:
- **Primary Key**: nodeId for unique identification
- **Product Links**: Connect to ProductEntity records
- **Relationship Fields**: parent/child product IDs
- **Metadata**: Timestamps and deletion flags

### Relationships with BreedingPairEntity

While BreedingPairEntity implementation details are pending, the integration will include:
- **Pair Identification**: Unique IDs for breeding pairs
- **Parent Links**: Connect pairs to offspring in family trees
- **Performance Data**: Breeding success rates and metrics
- **Genetic Information**: Trait inheritance tracking

### Integration with TransferEntity

Transfer records will connect with lineage:
- **Ownership Chain**: Track product ownership through transfers
- **Lineage Continuity**: Maintain lineage integrity across transfers
- **Verification**: Use lineage data to validate transfer legitimacy
- **Audit Trail**: Complete history of product movement

### Database Schema and Indices

The database schema is optimized for performance:
- **Foreign Key Constraints**: Ensure referential integrity
- **Composite Indices**: Efficient queries for relationships
- **Timestamps**: Support for auditing and conflict resolution
- **Soft Deletes**: Maintain historical data integrity

## UI Components

The user interface provides intuitive access to traceability features.

### FamilyTreeView: Tree Visualization Component

The main visualization component offers:
- **Hierarchical Display**: Generations arranged in levels
- **Interactive Nodes**: Click to view details or navigate
- **Zoom Controls**: Scale the view for large trees
- **Pan Functionality**: Navigate around the tree
- **Relationship Lines**: Visual connections between nodes

### LineagePreviewScreen: Preview and Navigation

As implemented in `LineagePreviewScreen.kt`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineagePreviewScreen(
    productId: String,
    onOpenFullTree: (String) -> Unit,
    onBack: () -> Unit
) {
    // Preview implementation with shareable links
    // Button to open full family tree
    // Lightweight preview functionality
}
```

Features:
- **Quick Preview**: Fast access to lineage information
- **Shareable Links**: Generate URLs for sharing lineage
- **Navigation**: Easy access to full tree view
- **Metadata Display**: Key information at a glance

### Interactive Tree Navigation

Advanced navigation features:
- **Node Expansion**: Drill down into specific branches
- **Search Functionality**: Find specific products in the tree
- **Filtering Options**: Show/hide generations or relationship types
- **Breadcrumb Navigation**: Track navigation path

### Filtering and Search Within Lineage

Powerful filtering capabilities:
- **Generation Filtering**: Limit display to specific generations
- **Date Range Filtering**: Show events within time periods
- **Trait Filtering**: Filter by genetic or performance traits
- **Health Status**: Filter by vaccination or health records

### Export Lineage Reports

Data export functionality:
- **PDF Reports**: Formatted family tree documents
- **CSV Export**: Tabular data for analysis
- **Shareable Links**: Web-accessible lineage views
- **Print Optimization**: Printer-friendly layouts

## Implementation Guide

This section provides guidance for developers working with the traceability system.

### How to Record Breeding Events

Recording breeding events involves:
1. **Create Breeding Pair Record**: Establish the breeding pair
2. **Log Mating Event**: Record successful breeding
3. **Register Offspring**: Create product records for new birds
4. **Update Family Tree**: Link offspring to parents

### How to Link Offspring to Parents

The linking process:
1. **Identify Parents**: Reference breeding pair records
2. **Create Family Tree Nodes**: Generate nodes for each relationship
3. **Set Relationship Types**: Define parent-child connections
4. **Update Timestamps**: Record when relationships are established

### How to Query Lineage Data

Querying lineage information:
- **Repository Methods**: Use TraceabilityRepository for data access
- **Recursive Queries**: Handle multi-generational relationships
- **Performance Optimization**: Use efficient database queries
- **Caching Strategies**: Cache frequently accessed lineage data

### Repository Methods for Traceability

Key repository methods include:
- **getFamilyTree(productId)**: Retrieve complete family tree
- **getAncestors(productId, generations)**: Get ancestor lineage
- **getDescendants(productId, generations)**: Get progeny lineage
- **addLineageRelationship(parentId, childId)**: Create new relationships

### Code Examples from Actual Implementation

Example of adding a lineage relationship:

```kotlin
suspend fun addOffspringToFamilyTree(
    parentProductId: String,
    offspringProductId: String,
    relationType: String = "child"
) {
    val nodeId = generateNodeId()
    val familyTreeEntity = FamilyTreeEntity(
        nodeId = nodeId,
        productId = offspringProductId,
        parentProductId = parentProductId,
        relationType = relationType
    )
    traceabilityDao.insertFamilyTreeNode(familyTreeEntity)
}
