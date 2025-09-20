package com.rio.rostry.domain.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val role: String, // farmer, buyer, transporter
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)