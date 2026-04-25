package com.faithselect.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for favorited verses — stored locally for offline access.
 */
@Entity(tableName = "favorite_verses")
data class FavoriteVerseEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val scriptureId: String,
    val religionId: String,
    val verseNumber: Int,
    val chapterNumber: Int,
    val originalText: String,
    val hindiText: String,
    val bengaliText: String,
    val englishText: String,
    val hindiMeaning: String,
    val bengaliMeaning: String,
    val englishMeaning: String,
    val audioUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)

/**
 * Room entity for favorited audio items.
 */
@Entity(tableName = "favorite_audio")
data class FavoriteAudioEntity(
    @PrimaryKey val id: String,
    val religionId: String,
    val scriptureId: String,
    val title: String,
    val titleHindi: String,
    val titleBengali: String,
    val description: String,
    val audioUrl: String,
    val coverImageUrl: String,
    val durationSeconds: Int,
    val category: String,
    val savedAt: Long = System.currentTimeMillis()
)

/**
 * Room entity for tracking download progress of audio.
 */
@Entity(tableName = "downloaded_audio")
data class DownloadedAudioEntity(
    @PrimaryKey val audioId: String,
    val localFilePath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSizeBytes: Long = 0L
)
