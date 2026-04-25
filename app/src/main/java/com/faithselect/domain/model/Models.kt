package com.faithselect.domain.model

/**
 * Represents a religion category (e.g., Hinduism, Christianity, Islam).
 * Designed for multi-religion expansion — just add new entries to Firestore.
 */
data class Religion(
    val id: String = "",
    val name: String = "",
    val nameHindi: String = "",
    val nameBengali: String = "",
    val iconUrl: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

/**
 * Represents a scripture/book within a religion (e.g., Bhagavad Gita, Ramayan).
 */
data class Scripture(
    val id: String = "",
    val religionId: String = "",
    val title: String = "",
    val titleHindi: String = "",
    val titleBengali: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val totalChapters: Int = 0,
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

/**
 * Represents a chapter within a scripture.
 */
data class Chapter(
    val id: String = "",
    val scriptureId: String = "",
    val religionId: String = "",
    val chapterNumber: Int = 0,
    val title: String = "",
    val titleHindi: String = "",
    val titleBengali: String = "",
    val totalVerses: Int = 0,
    val summary: String = ""
)

/**
 * Represents a single verse/shloka. Supports multi-language text.
 * Audio URL is optional — not all verses may have audio.
 */
data class Verse(
    val id: String = "",
    val chapterId: String = "",
    val scriptureId: String = "",
    val religionId: String = "",
    val verseNumber: Int = 0,
    val chapterNumber: Int = 0,
    val originalText: String = "",       // Sanskrit/original language
    val hindiText: String = "",
    val bengaliText: String = "",
    val englishText: String = "",
    val hindiMeaning: String = "",
    val bengaliMeaning: String = "",
    val englishMeaning: String = "",
    val audioUrl: String = "",            // Firebase Storage URL
    val tags: List<String> = emptyList()
)

/**
 * Represents a standalone audio item (e.g., Hanuman Chalisa full audio, stories).
 */
data class AudioItem(
    val id: String = "",
    val religionId: String = "",
    val scriptureId: String = "",        // Optional — can be standalone
    val title: String = "",
    val titleHindi: String = "",
    val titleBengali: String = "",
    val description: String = "",
    val audioUrl: String = "",
    val coverImageUrl: String = "",
    val durationSeconds: Int = 0,
    val category: AudioCategory = AudioCategory.PRAYER,
    val isDownloadable: Boolean = true
)

enum class AudioCategory {
    PRAYER,      // Chalisa, Aarti
    STORY,       // Stories like Bajrangbali, etc.
    TEACHING,    // Discourses
    MEDITATION,  // Meditative music/chants
    VERSE        // Individual verse audio
}

/**
 * Daily content pushed from backend (Firebase Remote Config or Firestore).
 */
data class DailyContent(
    val id: String = "",
    val date: String = "",              // "yyyy-MM-dd" format
    val verseId: String = "",
    val originalText: String = "",
    val hindiText: String = "",
    val bengaliText: String = "",
    val englishText: String = "",
    val source: String = "",            // e.g., "Bhagavad Gita 2.47"
    val backgroundImageUrl: String = ""
)

/**
 * User subscription status — drives all paywall logic.
 */
data class SubscriptionStatus(
    val isSubscribed: Boolean = false,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 0,
    val expiryDateMillis: Long = 0L,
    val purchaseToken: String = "",
    val productId: String = ""
)

/**
 * Supported display languages within the app.
 */
enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी"),
    BENGALI("bn", "বাংলা")
}

/**
 * App theme preference.
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT
}
