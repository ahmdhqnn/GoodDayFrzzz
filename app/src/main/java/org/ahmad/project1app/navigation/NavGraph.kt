package org.ahmad.project1app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.ahmad.project1app.ui.screen.ARScreen
import org.ahmad.project1app.ui.screen.AnimatedSplashScreen
import org.ahmad.project1app.ui.screen.GlosariumScreen
import org.ahmad.project1app.ui.screen.KEY_ID_MODULE
import org.ahmad.project1app.ui.screen.MenuScreen
import org.ahmad.project1app.ui.screen.ModuleScreen
import org.ahmad.project1app.ui.screen.ReadingScreen
import org.ahmad.project1app.ui.screen.VisualScreen


@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Home.route) {
            MenuScreen(navController)
        }
        composable(route = Screen.Splash.route) {
            AnimatedSplashScreen(navController)
        }
        composable(route = Screen.Augmented.route) {
            ARScreen(navController)
        }
        composable(route = Screen.Visual.route) {
            VisualScreen(navController)
        }
        composable(route = Screen.Modul.route) {
            ModuleScreen(navController)
        }
        composable(route = Screen.Glosarium.route) {
            GlosariumScreen(navController)
        }
        composable(
            route = Screen.Reading.route,
            arguments = listOf(
                navArgument(KEY_ID_MODULE) { type = NavType.LongType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong(KEY_ID_MODULE)
            ReadingScreen(navController,id)
        }

    }
}
