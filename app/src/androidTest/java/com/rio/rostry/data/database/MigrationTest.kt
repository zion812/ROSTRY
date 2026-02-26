package com.rio.rostry.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Test database migrations to ensure schema changes are applied correctly.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate87To88() {
        // Create database at version 87
        helper.createDatabase(TEST_DB, 87).apply {
            close()
        }

        // Re-open the database with version 88 and provide MIGRATION_87_88
        helper.runMigrationsAndValidate(TEST_DB, 88, true, AppDatabase.Converters.MIGRATION_87_88)
    }

    @Test
    @Throws(IOException::class)
    fun migrate88To89() {
        // Create database at version 88
        helper.createDatabase(TEST_DB, 88).apply {
            close()
        }

        // Re-open the database with version 89 and provide MIGRATION_88_89
        helper.runMigrationsAndValidate(TEST_DB, 89, true, AppDatabase.Converters.MIGRATION_88_89)
    }

    @Test
    @Throws(IOException::class)
    fun migrate89To90() {
        // Create database at version 89
        helper.createDatabase(TEST_DB, 89).apply {
            close()
        }

        // Re-open the database with version 90 and provide MIGRATION_89_90
        helper.runMigrationsAndValidate(TEST_DB, 90, true, AppDatabase.Converters.MIGRATION_89_90)
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll87To90() {
        // Create database at version 87
        helper.createDatabase(TEST_DB, 87).apply {
            close()
        }

        // Re-open the database with version 90 and provide all migrations
        helper.runMigrationsAndValidate(
            TEST_DB,
            90,
            true,
            AppDatabase.Converters.MIGRATION_87_88,
            AppDatabase.Converters.MIGRATION_88_89,
            AppDatabase.Converters.MIGRATION_89_90
        )
    }
}
