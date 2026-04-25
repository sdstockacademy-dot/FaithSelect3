package com.faithselect.presentation.audio

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.faithselect.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Background audio playback service using Media3 MediaSessionService.
 * This enables:
 * - Background playback (music continues when app is minimized)
 * - Media notifications with play/pause controls
 * - Integration with system media controls (lock screen, Bluetooth)
 * - Resume playback after interruptions
 */
@AndroidEntryPoint
class AudioPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        // Build ExoPlayer with audio-focused attributes
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true) // Pause on headphone disconnect
            .build()

        // Pending intent to open app when notification is tapped
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // ─── Helper to play audio from URL ────────────────────────────────────────
    fun playAudio(audioUrl: String, title: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(audioUrl)
            .setMediaId(audioUrl)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun pause() = player.pause()
    fun resume() = player.play()
    fun stop() = player.stop()
    fun seekTo(positionMs: Long) = player.seekTo(positionMs)

    val isPlaying get() = player.isPlaying
    val currentPosition get() = player.currentPosition
    val duration get() = player.duration
}
