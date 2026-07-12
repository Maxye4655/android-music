package com.liquidglass.musicplayer.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.liquidglass.musicplayer.ui.component.LiquidGlassBottomBar
import com.liquidglass.musicplayer.ui.component.MiniPlayer
import com.liquidglass.musicplayer.ui.screen.home.HomeScreen
import com.liquidglass.musicplayer.ui.screen.library.LibraryScreen
import com.liquidglass.musicplayer.ui.screen.LoginScreen
import com.liquidglass.musicplayer.ui.screen.LoginViewModel
import com.liquidglass.musicplayer.ui.screen.player.NowPlayingScreen
import com.liquidglass.musicplayer.ui.screen.player.PlayerViewModel
import com.liquidglass.musicplayer.ui.screen.playlist.PlaylistDetailScreen
import com.liquidglass.musicplayer.ui.screen.search.SearchScreen
import com.liquidglass.musicplayer.ui.screen.stats.StatsScreen
import com.liquidglass.musicplayer.ui.theme.*

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Stats : Screen("stats")
    data object Library : Screen("library")
    data object NowPlaying : Screen("now_playing")
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    data object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: String) = "album/$albumId"
    }
    data object Login : Screen("login")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Search, "Search", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Screen.Stats, "Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    BottomNavItem(Screen.Library, "Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)
)

@Composable
fun LiquidGlassNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Stats.route,
        Screen.Library.route
    )

    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                LiquidGlassBottomBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = OnSurfaceVariant,
                                unselectedTextColor = OnSurfaceVariant,
                                indicatorColor = Primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.weight(1f),
                enterTransition = {
                    fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(250)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(200))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(200))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(200)
                    )
                }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        },
                        onAlbumClick = { album ->
                            navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                        },
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route)
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        },
                        onAlbumClick = { album ->
                            navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                        }
                    )
                }

                composable(Screen.Stats.route) {
                    StatsScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        }
                    )
                }

                composable(Screen.Library.route) {
                    LibraryScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        },
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route)
                        }
                    )
                }

                composable(Screen.NowPlaying.route) {
                    NowPlayingScreen(
                        viewModel = playerViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        authManager = hiltViewModel<LoginViewModel>().authManager
                    )
                }

                composable(
                    route = Screen.PlaylistDetail.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                ) {
                    PlaylistDetailScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            MiniPlayer(
                track = currentTrack,
                isPlaying = isPlaying,
                onClick = { navController.navigate(Screen.NowPlaying.route) },
                onPlayPauseClick = { playerViewModel.togglePlayPause() },
                progress = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f
            )
        }
    }
}
