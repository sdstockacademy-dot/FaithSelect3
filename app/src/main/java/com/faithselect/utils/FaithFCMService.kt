package com.faithselect.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.faithselect.FaithSelectApp
import com.faithselect.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Handles incoming FCM push notifications.
 *
 * To send a daily verse notification from Firebase Console:
 * 1. Go to Firebase Console → Engage → Messaging
 * 2. Create a new campaign
 * 3. Target: Topic "daily_verse"
 * 4. Include data payload: { "type": "daily_verse", "date": "2024-01-01" }
 *
 * Or from Firebase Functions (recommended for scheduled daily sends):
 *   admin.messaging().sendToTopic('daily_verse', { notification: {...}, data: {...} })
 */
class FaithFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "Faith Select"
        val body  = message.notification?.body  ?: message.data["body"]  ?: "Your daily verse awaits"
        val type  = message.data["type"] ?: ""

        showNotification(title = title, body = body, type = type)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send this token to your backend/Firestore if you need user-specific notifications
        // For topic-based (daily verse), no server-side token storage is needed
    }

    private fun showNotification(title: String, body: String, type: String) {
        val channelId = when (type) {
            "audio_update" -> FaithSelectApp.CHANNEL_AUDIO_PLAYBACK
            else           -> FaithSelectApp.CHANNEL_DAILY_VERSE
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your icon
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
