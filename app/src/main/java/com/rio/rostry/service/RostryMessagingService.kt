package com.rio.rostry.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RostryMessagingService : FirebaseMessagingService() {

    // In a real app, you would inject a repository here to save the token
    // @Inject lateinit var userRepository: UserRepository 

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM Token: %s", token)
        // Here you would typically send the token to your server or save it to Firestore
        // e.g., viewModelScope.launch { userRepository.updateFcmToken(token) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("From: %s", remoteMessage.from)

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Timber.d("Message data payload: " + remoteMessage.data)
            // Handle data payload
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Timber.d("Message Notification Body: %s", it.body)
            // Handle notification payload (e.g., show a local notification)
        }
    }
}
