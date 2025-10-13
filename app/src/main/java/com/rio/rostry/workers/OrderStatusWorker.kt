package com.rio.rostry.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.MainActivity
import com.rio.rostry.R
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.sync.FirestoreService
import com.rio.rostry.session.CurrentUserProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class OrderStatusWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val orderRepository: OrderRepository,
    private val notificationDao: NotificationDao,
    private val firestoreService: FirestoreService,
    private val currentUserProvider: CurrentUserProvider
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
                        since = order.updatedAt,
                        limit = 1
                    )
                    val remoteOrder = remoteOrders.firstOrNull { it.orderId == order.orderId }
                    
                    if (remoteOrder != null && remoteOrder.status != order.status) {
                        // Status changed - update local order
                        orderRepository.updateOrderStatus(order.orderId, remoteOrder.status)
                        
                        // Create and display notification
                        val notificationId = order.orderId.hashCode()
                        val title = "Order Update"
                        val message = "Your order #${order.orderId.takeLast(6)} is now ${remoteOrder.status}"
                        
                        createNotification(notificationId, title, message, order.orderId)
                        
                        // Store notification in database
                        val notification = NotificationEntity(
                            notificationId = UUID.randomUUID().toString(),
                            userId = userId,
                            title = title,
                            message = message,
                            type = "ORDER_UPDATE",
                            deepLinkUrl = "rostry://order/${order.orderId}",
                            isRead = false,
                            createdAt = System.currentTimeMillis()
                        )
                        notificationDao.insertNotification(notification)
                        
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

    private fun createNotification(id: Int, title: String, message: String, orderId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Orders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Order status updates"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent for deep link
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("orderId", orderId)
            putExtra("deepLink", "rostry://order/$orderId")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(id, notification)
    }

    companion object {
        private const val WORK_NAME = "OrderStatusWorker"
        private const val CHANNEL_ID = "order_updates"
        
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
