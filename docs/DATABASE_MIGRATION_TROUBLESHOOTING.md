# Database Migration Troubleshooting Guide

## Common Migration Error: "Migration didn't properly handle"

This error occurs when there's a schema mismatch between what Room expects and what exists in the device database.

### Quick Fixes

#### 1. **Clear App Data (Recommended for Development)**
The fastest solution during development:

**Via ADB:**
```bash
adb shell pm clear com.rio.rostry
```

**Manual Steps:**
1. Open device Settings
2. Apps → ROSTRY
3. Storage → Clear Data

#### 2. **Uninstall and Reinstall**
```bash
adb uninstall com.rio.rostry
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 3. **Force Destructive Migration (Debug Only)**
Already configured in `DatabaseModule.kt`:
```kotlin
if (BuildConfig.DEBUG) {
    builder.fallbackToDestructiveMigration()
}
```

This automatically drops and recreates the database when migration fails in debug builds.

---

## Understanding the Error

### What Room Expects (Version 40):
```sql
CREATE TABLE products (
    productId TEXT NOT NULL,
    sellerId TEXT NOT NULL,
    name TEXT NOT NULL,
    -- ... 45 total columns
    PRIMARY KEY(productId),
    FOREIGN KEY(sellerId) REFERENCES users(userId)
)
```

### Common Causes:

1. **Incomplete Migration Chain**
   - Device has version 35, but migrations 35→36→...→39→40 are missing
   - **Fix:** Ensure all migrations are added to `DatabaseModule.kt`

2. **Schema Mismatch from Old Version**
   - Device database has old column structure
   - **Fix:** Clear data or use destructive migration

3. **Manual Database Modifications**
   - Database was modified outside of Room migrations
   - **Fix:** Must clear data

4. **Type Converter Issues**
   - Room can't convert custom types (List<String>, enums)
   - **Fix:** Ensure `@TypeConverters` is properly configured

---

## Production Migration Best Practices

### 1. **Never Use Destructive Migration in Production**
```kotlin
if (BuildConfig.DEBUG) {
    builder.fallbackToDestructiveMigration()
} else {
    // Only handle downgrades gracefully
    builder.fallbackToDestructiveMigrationOnDowngrade()
}
```

### 2. **Test Migrations from All Previous Versions**
```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @Test
    fun migrate39To40() {
        helper.createDatabase(TEST_DB, 39).apply {
            // Insert test data
            close()
        }
        
        helper.runMigrationsAndValidate(TEST_DB, 40, true, MIGRATION_39_40)
    }
}
```

### 3. **Provide Default Values for New Columns**
```kotlin
db.execSQL("ALTER TABLE products ADD COLUMN newField TEXT NOT NULL DEFAULT ''")
```

### 4. **Export Schema for Documentation**
In `build.gradle.kts`:
```kotlin
room {
    schemaDirectory("$projectDir/schemas")
}
```

---

## Current Database Version: 40

### Recent Migrations:

#### **MIGRATION_39_40** (Performance Indexes)
- Adds 13 single-column indexes
- Adds 3 composite indexes
- **No schema changes** - only index creation

#### **MIGRATION_38_39** (No-op)
- Enables deferred foreign key checks
- No actual schema modifications

---

## Debugging Migration Issues

### 1. **Check Logcat for Details**
```bash
adb logcat | grep -E "DatabaseModule|Migration_39_40"
```

Look for:
- `Database opened. Version: X` - Shows current version
- `Successfully added performance indexes` - Migration succeeded
- `Error during migration` - Migration failed

### 2. **Inspect Database Version**
```bash
adb shell "run-as com.rio.rostry sqlite3 /data/data/com.rio.rostry/databases/rostry_database.db 'PRAGMA user_version'"
```

### 3. **Check Table Schema**
```bash
adb shell "run-as com.rio.rostry sqlite3 /data/data/com.rio.rostry/databases/rostry_database.db '.schema products'"
```

### 4. **List All Indexes**
```bash
adb shell "run-as com.rio.rostry sqlite3 /data/data/com.rio.rostry/databases/rostry_database.db 'SELECT name FROM sqlite_master WHERE type=\"index\" AND tbl_name=\"products\"'"
```

---

## How to Write Safe Migrations

### ✅ **Safe Operations:**
- Adding nullable columns
- Creating indexes
- Adding tables
- Dropping indexes

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN newField TEXT")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_products_newField ON products(newField)")
    }
}
```

### ⚠️ **Risky Operations:**
- Renaming columns (requires data copy)
- Changing column types
- Adding NOT NULL columns without defaults
- Dropping columns with data

```kotlin
// ❌ BAD - Will fail if column has data
db.execSQL("ALTER TABLE products ADD COLUMN status TEXT NOT NULL")

// ✅ GOOD - Provides default value
db.execSQL("ALTER TABLE products ADD COLUMN status TEXT NOT NULL DEFAULT 'available'")
```

### 🚨 **Complex Operations (Requires Multi-Step Migration):**
```kotlin
// Renaming column: oldName → newName
db.execSQL("CREATE TABLE products_new (productId TEXT PRIMARY KEY, newName TEXT)")
db.execSQL("INSERT INTO products_new SELECT productId, oldName FROM products")
db.execSQL("DROP TABLE products")
db.execSQL("ALTER TABLE products_new RENAME TO products")
```

---

## When to Increment Version

**Increment database version when:**
- Adding/removing tables
- Adding/removing/modifying columns
- Changing column types
- Modifying foreign keys
- Adding indexes (best practice, though not strictly required)

**Don't increment for:**
- Changes in DAO methods
- Changes in query logic
- Type converter modifications (unless schema changes)

---

## Emergency Recovery

If users report data loss after update:

1. **Rollback to Previous Version**
   - Deploy previous APK version
   - Fix migration in newer build

2. **Provide Migration Path**
   - Create migration from problematic version
   - Test thoroughly before release

3. **Export User Data (If Possible)**
   ```kotlin
   // Before migration
   val backup = database.query("SELECT * FROM products")
   // Save to file/cloud
   ```

---

## Contact

For migration issues, check:
- Schema files: `app/schemas/com.rio.rostry.data.database.AppDatabase/`
- Migration definitions: `AppDatabase.kt` companion object
- Database module: `di/DatabaseModule.kt`
