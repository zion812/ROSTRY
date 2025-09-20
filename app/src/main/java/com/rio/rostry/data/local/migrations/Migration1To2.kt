package com.rio.rostry.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create new tables WITHOUT the isDeleted and deletedAt columns initially
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `product_tracking` (
                `id` TEXT NOT NULL,
                `productId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                `notes` TEXT,
                `imageUrl` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `family_tree` (
                `id` TEXT NOT NULL,
                `parentId` TEXT NOT NULL,
                `childId` TEXT NOT NULL,
                `relationshipType` TEXT NOT NULL,
                `generation` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `chat_messages` (
                `id` TEXT NOT NULL,
                `senderId` TEXT NOT NULL,
                `receiverId` TEXT NOT NULL,
                `orderId` TEXT,
                `message` TEXT NOT NULL,
                `messageType` TEXT NOT NULL,
                `isRead` INTEGER NOT NULL DEFAULT 0,
                `imageUrl` TEXT,
                `fileUrl` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        
        // Add soft delete columns to existing tables
        addColumnIfNotExists(database, "users", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "users", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "products", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "products", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "orders", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "orders", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "transfers", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "transfers", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "coins", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "coins", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "notifications", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "notifications", "deletedAt", "INTEGER")
        
        // Now add the soft delete columns to the new tables
        addColumnIfNotExists(database, "product_tracking", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "product_tracking", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "family_tree", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "family_tree", "deletedAt", "INTEGER")
        
        addColumnIfNotExists(database, "chat_messages", "isDeleted", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfNotExists(database, "chat_messages", "deletedAt", "INTEGER")
    }
    
    private fun addColumnIfNotExists(database: SupportSQLiteDatabase, tableName: String, columnName: String, columnDefinition: String) {
        try {
            // Check if column exists by trying to select from it
            database.execSQL("SELECT $columnName FROM $tableName LIMIT 0")
            // If we get here, column exists, so we don't need to add it
        } catch (e: Exception) {
            // Column doesn't exist, so add it
            database.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnName` $columnDefinition")
        }
    }
}