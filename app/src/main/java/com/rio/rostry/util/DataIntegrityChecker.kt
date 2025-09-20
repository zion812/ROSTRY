package com.rio.rostry.util

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.Product
import com.rio.rostry.domain.model.Order
import com.rio.rostry.domain.model.Transfer
import java.util.Date

object DataIntegrityChecker {
    
    fun validateUser(user: User): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (user.id.isBlank()) {
            errors.add("User ID cannot be empty")
        }
        
        if (user.phone.isBlank()) {
            errors.add("User phone cannot be empty")
        }
        
        if (user.createdAt.after(Date())) {
            errors.add("User created date cannot be in the future")
        }
        
        if (user.updatedAt.after(Date())) {
            errors.add("User updated date cannot be in the future")
        }
        
        if (user.createdAt.after(user.updatedAt)) {
            errors.add("User created date cannot be after updated date")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateProduct(product: Product): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (product.id.isBlank()) {
            errors.add("Product ID cannot be empty")
        }
        
        if (product.name.isBlank()) {
            errors.add("Product name cannot be empty")
        }
        
        if (product.price < 0) {
            errors.add("Product price cannot be negative")
        }
        
        if (product.quantity < 0) {
            errors.add("Product quantity cannot be negative")
        }
        
        if (product.farmerId.isBlank()) {
            errors.add("Product farmer ID cannot be empty")
        }
        
        if (product.createdAt.after(Date())) {
            errors.add("Product created date cannot be in the future")
        }
        
        if (product.updatedAt.after(Date())) {
            errors.add("Product updated date cannot be in the future")
        }
        
        if (product.createdAt.after(product.updatedAt)) {
            errors.add("Product created date cannot be after updated date")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateOrder(order: Order): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (order.id.isBlank()) {
            errors.add("Order ID cannot be empty")
        }
        
        if (order.productId.isBlank()) {
            errors.add("Order product ID cannot be empty")
        }
        
        if (order.buyerId.isBlank()) {
            errors.add("Order buyer ID cannot be empty")
        }
        
        if (order.farmerId.isBlank()) {
            errors.add("Order farmer ID cannot be empty")
        }
        
        if (order.quantity <= 0) {
            errors.add("Order quantity must be positive")
        }
        
        if (order.totalPrice < 0) {
            errors.add("Order total price cannot be negative")
        }
        
        if (order.deliveryAddress.isBlank()) {
            errors.add("Order delivery address cannot be empty")
        }
        
        if (order.createdAt.after(Date())) {
            errors.add("Order created date cannot be in the future")
        }
        
        if (order.updatedAt.after(Date())) {
            errors.add("Order updated date cannot be in the future")
        }
        
        if (order.createdAt.after(order.updatedAt)) {
            errors.add("Order created date cannot be after updated date")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateTransfer(transfer: Transfer): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (transfer.id.isBlank()) {
            errors.add("Transfer ID cannot be empty")
        }
        
        if (transfer.fromUserId.isBlank()) {
            errors.add("Transfer from user ID cannot be empty")
        }
        
        if (transfer.toUserId.isBlank()) {
            errors.add("Transfer to user ID cannot be empty")
        }
        
        if (transfer.fromUserId == transfer.toUserId) {
            errors.add("Transfer from and to user IDs cannot be the same")
        }
        
        if (transfer.amount <= 0) {
            errors.add("Transfer amount must be positive")
        }
        
        if (transfer.type.isBlank()) {
            errors.add("Transfer type cannot be empty")
        }
        
        if (transfer.status.isBlank()) {
            errors.add("Transfer status cannot be empty")
        }
        
        if (transfer.createdAt.after(Date())) {
            errors.add("Transfer created date cannot be in the future")
        }
        
        if (transfer.updatedAt.after(Date())) {
            errors.add("Transfer updated date cannot be in the future")
        }
        
        if (transfer.createdAt.after(transfer.updatedAt)) {
            errors.add("Transfer created date cannot be after updated date")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    data class ValidationResult(val isValid: Boolean, val errors: List<String>)
}