package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.sync.FirestoreService
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.session.CurrentUserProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class OrderStatusWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val orderRepository: OrderRepository,
    private val notificationDao: NotificationDao,
    private val firestoreService: FirestoreService,
    private val currentUserProvider: CurrentUserProvider,
    private val intelligentNotificationService: IntelligentNotificationService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val userId = currentUserProvider.userIdOrNull() ?: return Result.success()
            
            Timber.d("OrderStatusWorker: Checking order status updates for user $userId")
            
            // Get orders with active statuses that need monitoring
            val activeStatuses = listOf("PLACED", "CONFIRMED", "OUT_FOR_DELIVERY")
            val orders = orderRepository.getOrdersForNotification(userId, activeStatuses)
                .first()
            
            var updatesCount = 0
            
            for (order in orders) {
                try {
                    // Fetch latest status from Firestore
                    val remoteOrders = firestoreService.fetchUpdatedOrders(
                        userId = userId,
                        since = order.updatedAt,
                        limit = 1
                    )
                    val remoteOrder = remoteOrders.firstOrNull { it.orderId == order.orderId }
                    
                    if (remoteOrder != null && remoteOrder.status != order.status) {
                        // Status changed - update local order
                        orderRepository.updateOrderStatus(order.orderId, remoteOrder.status)
                        
                        // Notify via service
                        val title = "Order Update"
                        val message = "Your order #${order.orderId.takeLast(6)} is now ${remoteOrder.status}"
                        
                        intelligentNotificationService.notifyOrderUpdate(order.orderId, remoteOrder.status, title, message)
                        
                        updatesCount++
                        Timber.d("OrderStatusWorker: Updated order ${order.orderId} to ${remoteOrder.status}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "OrderStatusWorker: Failed to check order ${order.orderId}")
                    // Continue with other orders
                }
            }
            
            Timber.d("OrderStatusWorker: Completed with $updatesCount updates")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "OrderStatusWorker: Failed")
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "OrderStatusWorker"
        
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<OrderStatusWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
            
            Timber.d("OrderStatusWorker: Scheduled periodic work (every 30 minutes)")
        }
    }
}
