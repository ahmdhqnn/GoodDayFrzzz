package org.ahmad.project1app.navigation

import org.ahmad.project1app.ui.screen.KEY_ID_MODULE

sealed class Screen(val route: String) {
    data object Home : Screen("mainScreen")
    data object Splash : Screen("splashScreen")
    data object Augmented : Screen("augmentedScreen")
    data object Visual : Screen("visualScreen")
    data object Modul : Screen("moduleScreen")
    data object Glosarium : Screen("glossaryScreen")
    data object Reading : Screen("readingScreen/{$KEY_ID_MODULE}") {
        fun withId(id: Long) = "readingScreen/$id"
    }
}