package com.espezzialy.simpleplayer.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.espezzialy.simpleplayer.presentation.album.AlbumDetailRoute
import com.espezzialy.simpleplayer.presentation.songs.SongsRoute

private const val ROUTE_SONGS = "songs"
private const val ROUTE_ALBUM = "album/{collectionId}"
private const val ARG_COLLECTION_ID = "collectionId"

private const val SLIDE_DURATION_MS = 320

@Composable
fun SimplePlayerNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_SONGS,
        modifier = modifier
    ) {
        composable(
            route = ROUTE_SONGS,
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(SLIDE_DURATION_MS),
                    targetOffsetX = { fullWidth -> -fullWidth }
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(SLIDE_DURATION_MS),
                    initialOffsetX = { fullWidth -> -fullWidth }
                )
            }
        ) {
            SongsRoute(
                onNavigateToAlbum = { collectionId ->
                    navController.navigate("album/$collectionId")
                }
            )
        }
        composable(
            route = ROUTE_ALBUM,
            arguments = listOf(
                navArgument(ARG_COLLECTION_ID) { type = NavType.LongType }
            ),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(SLIDE_DURATION_MS),
                    initialOffsetX = { fullWidth -> fullWidth }
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(SLIDE_DURATION_MS),
                    targetOffsetX = { fullWidth -> fullWidth }
                )
            }
        ) {
            AlbumDetailRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
