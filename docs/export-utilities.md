Title: Export Utilities
Version: 1.0
Last Updated: 2025-10-29
Audience: Developers, Contributors

## Table of Contents
- [PDF Export](#pdf-export)
- [Export Workflows](#export-workflows)
- [Data Preparation](#data-preparation)
- [Implementation Guide](#implementation-guide)
- [File Management](#file-management)
- [Performance Considerations](#performance-considerations)
- [User Experience](#user-experience)
- [Integration Examples](#integration-examples)

```kotlin
object CsvExporter {
    // Core writing functionality
    fun writeCsv(context: Context, fileName: String, headers: List<String>, rows: List<List<String>>): Uri
    
    // Specialized KPI export with metadata
    fun exportKpis(context: Context, kpis: Map<String, Any>, fileName: String, dateRange: Pair<Long, Long>?, units: Map<String, String>?): Resource<Uri>
    
    // Sharing utilities
    fun shareCsv(context: Context, uri: Uri, subject: String, title: String): Intent
    
    // Notification helpers
    fun showExportNotification(context: Context, uri: Uri, title: String, text: String)
}

**Implementation Status**: CsvExporter and PdfExporter utility classes are fully implemented. Integration with ViewModels and UI screens is in active development.
```

### Supported Data Types

CsvExporter supports various data types commonly used in ROSTRY:

- **Products**: Farm inventory with lineage, health status, and marketplace listings
- **Orders**: Transaction records including buyer/seller details, pricing, and fulfillment status
- **Farm Records**: Vaccination schedules, growth measurements, mortality logs, and breeding data
- **Analytics**: KPI metrics, performance indicators, and trend data
- **User Data**: Profile information, preferences, and engagement metrics (with privacy considerations)

### CSV Format and Structure

CSV files follow standard RFC 4180 specifications:

- **Header Row**: First row contains column names
- **Data Rows**: Subsequent rows contain corresponding values
- **Delimiter**: Comma (,) separator
- **Quoting**: Fields containing commas, quotes, or newlines are enclosed in double quotes
- **Escaping**: Internal quotes are escaped with double quotes ("")

Example CSV structure for farm products:
```
"Product ID","Name","Breed","Age (weeks)","Health Status","Vaccination Complete","Last Updated"
"PROD-001","Rhode Island Red","Rhode Island Red",12,"Healthy","Yes","2024-01-15"
"PROD-002","Leghorn","White Leghorn",8,"Quarantine","No","2024-01-14"
```

### Encoding and Special Character Handling

- **UTF-8 Encoding**: All CSV files use UTF-8 encoding to support international characters
- **Special Characters**: Commas in data are handled by quoting the entire field
- **Line Breaks**: Multi-line text is preserved within quoted fields
- **Unicode Support**: Full Unicode character set supported for breed names, locations, and notes

### File Location and Naming Conventions

CSV files are stored in the app's internal storage:

- **Directory**: `context.filesDir/reports/`
- **Naming Pattern**: `{type}_{timestamp}_{userId}.csv`
  - `type`: Data type (products, orders, analytics, etc.)
  - `timestamp`: ISO 8601 format (yyyy-MM-dd_HH-mm-ss)
  - `userId`: Anonymized user identifier for organization

Example: `farm_products_2024-01-15_12-30-45_user123.csv`

Files are accessible via FileProvider for secure sharing without exposing internal storage paths.

## PDF Export

PDF export provides rich, formatted reports with tables, headers, and visual elements for professional presentation.

### PdfExporter.kt: PDF Generation Functionality

The `PdfExporter` object handles PDF creation using Android's PdfDocument API:

```kotlin
object PdfExporter {
    data class TableSection(
        val title: String,
        val headers: List<String>,
        val rows: List<List<String>>
    )
    
    // Simple table export
    fun writeSimpleTable(context: Context, fileName: String, title: String, headers: List<String>, rows: List<List<String>>): Uri
    
    // Complex report with multiple sections
    fun writeReport(context: Context, fileName: String, docTitle: String, coverBitmap: Bitmap?, sections: List<TableSection>): Uri
}
```

### Report Templates and Layouts

PDF reports support multiple layout templates:

1. **Simple Table**: Single table with title and headers
2. **Multi-Section Report**: Multiple tables with section headers
3. **Cover Page**: Optional bitmap (QR code, logo) on first page
4. **Standard Layout**: A4 page size (595x842 points) with 40pt margins

Layout features:
- **Page Breaks**: Automatic pagination for large datasets
- **Font Sizing**: 18pt titles, 16pt section headers, 14pt body text, 13pt table data
- **Spacing**: Consistent 16-24pt line spacing
- **Table Formatting**: Pipe-separated columns with fixed-width layout

### Supported Report Types

ROSTRY generates various PDF report types:

- **Performance Reports**: Farm KPIs, growth metrics, and efficiency indicators
- **Financial Reports**: Revenue breakdowns, expense tracking, and profit analysis
- **Breeding Reports**: Pair performance, hatch rates, and genetic tracking
- **Health Reports**: Vaccination status, mortality analysis, and quarantine logs
- **Compliance Reports**: Audit trails, certification documentation, and regulatory submissions

### Formatting and Styling

PDF formatting includes:
- **Typography**: Sans-serif fonts for readability
- **Colors**: Black text on white background for printing compatibility
- **Alignment**: Left-aligned text with consistent indentation
- **Table Borders**: Visual separation using pipe characters and spacing

### Image and Chart Embedding

PDF reports can include:
- **Cover Images**: QR codes for report verification
- **Charts**: Embedded performance graphs (future enhancement)
- **Logos**: Brand elements for professional presentation
- **Photos**: Product images or farm documentation (planned feature)

## Export Workflows

Export workflows handle the complete process from user initiation to file delivery.

### User-Initiated Exports from UI

Users can trigger exports through various UI entry points:

1. **Analytics Dashboard**: "Export Report" buttons on ReportsScreen
2. **Farm Monitoring**: Export options in monitoring screens
3. **Settings**: Bulk export functionality for data backup
4. **Product Details**: Individual record exports

Workflow steps:
1. User selects export type and parameters (date range, filters)
2. UI shows progress indicator during generation
3. File is saved to internal storage
4. Share intent is launched for file distribution

### Scheduled Exports (via Workers)

Background workers automate periodic exports:

- **ReportingWorker**: Weekly generation of standard reports
- **AnalyticsAggregationWorker**: Daily KPI exports for archiving
- **FarmPerformanceWorker**: Monthly performance summaries

Scheduled exports:
- Run during low-usage hours (2-4 AM local time)
- Store files for 30 days before cleanup
- Send notifications when new reports are available

### Batch Export Operations

For large-scale exports, batch processing is used:

- **Chunked Processing**: Data processed in 1000-record chunks to prevent memory issues
- **Progress Tracking**: WorkManager progress updates for UI feedback
- **Resume Capability**: Failed exports can resume from last successful chunk
- **Parallel Processing**: Multiple export types can run simultaneously

### Export Progress and Notifications

Progress tracking includes:
- **Progress Indicators**: Circular progress bars in UI
- **Status Updates**: "Generating...", "Saving...", "Ready to share"
- **Notification Channel**: Dedicated "reports" channel for export completions
- **Foreground Service**: Large exports run as foreground services with persistent notifications

### Error Handling and Retry Logic

Robust error handling ensures reliability:

- **Validation**: Input validation before export starts
- **Retry Logic**: Automatic retry for transient failures (up to 3 attempts)
- **Fallback Options**: CSV fallback if PDF generation fails
- **User Feedback**: Clear error messages with recovery suggestions
- **Logging**: Detailed error logging for debugging

## Data Preparation

Data preparation transforms raw database records into export-ready formats.

### Data Aggregation for Exports

Export data is aggregated from multiple sources:

- **Room Database**: Primary data source for offline records
- **Repository Layer**: Business logic for data transformation
- **Real-time Sync**: Latest data from Firestore integration
- **Calculated Fields**: Derived metrics like averages, totals, and percentages

### Filtering and Date Range Selection

Flexible filtering options:

- **Date Ranges**: Custom start/end dates or predefined periods (last 7 days, month-to-date)
- **Status Filters**: Active/inactive records, completed/pending transactions
- **Category Filters**: Product types, farm locations, user roles
- **Data Scope**: User-owned data only, with privacy enforcement

### Data Transformation and Formatting

Data preparation includes:

- **Type Conversion**: Database types to human-readable strings
- **Unit Conversion**: Metric/imperial based on user preferences
- **Date Formatting**: Localized date/time display
- **Number Formatting**: Currency, percentages, and decimal precision
- **Text Sanitization**: Removal of sensitive data and formatting cleanup

### Privacy and Data Masking Considerations

Privacy protection measures:

- **Data Minimization**: Only export necessary fields
- **PII Masking**: Hash or remove personally identifiable information
- **Access Control**: User authentication required for sensitive exports
- **Audit Logging**: Track who exported what data and when
- **Retention Limits**: Automatic deletion of old export files

## Implementation Guide

This section provides guidance for developers extending or maintaining export functionality.

### How to Add New Export Types

To add a new export type:

1. **Define Data Model**: Create a data class for the export structure
2. **Add Repository Method**: Implement data fetching in appropriate repository
3. **Create Export Function**: Add method to CsvExporter or PdfExporter
4. **Update UI**: Add export option to relevant screens
5. **Add Worker Support**: If needed, extend ReportingWorker for scheduled exports

Example for adding product export:
```kotlin
// In ProductRepository
fun getProductsForExport(userId: String, filters: ProductFilters): Flow<List<ProductExportData>>

// In CsvExporter
fun exportProducts(context: Context, products: List<ProductExportData>): Resource<Uri> {
    val headers = listOf("ID", "Name", "Breed", "Age", "Status")
    val rows = products.map { listOf(it.id, it.name, it.breed, it.age.toString(), it.status) }
    return try {
        val uri = writeCsv(context, "products_${System.currentTimeMillis()}.csv", headers, rows)
        Resource.Success(uri)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Export failed")
    }
}
```

### Repository Methods for Export Data

Repository methods should provide export-specific data access:

- **Filtering**: Support date ranges, status filters, and pagination
- **Aggregation**: Pre-compute totals and averages for performance
- **Formatting**: Return data in export-ready format
- **Security**: Enforce user permissions and data scope

### ViewModel Integration for Export UI

ViewModels handle export state and user interactions:

```kotlin
class ExportViewModel(
    private val exportRepository: ExportRepository,
    private val csvExporter: CsvExporter,
    private val pdfExporter: PdfExporter
) : ViewModel() {
    
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState
    
    fun exportData(type: ExportType, filters: ExportFilters) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val data = exportRepository.getDataForExport(type, filters)
                val uri = when (type.format) {
                    Format.CSV -> csvExporter.exportData(data)
                    Format.PDF -> pdfExporter.exportData(data)
                }
                _exportState.value = ExportState.Success(uri)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Export failed")
            }
        }
    }
}
```

### Code Examples from Actual Implementation

From CsvExporter.kt - KPI export with metadata:
```kotlin
fun exportKpis(
    context: Context,
    kpis: Map<String, Any>,
    fileName: String,
    dateRange: Pair<Long, Long>? = null,
    units: Map<String, String>? = null
): Resource<Uri> {
    // Implementation includes header, date range, and unit handling
}
```

From PdfExporter.kt - Multi-section report:
```kotlin
fun writeReport(
    context: Context,
    fileName: String,
    docTitle: String,
    coverBitmap: Bitmap?,
    sections: List<TableSection>
): Uri {
    // Creates PDF with title page, sections, and proper pagination
}
```

### Testing Export Functionality

Comprehensive testing strategy:

- **Unit Tests**: Test export functions with mock data
- **Integration Tests**: Verify end-to-end export workflows
- **UI Tests**: Test export triggers and progress indicators
- **File Validation**: Verify generated files are correctly formatted
- **Performance Tests**: Test large dataset exports

Example test:
```kotlin
@Test
fun exportProducts_createsValidCsv() {
    val products = listOf(
        ProductExportData("1", "Test Bird", "Breed A", 10, "Healthy")
    )
    val result = csvExporter.exportProducts(context, products)
    assertTrue(result is Resource.Success)
    
    // Verify file contents
    val uri = (result as Resource.Success).data
    val content = readFileFromUri(uri)
    assertTrue(content.contains("Test Bird"))
}
```

## File Management

Export files require careful management for security, performance, and user experience.

### Storage Location (Internal vs External)

ROSTRY uses internal storage for security:

- **Internal Storage**: `context.filesDir/reports/` - protected from other apps
- **FileProvider**: Secure sharing without exposing file paths
- **No External Storage**: Avoids permission issues and security risks
- **Cache vs Persistent**: Reports use persistent storage for user access

### File Naming and Organization

Consistent naming ensures discoverability:

- **Pattern**: `{data_type}_{timestamp}_{user_hash}.{extension}`
- **Timestamps**: ISO format for sorting and uniqueness
- **User Hash**: Anonymized identifier for organization
- **Extensions**: `.csv` for spreadsheets, `.pdf` for documents

### Cleanup and Retention Policies

Automatic cleanup prevents storage bloat:

- **Retention Period**: 30 days for generated reports
- **Cleanup Worker**: Daily job removes expired files
- **User Control**: Manual delete options in export history
- **Size Limits**: Maximum 100MB per export to prevent abuse

### Sharing Exported Files

Secure sharing mechanisms:

- **Share Intent**: Standard Android sharing with chooser
- **FileProvider**: Grants temporary URI permissions
- **Supported Apps**: Email, cloud storage, messaging apps
- **Direct Upload**: Future integration with cloud services

## Performance Considerations

Export operations can be resource-intensive, requiring optimization for large datasets.

### Large Dataset Handling

Strategies for big data exports:

- **Streaming**: Process data in chunks rather than loading everything into memory
- **Pagination**: Repository methods support limit/offset for large result sets
- **Background Processing**: All exports run on background threads
- **Memory Limits**: Monitor heap usage and implement streaming for >10k records

### Background Processing for Exports

WorkManager integration ensures reliable execution:

- **Worker Classes**: Dedicated workers for different export types
- **Constraints**: Network, battery, and storage requirements
- **Retry Policy**: Exponential backoff for transient failures
- **Progress Updates**: Real-time progress for user feedback

### Memory Management

Memory optimization techniques:

- **Object Reuse**: Reuse formatters and buffers
- **Garbage Collection**: Explicit cleanup of temporary objects
- **Bitmap Handling**: Efficient bitmap scaling for PDF images
- **Stream Processing**: Use streams instead of loading entire datasets

### Progress Tracking for Long Exports

User experience during long operations:

- **Progress Callbacks**: WorkManager progress updates
- **UI Indicators**: Progress bars and status text
- **Cancellation**: Allow users to cancel long-running exports
- **Resume Capability**: Save progress state for resumable exports

## User Experience

Export functionality prioritizes usability and clear communication.

### Export UI Patterns

Consistent UI design across the app:

- **Export Buttons**: Clearly labeled "Export" or "Download" actions
- **Format Selection**: Radio buttons or dropdown for CSV/PDF choice
- **Filter Options**: Date pickers, checkboxes for data scope
- **Preview**: Optional preview before generation (future feature)

### Progress Indicators

Visual feedback during export:

- **Loading States**: Spinner or progress bar with descriptive text
- **Percentage Complete**: For long operations with known total
- **Status Messages**: "Preparing data...", "Generating file...", "Ready to share"
- **Time Estimates**: Based on dataset size and historical performance

### Success/Error Notifications

Clear outcome communication:

- **Success Notifications**: Toast messages and system notifications
- **Error Messages**: Specific error descriptions with recovery actions
- **Retry Options**: One-tap retry for failed exports
- **Help Links**: Links to documentation for common issues

### Preview Before Export

Future enhancement for user confidence:

- **Data Preview**: Show first 10 rows before full export
- **File Size Estimate**: Predict export size for storage planning
- **Content Summary**: Number of records, date range, filters applied
- **Format Preview**: Sample of how data will appear in final format

### Export History

Track and manage past exports:

- **History Screen**: List of recent exports with metadata
- **Re-export**: Quick option to regenerate with same parameters
- **File Management**: View, share, or delete historical exports
- **Storage Usage**: Show total space used by export files

## Integration Examples

Practical examples of export usage across ROSTRY features.

### Exporting Farm Monitoring Data

Farm monitoring exports help with compliance and analysis:

```kotlin
// In FarmMonitoringViewModel
fun exportVaccinationRecords(dateRange: Pair<Long, Long>) {
    viewModelScope.launch {
        val records = monitoringRepository.getVaccinationRecordsForExport(
            userId = currentUser.id,
            startDate = dateRange.first,
            endDate = dateRange.second
        )
        
        val csvData = records.map { record ->
            listOf(
                record.productId,
                record.vaccineType,
                record.dateAdministered.toString(),
                record.nextDueDate?.toString() ?: "",
                record.administeredBy
            )
        }
        
        val uri = csvExporter.writeCsv(
            context = getApplication(),
            fileName = "vaccination_records_${System.currentTimeMillis()}.csv",
            headers = listOf("Product ID", "Vaccine", "Date", "Next Due", "Administered By"),
            rows = csvData
        )
        
        // Launch share intent
        val intent = csvExporter.shareCsv(
            context = getApplication(),
            uri = uri,
            subject = "Farm Vaccination Records",
            title = "Share Vaccination Report"
        )
        startActivity(intent)
    }
}
```

This creates a CSV with vaccination history for regulatory compliance.

### Exporting Marketplace Transactions

Financial reporting for marketplace activities:

```kotlin
// Export sales data for tax reporting
fun exportMarketplaceSales(year: Int) {
    val sales = marketplaceRepository.getSalesForYear(currentUser.id, year)
    
    val pdfSections = listOf(
        PdfExporter.TableSection(
            title = "Sales Summary $year",
            headers = listOf("Date", "Product", "Buyer", "Amount", "Status"),
            rows = sales.map { sale ->
                listOf(
                    sale.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    sale.productName,
                    sale.buyerName,
                    sale.amount.toString(),
                    sale.status.name
                )
            }
        )
    )
    
    val uri = pdfExporter.writeReport(
        context = getApplication(),
        fileName = "marketplace_sales_$year.pdf",
        docTitle = "Marketplace Sales Report $year",
        coverBitmap = generateReportQR(),
        sections = pdfSections
    )
    
    showExportNotification(uri, "Sales report ready", "Tap to view or share")
}
```

### Exporting Analytics Reports

Performance metrics for farm management:

```kotlin
// Export KPI dashboard data
fun exportAnalyticsReport(dateRange: Pair<Long, Long>) {
    val kpis = analyticsRepository.getKpisForPeriod(dateRange)
    
    val uri = csvExporter.exportKpis(
        context = getApplication(),
        kpis = kpis,
        fileName = "farm_analytics_${dateRange.first}_${dateRange.second}.csv",
        dateRange = dateRange,
        units = mapOf(
            "mortality_rate" to "%",
            "growth_rate" to "kg/week",
            "revenue" to "USD"
        )
    )
    
    // Handle result
    when (uri) {
        is Resource.Success -> shareAnalyticsReport(uri.data)
        is Resource.Error -> showError("Export failed: ${uri.message}")
    }
}
```

### Exporting Breeding Records

Genetic and performance tracking:

```kotlin
// Export breeding pair performance
fun exportBreedingReport(pairId: String) {
    val breedingData = breedingRepository.getBreedingHistory(pairId)
    
    val pdfSections = listOf(
        PdfExporter.TableSection(
            title = "Breeding Performance",
            headers = listOf("Date", "Eggs Laid", "Fertile", "Hatched", "Success Rate"),
            rows = breedingData.map { record ->
                listOf(
                    record.date.toString(),
                    record.eggsLaid.toString(),
                    record.fertile.toString(),
                    record.hatched.toString(),
                    "${record.successRate}%"
                )
            }
        ),
        PdfExporter.TableSection(
            title = "Offspring Details",
            headers = listOf("Offspring ID", "Birth Date", "Gender", "Health Status"),
            rows = breedingData.flatMap { it.offspring }.map { offspring ->
                listOf(
                    offspring.id,
                    offspring.birthDate.toString(),
                    offspring.gender,
                    offspring.healthStatus
                )
            }
        )
    )
    
    val uri = pdfExporter.writeReport(
        context = getApplication(),
        fileName = "breeding_report_$pairId.pdf",
        docTitle = "Breeding Report - Pair $pairId",
        coverBitmap = null,
        sections = pdfSections
    )
}
