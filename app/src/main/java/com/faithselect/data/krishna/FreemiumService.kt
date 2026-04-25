package com.faithselect.data.krishna

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.krishnaDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "krishna_freemium")

/**
 * FreemiumService — tracks daily usage limits.
 *
 * FREE USERS:
 *   - 2 questions per day (Ask Krishna)
 *   - 2 audio plays per day
 *   - Resets automatically every 24 hours
 *
 * PAID USERS (₹49/month — payment UI only, billing added later):
 *   - Unlimited questions
 *   - Unlimited audio
 *
 * FOR TESTING:
 *   Call togglePremiumForTesting() to simulate paid account.
 */
@Singleton
class FreemiumService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // DataStore Keys
        val KEY_IS_PREMIUM       = booleanPreferencesKey("is_premium")
        val KEY_QUESTION_COUNT   = intPreferencesKey("question_count")
        val KEY_AUDIO_COUNT      = intPreferencesKey("audio_count")
        val KEY_LAST_RESET_DATE  = stringPreferencesKey("last_reset_date")
        val KEY_STREAK_COUNT     = intPreferencesKey("streak_count")
        val KEY_LAST_STREAK_DATE = stringPreferencesKey("last_streak_date")
        val KEY_TOTAL_QUESTIONS  = intPreferencesKey("total_questions")

        // Free limits
        const val FREE_QUESTION_LIMIT = 2
        const val FREE_AUDIO_LIMIT    = 2
    }

    private fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // ─── Auto-reset daily counters ────────────────────────────────────────────

    /**
     * Call this on app start. Resets question/audio counts if a new day has begun.
     */
    suspend fun checkAndResetDailyCounters() {
        val prefs = context.krishnaDataStore.data.first()
        val lastReset = prefs[KEY_LAST_RESET_DATE] ?: ""
        val today = todayString()

        if (lastReset != today) {
            context.krishnaDataStore.edit { mutablePrefs ->
                mutablePrefs[KEY_QUESTION_COUNT] = 0
                mutablePrefs[KEY_AUDIO_COUNT] = 0
                mutablePrefs[KEY_LAST_RESET_DATE] = today
            }
            updateStreak(lastReset)
        }
    }

    // ─── Streak System ─────────────────────────────────────────────────────────

    private suspend fun updateStreak(lastResetDate: String) {
        val prefs = context.krishnaDataStore.data.first()
        val currentStreak = prefs[KEY_STREAK_COUNT] ?: 0

        if (lastResetDate.isEmpty()) {
            context.krishnaDataStore.edit { it[KEY_STREAK_COUNT] = 1 }
            return
        }

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val lastDate = sdf.parse(lastResetDate) ?: return
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            val lastCal = Calendar.getInstance().apply { time = lastDate }
            val yesCal = Calendar.getInstance().apply { time = yesterday }

            val isYesterday = lastCal.get(Calendar.YEAR) == yesCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == yesCal.get(Calendar.DAY_OF_YEAR)

            context.krishnaDataStore.edit { mutablePrefs ->
                mutablePrefs[KEY_STREAK_COUNT] = if (isYesterday) currentStreak + 1 else 1
            }
        } catch (e: Exception) {
            context.krishnaDataStore.edit { it[KEY_STREAK_COUNT] = 1 }
        }
    }

    // ─── Premium Status ────────────────────────────────────────────────────────

    val isPremiumFlow: Flow<Boolean> = context.krishnaDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_IS_PREMIUM] ?: false }

    suspend fun setPremium(value: Boolean) {
        context.krishnaDataStore.edit { it[KEY_IS_PREMIUM] = value }
    }

    /** FOR TESTING ONLY — toggle premium without payment */
    suspend fun togglePremiumForTesting() {
        val current = context.krishnaDataStore.data.first()[KEY_IS_PREMIUM] ?: false
        context.krishnaDataStore.edit { it[KEY_IS_PREMIUM] = !current }
    }

    // ─── Question Limits ──────────────────────────────────────────────────────

    val questionCountFlow: Flow<Int> = context.krishnaDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_QUESTION_COUNT] ?: 0 }

    suspend fun canAskQuestion(): Boolean {
        val prefs = context.krishnaDataStore.data.first()
        val isPremium = prefs[KEY_IS_PREMIUM] ?: false
        val count = prefs[KEY_QUESTION_COUNT] ?: 0
        return isPremium || count < FREE_QUESTION_LIMIT
    }

    suspend fun incrementQuestionCount() {
        context.krishnaDataStore.edit { prefs ->
            val current = prefs[KEY_QUESTION_COUNT] ?: 0
            prefs[KEY_QUESTION_COUNT] = current + 1
            val total = prefs[KEY_TOTAL_QUESTIONS] ?: 0
            prefs[KEY_TOTAL_QUESTIONS] = total + 1
        }
    }

    // ─── Audio Limits ──────────────────────────────────────────────────────────

    val audioCountFlow: Flow<Int> = context.krishnaDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_AUDIO_COUNT] ?: 0 }

    suspend fun canPlayAudio(): Boolean {
        val prefs = context.krishnaDataStore.data.first()
        val isPremium = prefs[KEY_IS_PREMIUM] ?: false
        val count = prefs[KEY_AUDIO_COUNT] ?: 0
        return isPremium || count < FREE_AUDIO_LIMIT
    }

    suspend fun incrementAudioCount() {
        context.krishnaDataStore.edit { prefs ->
            val current = prefs[KEY_AUDIO_COUNT] ?: 0
            prefs[KEY_AUDIO_COUNT] = current + 1
        }
    }

    // ─── Stats Flow ────────────────────────────────────────────────────────────

    val streakFlow: Flow<Int> = context.krishnaDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_STREAK_COUNT] ?: 0 }

    val totalQuestionsFlow: Flow<Int> = context.krishnaDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_TOTAL_QUESTIONS] ?: 0 }
}
