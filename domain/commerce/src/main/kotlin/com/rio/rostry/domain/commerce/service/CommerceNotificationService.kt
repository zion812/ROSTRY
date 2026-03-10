package com.rio.rostry.domain.commerce.service

interface CommerceNotificationService {
    suspend fun notifyOrderUpdate(
        orderId: String,
        status: String,
        title: String,
        message: String
    )
    
    suspend fun onOrderStatusChanged(
        orderId: String,
        userId: String,
        oldStatus: String,
        newStatus: String,
        title: String,
        message: String
    )
}
