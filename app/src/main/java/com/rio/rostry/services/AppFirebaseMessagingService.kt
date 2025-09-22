package com.rio.rostry.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rio.rostry.utils.notif.SocialNotifierImpl
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("FCM token: $token")
        // TODO: send token to backend if needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val type = data["type"]
        val notifier = SocialNotifierImpl(applicationContext)
        when (type) {
            "message" -> {
                val threadId = data["threadId"] ?: ""
                val from = data["from"] ?: "Someone"
                val text = data["text"] ?: "New message"
                notifier.notifyNewMessage(threadId, from, text)
            }
            "comment" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyNewComment("", postId, who)
            }
            "like" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyNewLike("", postId, who)
            }
            "follow" -> {
                val userId = data["userId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyFollow(userId, who)
            }
            else -> {
                Timber.d("Unknown social notification type: $type")
            }
        }
    }
}
