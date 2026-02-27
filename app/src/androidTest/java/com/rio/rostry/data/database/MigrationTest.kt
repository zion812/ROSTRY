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
        helper.createDatabase(TEST_DB, 87).apply {
            close()
        }
        helper.runMigrationsAndValidate(TEST_DB, 88, true, AppDatabase.Converters.MIGRATION_87_88)
    }

    @Test
    @Throws(IOException::class)
    fun migrate88To89() {
        helper.createDatabase(TEST_DB, 88).apply {
            close()
        }
        helper.runMigrationsAndValidate(TEST_DB, 89, true, AppDatabase.Converters.MIGRATION_88_89)
    }

    @Test
    @Throws(IOException::class)
    fun migrate89To90() {
        helper.createDatabase(TEST_DB, 89).apply {
            close()
        }
        helper.runMigrationsAndValidate(TEST_DB, 90, true, AppDatabase.Converters.MIGRATION_89_90)
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll87To90() {
        helper.createDatabase(TEST_DB, 87).apply {
            close()
        }
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
