package com.faithselect.data.local.dao

import androidx.room.*
import com.faithselect.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteVerseDao {

    @Query("SELECT * FROM favorite_verses ORDER BY savedAt DESC")
    fun getAllFavoriteVerses(): Flow<List<FavoriteVerseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteVerse(verse: FavoriteVerseEntity)

    @Query("DELETE FROM favorite_verses WHERE id = :verseId")
    suspend fun deleteFavoriteVerse(verseId: String)

    @Query("SELECT COUNT(*) FROM favorite_verses WHERE id = :verseId")
    suspend fun isFavorited(verseId: String): Int
}

@Dao
interface FavoriteAudioDao {

    @Query("SELECT * FROM favorite_audio ORDER BY savedAt DESC")
    fun getAllFavoriteAudio(): Flow<List<FavoriteAudioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteAudio(audio: FavoriteAudioEntity)

    @Query("DELETE FROM favorite_audio WHERE id = :audioId")
    suspend fun deleteFavoriteAudio(audioId: String)

    @Query("SELECT COUNT(*) FROM favorite_audio WHERE id = :audioId")
    suspend fun isFavorited(audioId: String): Int
}

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloaded_audio WHERE audioId = :audioId")
    suspend fun getDownload(audioId: String): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadedAudioEntity)

    @Query("DELETE FROM downloaded_audio WHERE audioId = :audioId")
    suspend fun deleteDownload(audioId: String)

    @Query("SELECT * FROM downloaded_audio")
    fun getAllDownloads(): Flow<List<DownloadedAudioEntity>>
}
