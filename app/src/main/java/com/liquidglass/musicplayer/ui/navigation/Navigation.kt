package com.liquidglass.musicplayer.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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

private val mainTabRoutes = bottomNavItems.map { it.screen.route }.toSet()

@Composable
fun LiquidGlassNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isOverlayScreen = currentRoute != null && currentRoute !in mainTabRoutes

    val pagerState = rememberPagerState(pageCount = { bottomNavItems.size })

    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    LaunchedEffect(pagerState.currentPage) {
        val targetRoute = bottomNavItems[pagerState.currentPage].screen.route
        if (currentRoute == null || currentRoute !in mainTabRoutes || currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(Screen.Home.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute in mainTabRoutes) {
            val targetIndex = bottomNavItems.indexOfFirst { it.screen.route == currentRoute }
            if (targetIndex >= 0 && targetIndex != pagerState.currentPage) {
                pagerState.animateScrollToPage(targetIndex)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LiquidGlassBottomBar {
                bottomNavItems.forEachIndexed { index, item ->
                    val selected = pagerState.currentPage == index && !isOverlayScreen

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
                            if (isOverlayScreen) {
                                navController.popBackStack()
                            }
                            if (pagerState.currentPage != index) {
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
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = !isOverlayScreen
            ) { page ->
                when (page) {
                    0 -> HomeScreen(
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
                    1 -> SearchScreen(
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
                    2 -> StatsScreen(
                        onTrackClick = { track ->
                            playerViewModel.playTrack(track)
                            navController.navigate(Screen.NowPlaying.route)
                        }
                    )
                    3 -> LibraryScreen(
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
            }

            if (isOverlayScreen) {
                NavHost(
                    navController = navController,
                    startDestination = currentRoute!!,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(200))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(200))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(200))
                    }
                ) {
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
