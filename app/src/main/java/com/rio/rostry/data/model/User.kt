package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val role: String, // farmer, buyer, transporter
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)