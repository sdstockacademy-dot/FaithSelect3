package com.faithselect.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.faithselect.presentation.audio.AudioListScreen
import com.faithselect.presentation.audio.AudioPlayerScreen
import com.faithselect.presentation.favorites.FavoritesScreen
import com.faithselect.presentation.home.HomeScreen
import com.faithselect.presentation.home.SearchScreen
import com.faithselect.presentation.krishna.AskKrishnaScreen
import com.faithselect.presentation.library.*
import com.faithselect.presentation.mood.MoodScreen
import com.faithselect.presentation.onboarding.OnboardingScreen
import com.faithselect.presentation.paywall.PaywallScreen
import com.faithselect.presentation.profile.ProfileScreen
import com.faithselect.presentation.reader.VerseListScreen
import com.faithselect.presentation.reader.VerseReaderScreen
import com.faithselect.presentation.splash.SplashScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FaithSelectNavGraph(
    navController: NavHostController,
    isSubscribed: Boolean,
    isOnboardingComplete: Boolean
) {
    val startDestination = when {
        !isOnboardingComplete -> Screen.Onboarding.route
        !isSubscribed         -> Screen.Paywall.route
        else                  -> Screen.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(animationSpec = tween(300)) { it / 4 }
        },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                    slideOutHorizontally(animationSpec = tween(300)) { it / 4 }
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(startDestination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(
                        if (isSubscribed) Screen.Home.route else Screen.Paywall.route
                    ) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(
                onSubscribed = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Paywall.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDailyVerse = { navController.navigate(Screen.DailyVerse.route) },
                onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                onNavigateToAudio = { navController.navigate(Screen.Audio.route) },
                onNavigateToAskKrishna = { navController.navigate(Screen.AskKrishna.route) },
                onNavigateToMood = { navController.navigate(Screen.Mood.route) }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onReligionClick = { religionId ->
                    navController.navigate(Screen.ScriptureList.createRoute(religionId))
                }
            )
        }

        composable(Screen.Audio.route) {
            AudioListScreen(
                onAudioClick = { audioId ->
                    navController.navigate(Screen.AudioPlayer.createRoute(audioId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onVerseClick = { verseId ->
                    navController.navigate(Screen.VerseReader.createRoute(verseId))
                },
                onAudioClick = { audioId ->
                    navController.navigate(Screen.AudioPlayer.createRoute(audioId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onVerseClick = { verseId ->
                    navController.navigate(Screen.VerseReader.createRoute(verseId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Krishna AI Screens (NEW) ──────────────────────────────────────
        composable(Screen.AskKrishna.route) {
            AskKrishnaScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Mood.route) {
            MoodScreen(onBack = { navController.popBackStack() })
        }

        // ─── Scripture Hierarchy ───────────────────────────────────────────
        composable(
            route = Screen.ScriptureList.route,
            arguments = listOf(navArgument("religionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val religionId = backStackEntry.arguments?.getString("religionId") ?: return@composable
            ScriptureListScreen(
                religionId = religionId,
                onScriptureClick = { id, title ->
                    navController.navigate(Screen.ChapterList.createRoute(id, title))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ChapterList.route,
            arguments = listOf(
                navArgument("scriptureId") { type = NavType.StringType },
                navArgument("scriptureTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scriptureId = backStackEntry.arguments?.getString("scriptureId") ?: return@composable
            val scriptureTitle = backStackEntry.arguments?.getString("scriptureTitle") ?: ""
            ChapterListScreen(
                scriptureId = scriptureId,
                scriptureTitle = scriptureTitle,
                onChapterClick = { id, title ->
                    navController.navigate(Screen.VerseList.createRoute(id, title))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VerseList.route,
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("chapterTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: return@composable
            val chapterTitle = backStackEntry.arguments?.getString("chapterTitle") ?: ""
            VerseListScreen(
                chapterId = chapterId,
                chapterTitle = chapterTitle,
                onVerseClick = { verseId ->
                    navController.navigate(Screen.VerseReader.createRoute(verseId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VerseReader.route,
            arguments = listOf(navArgument("verseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val verseId = backStackEntry.arguments?.getString("verseId") ?: return@composable
            VerseReaderScreen(
                verseId = verseId,
                onAudioPlay = {},
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AudioPlayer.route,
            arguments = listOf(navArgument("audioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val audioId = backStackEntry.arguments?.getString("audioId") ?: return@composable
            AudioPlayerScreen(audioId = audioId, onBack = { navController.popBackStack() })
        }

        composable(Screen.DailyVerse.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToDailyVerse = {},
                onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                onNavigateToAudio = { navController.navigate(Screen.Audio.route) },
                onNavigateToAskKrishna = { navController.navigate(Screen.AskKrishna.route) },
                onNavigateToMood = { navController.navigate(Screen.Mood.route) }
            )
        }
    }
}
