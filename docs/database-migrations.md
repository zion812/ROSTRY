# Database Migrations Guide

**Version:** 1.0  
**Last Updated:** 2025-10-16  
**Audience:** Developers

---

**Note**: This is the canonical migration guide. All migration examples and testing patterns are documented here.

## Table of Contents

- [Overview](#overview)
- [Migration Basics](#migration-basics)
- [Current Database Schema](#current-database-schema)
- [Creating a Migration](#creating-a-migration)
- [Migration Best Practices](#migration-best-practices)
- [Testing Migrations](#testing-migrations)
- [Migration Examples](#migration-examples)
- [Troubleshooting](#troubleshooting)
- [Migration History](#migration-history)

---

## Overview

ROSTRY uses **Room** for local database persistence with **SQLCipher** encryption. Database migrations are critical for evolving the schema while preserving user data across app updates.

### What are Migrations?

Migrations are step-by-step instructions for Room to transform the database schema from one version to another. They ensure data integrity when:
- Adding new tables or columns
- Renaming or removing columns
- Changing data types
- Adding indexes or constraints

### Why Migrations Matter

**Without proper migrations**:
- App crashes on schema mismatch
- User data is lost
- Negative user experience
- Poor app ratings

**With migrations**:
- Seamless app updates
- Data preserved across versions
- User trust maintained
- Professional app quality

---

## Migration Basics

### Room Database Versions

Current database version: **16**

Version is defined in `@Database` annotation:

```kotlin
@Database(
    entities = [/* all entities */],
    version = 16,  // Increment for each schema change
    exportSchema = true
)
```

### Migration Object

Migrations are defined using Room's `Migration` class:

```kotlin
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL statements to transform schema
    }
}
```

### Adding Migration to Database

```kotlin
Room.databaseBuilder(context, RostryDatabase::class.java, "rostry_database")
    .addMigrations(
        MIGRATION_1_2,
        MIGRATION_2_3,
        // ... all migrations
        MIGRATION_15_16
    )
    .build()
```

---

## Current Database Schema

ROSTRY database has **60+ entities** across multiple feature domains:

### Core Entities

**Users & Auth**:
- `UserEntity`
- `SessionEntity`

**Marketplace**:
- `ProductEntity`
- `OrderEntity`
- `CartItemEntity`
- `WishlistItemEntity`
- `AuctionEntity`
- `BidEntity`

**Social Platform**:
- `PostEntity`
- `CommentEntity`
- `LikeEntity`
- `MessageEntity`
- `ThreadEntity`
- `GroupEntity`
- `EventEntity`

**Transfer System**:
- `TransferEntity`
- `TransferVerificationEntity`
- `TransferAuditEntity`

**Farm Monitoring**:
- `GrowthRecordEntity`
- `BreedingPairEntity`
- `QuarantineRecordEntity`
- `MortalityRecordEntity`
- `VaccinationScheduleEntity`
- `HatchingBatchEntity`

**Community Engagement**:
- `ThreadMetadataEntity`
- `CommunityRecommendationEntity`
- `UserInterestEntity`
- `ExpertProfileEntity`

### Database Files

**Main Database**: `RostryDatabase.kt`
- Location: `app/src/main/java/com/rio/rostry/data/local/RostryDatabase.kt`
- All primary application entities
- Encrypted with SQLCipher

**Farm Monitoring Database**: `FarmMonitoringDatabase.kt`
- Separate database for farm-specific features
- Keeps main database manageable

---

## Creating a Migration

### Step-by-Step Process

#### 1. Identify Schema Change

Example: Adding email field to UserEntity

**Before** (version 15):
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    val displayName: String
)
```

**After** (version 16):
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    val displayName: String,
    val email: String? = null  // New field
)
```

#### 2. Update Database Version

```kotlin
@Database(
    entities = [/* ... */],
    version = 16,  // Was 15, now 16
    exportSchema = true
)
abstract class RostryDatabase : RoomDatabase() {
    // ...
}
```

#### 3. Create Migration Object

```kotlin
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add email column to users table
        database.execSQL(
            "ALTER TABLE users ADD COLUMN email TEXT"
        )
    }
}
```

#### 4. Add Migration to Builder

```kotlin
Room.databaseBuilder(/* ... */)
    .addMigrations(
        // ... existing migrations
        MIGRATION_15_16  // Add new migration
    )
    .build()
```

#### 5. Update CHANGELOG

```markdown
### Database Migration 15→16
- Added `email` column to `users` table (nullable TEXT)
```

#### 6. Test Migration

Write migration tests (see Testing section below)

---

## Migration Best Practices

### Do's

✅ **Increment Version for Every Schema Change**:
```kotlin
// Any change = new version
version = 16  // was 15
```

✅ **Make Backwards-Compatible Changes**:
```kotlin
// Add nullable column (safe)
database.execSQL("ALTER TABLE users ADD COLUMN email TEXT")

// Add column with default (safe)
database.execSQL("ALTER TABLE products ADD COLUMN quantity INTEGER NOT NULL DEFAULT 0")
```

✅ **Use Transactions Implicitly**:
Room wraps migration in transaction automatically - no need for `beginTransaction()`

✅ **Test All Migration Paths**:
Test: 15→16, but also 14→15→16, 1→2→...→16

✅ **Export Schema**:
```kotlin
@Database(exportSchema = true)
```
Schemas exported to: `app/schemas/`

✅ **Document Complex Migrations**:
```kotlin
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Migration purpose: Add community engagement features
        // - ThreadMetadataEntity for context tracking
        // - CommunityRecommendationEntity for suggestions
        // - UserInterestEntity for personalization
        // - ExpertProfileEntity for expert information
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS thread_metadata (
                threadId TEXT NOT NULL PRIMARY KEY,
                contextType TEXT NOT NULL,
                contextId TEXT
            )
        """.trimIndent())
        
        // ... more SQL
    }
}
```

### Don'ts

❌ **Don't Skip Versions**:
```kotlin
// Bad: Jumping from 15 to 17
val MIGRATION_15_17 = object : Migration(15, 17) {
    // This skips version 16!
}
```

❌ **Don't Use Destructive Migrations**:
```kotlin
// Bad: Loses all user data
Room.databaseBuilder(/* ... */)
    .fallbackToDestructiveMigration()  // Only for development!
    .build()
```

❌ **Don't Forget to Update Entity**:
```kotlin
// Migration adds column, but entity not updated = crash
```

❌ **Don't Remove Required Migrations**:
```kotlin
// Bad: Removing old migrations breaks upgrade path
Room.databaseBuilder(/* ... */)
    .addMigrations(
        // MIGRATION_1_2,  // ❌ Don't remove!
        MIGRATION_2_3,
        MIGRATION_3_4
    )
```

❌ **Don't Use Dynamic SQL**:
```kotlin
// Bad: SQL injection risk
database.execSQL("ALTER TABLE $tableName ADD COLUMN ...")

// Good: Hardcoded table names
database.execSQL("ALTER TABLE users ADD COLUMN ...")
```

---

## Testing Migrations

### Automated Migration Tests

Room provides `MigrationTestHelper` for testing migrations:

```kotlin
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RostryDatabase::class.java
    )

    @Test
    fun migrate15To16() {
        // Create database with version 15
        helper.createDatabase(TEST_DB, 15).apply {
            // Insert test data with old schema
            execSQL("""
                INSERT INTO users (userId, phoneNumber, displayName)
                VALUES ('user1', '+1234567890', 'Test User')
            """)
            close()
        }

        // Run migration
        helper.runMigrationsAndValidate(TEST_DB, 16, true, MIGRATION_15_16)

        // Validate migration succeeded
        getMigratedRoomDatabase().apply {
            val cursor = query("SELECT * FROM users WHERE userId = 'user1'")
            assertTrue(cursor.moveToFirst())

            // Check new column exists and is null
            val emailIndex = cursor.getColumnIndex("email")
            assertTrue(emailIndex >= 0)
            assertTrue(cursor.isNull(emailIndex))

            cursor.close()
            close()
        }
    }

    @Test
    fun migrate16To17_addsCategory() {
        helper.createDatabase("test", 16).apply {
            execSQL("INSERT INTO products VALUES ('1', 'Chicken', 100.0, 'user1', 1234567890)")
            close()
        }

        helper.runMigrationsAndValidate("test", 17, true, MIGRATION_16_17)

        helper.openDatabase("test", 17).apply {
            query("SELECT category FROM products WHERE id = '1'").use { cursor ->
                assertThat(cursor.moveToFirst()).isTrue()
                assertThat(cursor.getString(0)).isEqualTo("General")
            }
        }
    }

    @Test
    fun migrateAll() {
        // Create empty database with version 1
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Migrate through all versions
        helper.runMigrationsAndValidate(
            TEST_DB,
            16,  // Current version
            true,
            MIGRATION_1_2,
            MIGRATION_2_3,
            // ... all migrations
            MIGRATION_15_16
        )
    }

    private fun getMigratedRoomDatabase(): SupportSQLiteDatabase {
        val database = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            RostryDatabase::class.java,
            TEST_DB
        )
            .addMigrations(/* all migrations */)
            .build()

        // Trigger database creation
        helper.closeWhenFinished(database)
        return helper.openDatabase(TEST_DB, false)
    }
}
```

### Manual Testing

1. **Test Fresh Install**:
   - Install app with new version
   - Verify all features work

2. **Test Upgrade Path**:
   - Install old version (from APK or Play Store)
   - Add test data
   - Install new version
   - Verify data preserved
   - Test all features

3. **Test Multi-Step Upgrade**:
   - Install version N-2
   - Upgrade to version N-1
   - Upgrade to version N
   - Verify data integrity

### Testing Checklist

- [ ] Migration compiles without errors
- [ ] Automated tests pass
- [ ] Fresh install works
- [ ] Upgrade from previous version works
- [ ] Upgrade from oldest supported version works
- [ ] Data is preserved correctly
- [ ] New features work with migrated data
- [ ] No data loss
- [ ] No crashes
- [ ] Performance acceptable

---

## Migration Examples

### Example 1: Adding a Column

**Scenario**: Add `email` to `users` table

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE users ADD COLUMN email TEXT"
        )
    }
}
```

### Example 2: Adding a Column with Default

**Scenario**: Add `isVerified` with default `false`

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE users ADD COLUMN isVerified INTEGER NOT NULL DEFAULT 0"
        )
    }
}
```

### Example 3: Creating a New Table

**Scenario**: Add `notifications` table

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS notifications (
                notificationId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                isRead INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE
            )
        """.trimIndent())
        
        // Add index for queries
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_notifications_userId ON notifications(userId)"
        )
    }
}
```

### Example 4: Renaming a Column

**Scenario**: Rename `displayName` to `name`

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQLite doesn't support RENAME COLUMN directly in old versions
        // Must create new table, copy data, drop old table
        
        // 1. Create new table with new schema
        database.execSQL("""
            CREATE TABLE users_new (
                userId TEXT NOT NULL PRIMARY KEY,
                phoneNumber TEXT NOT NULL,
                name TEXT NOT NULL,
                email TEXT
            )
        """.trimIndent())
        
        // 2. Copy data from old table
        database.execSQL("""
            INSERT INTO users_new (userId, phoneNumber, name, email)
            SELECT userId, phoneNumber, displayName, email FROM users
        """.trimIndent())
        
        // 3. Drop old table
        database.execSQL("DROP TABLE users")
        
        // 4. Rename new table to original name
        database.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}
```

### Example 5: Changing Data Type

**Scenario**: Change `price` from INTEGER to REAL

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new table with correct type
        database.execSQL("""
            CREATE TABLE products_new (
                productId TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                quantity INTEGER NOT NULL
            )
        """.trimIndent())
        
        // Copy and convert data
        database.execSQL("""
            INSERT INTO products_new (productId, name, price, quantity)
            SELECT productId, name, CAST(price AS REAL), quantity FROM products
        """.trimIndent())
        
        // Replace old table
        database.execSQL("DROP TABLE products")
        database.execSQL("ALTER TABLE products_new RENAME TO products")
    }
}
```

### Example 6: Adding Foreign Key

**Scenario**: Add foreign key to `orders` table

```kotlin
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new table with foreign key
        database.execSQL("""
            CREATE TABLE orders_new (
                orderId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                productId TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE,
                FOREIGN KEY(productId) REFERENCES products(productId) ON DELETE RESTRICT
            )
        """.trimIndent())
        
        // Copy data (only valid references)
        database.execSQL("""
            INSERT INTO orders_new (orderId, userId, productId, quantity)
            SELECT o.orderId, o.userId, o.productId, o.quantity
            FROM orders o
            INNER JOIN users u ON o.userId = u.userId
            INNER JOIN products p ON o.productId = p.productId
        """.trimIndent())
        
        database.execSQL("DROP TABLE orders")
        database.execSQL("ALTER TABLE orders_new RENAME TO orders")
    }
}
```

### Example 7: Complex Multi-Table Migration

**Scenario**: Migration 15→16 (Community features)

```kotlin
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. Thread metadata for context tracking
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS thread_metadata (
                threadId TEXT NOT NULL PRIMARY KEY,
                contextType TEXT NOT NULL,
                contextId TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """.trimIndent())
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_thread_metadata_contextType " +
            "ON thread_metadata(contextType)"
        )
        
        // 2. Community recommendations
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS community_recommendations (
                recommendationId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                type TEXT NOT NULL,
                targetId TEXT NOT NULL,
                score REAL NOT NULL,
                reason TEXT,
                createdAt INTEGER NOT NULL,
                isViewed INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_community_recommendations_userId " +
            "ON community_recommendations(userId)"
        )
        
        // 3. User interests for personalization
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_interests (
                interestId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                interestType TEXT NOT NULL,
                weight REAL NOT NULL,
                lastUpdated INTEGER NOT NULL
            )
        """.trimIndent())
        
        // 4. Expert profiles
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS expert_profiles (
                userId TEXT NOT NULL PRIMARY KEY,
                specialties TEXT NOT NULL,
                rating REAL NOT NULL DEFAULT 0.0,
                reviewCount INTEGER NOT NULL DEFAULT 0,
                hourlyRate REAL,
                isAvailable INTEGER NOT NULL DEFAULT 1,
                bio TEXT
            )
        """.trimIndent())
    }
}
```

---

## Troubleshooting

### Common Issues

#### "IllegalStateException: Migration didn't properly handle..."

**Cause**: Schema mismatch after migration

**Solution**:
1. Check migration SQL is correct
2. Verify entity matches new schema
3. Run migration tests
4. Check exported schema JSON

#### "Cannot find migration path from X to Y"

**Cause**: Missing migration in chain

**Solution**:
```kotlin
// Add all migrations between versions
Room.databaseBuilder(/* ... */)
    .addMigrations(
        MIGRATION_X_Y,  // Was missing
        MIGRATION_Y_Z
    )
