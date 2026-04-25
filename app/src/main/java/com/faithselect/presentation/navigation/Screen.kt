package com.faithselect.presentation.navigation

/**
 * Sealed class defining all navigation routes in the app.
 * Using a sealed class ensures type-safety and prevents typo bugs.
 */
sealed class Screen(val route: String) {

    // ─── Root / Auth Screens ──────────────────────────────────────────────────
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Paywall     : Screen("paywall")

    // ─── Main Bottom Nav Screens ──────────────────────────────────────────────
    object Home        : Screen("home")
    object Library     : Screen("library")
    object Audio       : Screen("audio")
    object Favorites   : Screen("favorites")
    object Profile     : Screen("profile")

    // ─── Content Detail Screens ───────────────────────────────────────────────
    object ScriptureList : Screen("scripture_list/{religionId}") {
        fun createRoute(religionId: String) = "scripture_list/$religionId"
    }
    object ChapterList : Screen("chapter_list/{scriptureId}/{scriptureTitle}") {
        fun createRoute(scriptureId: String, scriptureTitle: String) =
            "chapter_list/$scriptureId/$scriptureTitle"
    }
    object VerseList : Screen("verse_list/{chapterId}/{chapterTitle}") {
        fun createRoute(chapterId: String, chapterTitle: String) =
            "verse_list/$chapterId/$chapterTitle"
    }
    object VerseReader : Screen("verse_reader/{verseId}") {
        fun createRoute(verseId: String) = "verse_reader/$verseId"
    }
    object AudioPlayer : Screen("audio_player/{audioId}") {
        fun createRoute(audioId: String) = "audio_player/$audioId"
    }
    object Search      : Screen("search")
    object DailyVerse  : Screen("daily_verse")

    // ─── Krishna AI Screens (NEW) ─────────────────────────────────────────
    object AskKrishna  : Screen("ask_krishna")
    object Mood        : Screen("mood")
}

/** Bottom navigation items shown in the main scaffold */
sealed class BottomNavItem(
    val screen: Screen,
    val labelResKey: String,
    val iconResKey: String
) {
    object Home      : BottomNavItem(Screen.Home,      "home",      "ic_home")
    object Library   : BottomNavItem(Screen.Library,   "library",   "ic_library")
    object Audio     : BottomNavItem(Screen.Audio,     "audio",     "ic_audio")
    object Favorites : BottomNavItem(Screen.Favorites, "favorites", "ic_favorite")
    object Profile   : BottomNavItem(Screen.Profile,   "profile",   "ic_profile")

    companion object {
        val items = listOf(Home, Library, Audio, Favorites, Profile)
    }
}
