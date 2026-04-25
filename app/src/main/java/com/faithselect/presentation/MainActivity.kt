package com.faithselect.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.faithselect.domain.model.AppTheme
import com.faithselect.TtsHelper
import com.faithselect.presentation.navigation.BottomNavItem
import com.faithselect.presentation.navigation.FaithSelectNavGraph
import com.faithselect.presentation.navigation.Screen
import com.faithselect.presentation.theme.FaithSelectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        TtsHelper.init(this)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            splashScreen.setKeepOnScreenCondition { uiState.isLoading }
            val isDarkTheme = when (uiState.appTheme) {
                AppTheme.DARK           -> true
                AppTheme.LIGHT          -> false
                AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
            }
            FaithSelectTheme(darkTheme = isDarkTheme) {
                FaithSelectApp(
                    isSubscribed = uiState.isSubscribed,
                    isOnboardingComplete = uiState.isOnboardingComplete
                )
            }
        }
    }
}

@Composable
fun FaithSelectApp(
    isSubscribed: Boolean,
    isOnboardingComplete: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavHiddenRoutes = setOf(
        Screen.Splash.route,
        Screen.Onboarding.route,
        Screen.Paywall.route,
        Screen.VerseReader.route,
        Screen.AudioPlayer.route,
        Screen.AskKrishna.route,
        Screen.Mood.route
    )

    val showBottomBar = currentDestination?.route?.let { route ->
        bottomNavHiddenRoutes.none { hiddenRoute ->
            route.startsWith(hiddenRoute.split("/").first())
        }
    } ?: false

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                FaithBottomNavBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { _ ->
        FaithSelectNavGraph(
            navController = navController,
            isSubscribed = isSubscribed,
            isOnboardingComplete = isOnboardingComplete,
        )
    }
}

@Composable
fun FaithBottomNavBar(
    navController: androidx.navigation.NavHostController,
    currentDestination: NavDestination?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        BottomNavItem.items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.screen.route
            } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (item) {
                            is BottomNavItem.Home      -> Icons.Filled.Home
                            is BottomNavItem.Library   -> Icons.Filled.MenuBook
                            is BottomNavItem.Audio     -> Icons.Filled.LibraryMusic
                            is BottomNavItem.Favorites -> Icons.Filled.Favorite
                            is BottomNavItem.Profile   -> Icons.Filled.Person
                            else                       -> Icons.Filled.Home
                        },
                        contentDescription = item.labelResKey
                    )
                },
                label = {
                    Text(
                        text = when (item) {
                            is BottomNavItem.Home      -> "Home"
                            is BottomNavItem.Library   -> "Library"
                            is BottomNavItem.Audio     -> "Audio"
                            is BottomNavItem.Favorites -> "Favorites"
                            is BottomNavItem.Profile   -> "Profile"
                            else                       -> ""
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}