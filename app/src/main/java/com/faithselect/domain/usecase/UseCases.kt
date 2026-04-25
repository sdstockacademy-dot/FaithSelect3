package com.faithselect.domain.usecase

import com.faithselect.domain.model.*
import com.faithselect.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ─── Content Use Cases ────────────────────────────────────────────────────────

class GetReligionsUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(): Flow<List<Religion>> = repository.getReligions()
}

class GetScripturesUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(religionId: String): Flow<List<Scripture>> =
        repository.getScripturesByReligion(religionId)
}

class GetChaptersUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(scriptureId: String): Flow<List<Chapter>> =
        repository.getChaptersByScripture(scriptureId)
}

class GetVersesUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(chapterId: String): Flow<List<Verse>> =
        repository.getVersesByChapter(chapterId)
}

class SearchUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    operator fun invoke(query: String): Flow<List<Verse>> =
        repository.searchVerses(query)
}

class GetDailyContentUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(): DailyContent? = repository.getDailyContent()
}

class GetAudioItemsUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    fun byReligion(religionId: String): Flow<List<AudioItem>> =
        repository.getAudioByReligion(religionId)

    fun all(): Flow<List<AudioItem>> = repository.getAllAudioItems()

    fun byCategory(category: AudioCategory): Flow<List<AudioItem>> =
        repository.getAudioByCategory(category)
}

// ─── Favorites Use Cases ──────────────────────────────────────────────────────

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    fun verses(): Flow<List<Verse>> = repository.getFavoriteVerses()
    fun audio(): Flow<List<AudioItem>> = repository.getFavoriteAudio()
}

class ToggleVerseFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(verse: Verse, isFavorited: Boolean) {
        if (isFavorited) {
            repository.removeVerseFromFavorites(verse.id)
        } else {
            repository.addVerseToFavorites(verse)
        }
    }
}

class ToggleAudioFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(audio: AudioItem, isFavorited: Boolean) {
        if (isFavorited) {
            repository.removeAudioFromFavorites(audio.id)
        } else {
            repository.addAudioToFavorites(audio)
        }
    }
}

// ─── Subscription Use Cases ───────────────────────────────────────────────────

class GetSubscriptionStatusUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<SubscriptionStatus> = repository.getSubscriptionStatus()
}

class RefreshSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke() = repository.refreshSubscriptionStatus()
}

// ─── Preferences Use Cases ────────────────────────────────────────────────────

class GetPreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    fun theme(): Flow<AppTheme> = repository.getAppTheme()
    fun language(): Flow<AppLanguage> = repository.getAppLanguage()
    fun fontSize(): Flow<Float> = repository.getFontSize()
    fun isOnboardingComplete(): Flow<Boolean> = repository.isOnboardingComplete()
}

class UpdatePreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend fun setTheme(theme: AppTheme) = repository.setAppTheme(theme)
    suspend fun setLanguage(language: AppLanguage) = repository.setAppLanguage(language)
    suspend fun setFontSize(size: Float) = repository.setFontSize(size)
    suspend fun completeOnboarding() = repository.setOnboardingComplete(true)
}
