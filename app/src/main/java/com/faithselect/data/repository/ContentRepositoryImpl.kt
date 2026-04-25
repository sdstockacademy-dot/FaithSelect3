package com.faithselect.data.repository

import com.faithselect.data.local.dao.FavoriteAudioDao
import com.faithselect.data.local.dao.FavoriteVerseDao
import com.faithselect.data.local.entities.FavoriteAudioEntity
import com.faithselect.data.local.entities.FavoriteVerseEntity
import com.faithselect.data.remote.firebase.FirestoreDataSource
import com.faithselect.domain.model.*
import com.faithselect.domain.repository.ContentRepository
import com.faithselect.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : ContentRepository {

    override fun getReligions(): Flow<List<Religion>> = firestoreDataSource.getReligions()

    override fun getScripturesByReligion(religionId: String): Flow<List<Scripture>> =
        firestoreDataSource.getScripturesByReligion(religionId)

    override fun getChaptersByScripture(scriptureId: String): Flow<List<Chapter>> =
        firestoreDataSource.getChaptersByScripture(scriptureId)

    override fun getVersesByChapter(chapterId: String): Flow<List<Verse>> =
        firestoreDataSource.getVersesByChapter(chapterId)

    override suspend fun getVerseById(verseId: String): Verse? =
        firestoreDataSource.getVerseById(verseId)

    override fun searchVerses(query: String): Flow<List<Verse>> =
        firestoreDataSource.searchVerses(query)

    override fun getAllAudioItems(): Flow<List<AudioItem>> =
        firestoreDataSource.getAllAudioItems()

    override fun getAudioByReligion(religionId: String): Flow<List<AudioItem>> =
        firestoreDataSource.getAudioByReligion(religionId)

    override fun getAudioByCategory(category: AudioCategory): Flow<List<AudioItem>> =
        firestoreDataSource.getAudioByCategory(category)

    override suspend fun getDailyContent(): DailyContent? =
        firestoreDataSource.getDailyContent()
}

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val verseDao: FavoriteVerseDao,
    private val audioDao: FavoriteAudioDao
) : FavoritesRepository {

    override fun getFavoriteVerses(): Flow<List<Verse>> =
        verseDao.getAllFavoriteVerses().map { entities ->
            entities.map { it.toVerse() }
        }

    override fun getFavoriteAudio(): Flow<List<AudioItem>> =
        audioDao.getAllFavoriteAudio().map { entities ->
            entities.map { it.toAudioItem() }
        }

    override suspend fun addVerseToFavorites(verse: Verse) {
        verseDao.insertFavoriteVerse(verse.toEntity())
    }

    override suspend fun removeVerseFromFavorites(verseId: String) {
        verseDao.deleteFavoriteVerse(verseId)
    }

    override suspend fun addAudioToFavorites(audio: AudioItem) {
        audioDao.insertFavoriteAudio(audio.toEntity())
    }

    override suspend fun removeAudioFromFavorites(audioId: String) {
        audioDao.deleteFavoriteAudio(audioId)
    }

    override suspend fun isVerseFavorited(verseId: String): Boolean =
        verseDao.isFavorited(verseId) > 0

    override suspend fun isAudioFavorited(audioId: String): Boolean =
        audioDao.isFavorited(audioId) > 0

    // ─── Mapper Extensions ────────────────────────────────────────────────────

    private fun Verse.toEntity() = FavoriteVerseEntity(
        id = id,
        chapterId = chapterId,
        scriptureId = scriptureId,
        religionId = religionId,
        verseNumber = verseNumber,
        chapterNumber = chapterNumber,
        originalText = originalText,
        hindiText = hindiText,
        bengaliText = bengaliText,
        englishText = englishText,
        hindiMeaning = hindiMeaning,
        bengaliMeaning = bengaliMeaning,
        englishMeaning = englishMeaning,
        audioUrl = audioUrl
    )

    private fun FavoriteVerseEntity.toVerse() = Verse(
        id = id,
        chapterId = chapterId,
        scriptureId = scriptureId,
        religionId = religionId,
        verseNumber = verseNumber,
        chapterNumber = chapterNumber,
        originalText = originalText,
        hindiText = hindiText,
        bengaliText = bengaliText,
        englishText = englishText,
        hindiMeaning = hindiMeaning,
        bengaliMeaning = bengaliMeaning,
        englishMeaning = englishMeaning,
        audioUrl = audioUrl
    )

    private fun AudioItem.toEntity() = FavoriteAudioEntity(
        id = id,
        religionId = religionId,
        scriptureId = scriptureId,
        title = title,
        titleHindi = titleHindi,
        titleBengali = titleBengali,
        description = description,
        audioUrl = audioUrl,
        coverImageUrl = coverImageUrl,
        durationSeconds = durationSeconds,
        category = category.name
    )

    private fun FavoriteAudioEntity.toAudioItem() = AudioItem(
        id = id,
        religionId = religionId,
        scriptureId = scriptureId,
        title = title,
        titleHindi = titleHindi,
        titleBengali = titleBengali,
        description = description,
        audioUrl = audioUrl,
        coverImageUrl = coverImageUrl,
        durationSeconds = durationSeconds,
        category = try { AudioCategory.valueOf(category) } catch (e: Exception) { AudioCategory.PRAYER }
    )
}
