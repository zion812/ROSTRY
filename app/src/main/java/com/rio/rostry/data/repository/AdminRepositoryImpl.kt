package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.domain.model.AdminOrderSummary
import com.rio.rostry.domain.model.AdminUserProfile
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val productDao: ProductDao
) : AdminRepository {

    override fun getUserFullProfile(userId: String): Flow<Resource<AdminUserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val user = userDao.findById(userId)
            if (user == null) {
                emit(Resource.Error("User not found"))
                return@flow
            }

            // Aggregate data
            
            // 1. Products (Seller context)
            val products = productDao.getProductsBySellerSuspend(userId)
            val activeListings = products.count { it.status == "available" }

            // 2. Orders (Buyer context) - showing what they BOUGHT
            val orders = orderDao.getOrdersByBuyerIdSuspend(userId)
            val totalOrders = orders.size
            val totalSpend = orders.sumOf { it.totalAmount }
            
            // Map recent 5 orders
            val recentOrders = orders.sortedByDescending { it.createdAt }
                .take(5)
                .map { order ->
                    AdminOrderSummary(
                        orderId = order.orderId,
                        date = Date(order.createdAt),
                        amount = order.totalAmount,
                        status = order.status, // already a string
                        itemCount = 1 // OrderEntity is per item/quantity, no sub-items list
                    )
                }

            // 3. Risk Score (Stub logic for now, could check disputes)
            val riskScore = if (user.isSuspended) 100 else 0

            val profile = AdminUserProfile(
                user = user,
                totalOrdersCount = totalOrders,
                totalSpend = totalSpend,
                activeListingsCount = activeListings,
                lastActive = Date(), // TODO: getting real last active requires separate tracking
                recentOrders = recentOrders,
                riskScore = riskScore
            )

            emit(Resource.Success(profile))

        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch profile: ${e.message}"))
        }
    }
}
