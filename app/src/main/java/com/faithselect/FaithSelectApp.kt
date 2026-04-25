package com.faithselect

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.faithselect.data.repository.SubscriptionRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point.
 * @HiltAndroidApp triggers Hilt's code generation for dependency injection.
 */
@HiltAndroidApp
class FaithSelectApp : Application() {

    // Inject the subscription repo to initialize billing on app start
    @Inject
    lateinit var subscriptionRepository: SubscriptionRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        setupNotificationChannels()
        subscriptionRepository.initialize()
    }

    /**
     * Create notification channels required for Android 8.0+
     */
    private fun setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Audio playback channel
            val audioChannel = NotificationChannel(
                CHANNEL_AUDIO_PLAYBACK,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for audio playback"
                setShowBadge(false)
            }

            // Daily verse notification channel
            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_VERSE,
                "Daily Verse",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily verse and inspiration notifications"
            }

            manager.createNotificationChannels(listOf(audioChannel, dailyChannel))
        }
    }

    companion object {
        const val CHANNEL_AUDIO_PLAYBACK = "faith_select_audio"
        const val CHANNEL_DAILY_VERSE    = "faith_select_daily"
    }
}
