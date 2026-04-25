package com.faithselect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.faithselect.data.local.dao.*
import com.faithselect.data.local.entities.*

/**
 * FaithSelect local Room database.
 * Stores favorites and download metadata — all content streams from Firebase.
 */
@Database(
    entities = [
        FavoriteVerseEntity::class,
        FavoriteAudioEntity::class,
        DownloadedAudioEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FaithSelectDatabase : RoomDatabase() {
    abstract fun favoriteVerseDao(): FavoriteVerseDao
    abstract fun favoriteAudioDao(): FavoriteAudioDao
    abstract fun downloadDao(): DownloadDao
}