```

#### "Error: duplicate column name"

**Cause**: Column already exists

**Solution**:
```kotlin
// Check if column exists first (not recommended)
// Or use CREATE TABLE IF NOT EXISTS
// Or ensure migration only runs once
```

#### App crashes after update

**Cause**: Migration failed silently

**Solution**:
1. Check Logcat for migration errors
2. Test migration thoroughly
3. Add fallback for corrupted data
4. Consider destructive migration for unrecoverable issues (development only)

### Debug Migration

Enable migration logging:

```kotlin
Room.databaseBuilder(/* ... */)
    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)  // For debugging
    .build()
```

Check migration logs:
```bash
adb logcat | grep "RoomDatabase"
```

---

## Migration History

### Version 16 (Current)

**Added**: Community engagement features
- `thread_metadata` table
- `community_recommendations` table
- `user_interests` table
- `expert_profiles` table

### Version 15

**Added**: Enhanced farm monitoring
- Updated vaccination schedules
- Added hatching batch tracking

### Version 14

**Added**: Transfer system enhancements
- Transfer verification fields
- Audit trail

### Version 13

**Added**: Analytics dashboard
- Performance metrics
- Financial tracking

### Earlier Versions

See `CHANGELOG.md` for complete version history.

---

## Related Documentation

- [Architecture](architecture.md) - Database architecture
- [Data Contracts](data-contracts.md) - Entity definitions
- [CHANGELOG.md](../CHANGELOG.md) - Version history
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [SQLite Documentation](https://www.sqlite.org/docs.html)

---

**Database migrations are critical for app stability. Test thoroughly before releasing!** 🗄️
