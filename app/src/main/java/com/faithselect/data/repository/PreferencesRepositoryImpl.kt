package com.faithselect.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.faithselect.domain.model.AppLanguage
import com.faithselect.domain.model.AppTheme
import com.faithselect.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "faith_select_preferences"
)

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {

    // ─── Keys ─────────────────────────────────────────────────────────────────
    private companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_FONT_SIZE = floatPreferencesKey("font_size")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_LAST_PLAYED_AUDIO = stringPreferencesKey("last_played_audio")

        const val DEFAULT_FONT_SIZE = 16f
    }

    // ─── Theme ────────────────────────────────────────────────────────────────
    override fun getAppTheme(): Flow<AppTheme> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val themeName = prefs[KEY_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
            try { AppTheme.valueOf(themeName) } catch (e: Exception) { AppTheme.SYSTEM_DEFAULT }
        }

    override suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }

    // ─── Language ─────────────────────────────────────────────────────────────
    override fun getAppLanguage(): Flow<AppLanguage> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val langCode = prefs[KEY_LANGUAGE] ?: AppLanguage.ENGLISH.code
            AppLanguage.entries.find { it.code == langCode } ?: AppLanguage.ENGLISH
        }

    override suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.code
        }
    }

    // ─── Font Size ────────────────────────────────────────────────────────────
    override fun getFontSize(): Flow<Float> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_FONT_SIZE] ?: DEFAULT_FONT_SIZE }

    override suspend fun setFontSize(size: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FONT_SIZE] = size.coerceIn(12f, 28f)
        }
    }

    // ─── Onboarding ──────────────────────────────────────────────────────────
    override fun isOnboardingComplete(): Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETE] ?: false }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETE] = complete
        }
    }

    // ─── Last Played ─────────────────────────────────────────────────────────
    override fun getLastPlayedAudioId(): Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_LAST_PLAYED_AUDIO] ?: "" }

    override suspend fun setLastPlayedAudioId(audioId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_PLAYED_AUDIO] = audioId
        }
    }
}
