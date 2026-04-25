package com.faithselect.domain.repository

import com.faithselect.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Defines all content-related operations.
 * Implementation lives in data layer — domain stays clean and testable.
 */
interface ContentRepository {

    // ─── Religions ────────────────────────────────────────────────────────────
    fun getReligions(): Flow<List<Religion>>

    // ─── Scriptures ──────────────────────────────────────────────────────────
    fun getScripturesByReligion(religionId: String): Flow<List<Scripture>>

    // ─── Chapters ────────────────────────────────────────────────────────────
    fun getChaptersByScripture(scriptureId: String): Flow<List<Chapter>>

    // ─── Verses ──────────────────────────────────────────────────────────────
    fun getVersesByChapter(chapterId: String): Flow<List<Verse>>
    suspend fun getVerseById(verseId: String): Verse?
    fun searchVerses(query: String): Flow<List<Verse>>

    // ─── Audio ────────────────────────────────────────────────────────────────
    fun getAllAudioItems(): Flow<List<AudioItem>>
    fun getAudioByReligion(religionId: String): Flow<List<AudioItem>>
    fun getAudioByCategory(category: AudioCategory): Flow<List<AudioItem>>

    // ─── Daily Content ────────────────────────────────────────────────────────
    suspend fun getDailyContent(): DailyContent?
}

/**
 * Favorites — stored locally in Room DB for offline access.
 */
interface FavoritesRepository {
    fun getFavoriteVerses(): Flow<List<Verse>>
    fun getFavoriteAudio(): Flow<List<AudioItem>>
    suspend fun addVerseToFavorites(verse: Verse)
    suspend fun removeVerseFromFavorites(verseId: String)
    suspend fun addAudioToFavorites(audio: AudioItem)
    suspend fun removeAudioFromFavorites(audioId: String)
    suspend fun isVerseFavorited(verseId: String): Boolean
    suspend fun isAudioFavorited(audioId: String): Boolean
}

/**
 * Subscription — wraps Google Play Billing logic.
 */
interface SubscriptionRepository {
    fun getSubscriptionStatus(): Flow<SubscriptionStatus>
    suspend fun refreshSubscriptionStatus()
    suspend fun acknowledgePurchase(purchaseToken: String)
}

/**
 * User preferences — theme, language, font size, etc.
 */
interface PreferencesRepository {
    fun getAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)

    fun getAppLanguage(): Flow<AppLanguage>
    suspend fun setAppLanguage(language: AppLanguage)

    fun getFontSize(): Flow<Float>
    suspend fun setFontSize(size: Float)

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(complete: Boolean)

    fun getLastPlayedAudioId(): Flow<String>
    suspend fun setLastPlayedAudioId(audioId: String)
}
