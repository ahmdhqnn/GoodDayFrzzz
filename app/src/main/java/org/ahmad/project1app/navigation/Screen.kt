package org.ahmad.project1app.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object Splash: Screen("splashScreen")
    data object Augmented: Screen("augmentedScreen")
    data object Visual: Screen("visualScreen")
    data object Modul: Screen("moduleScreen")
    data object Glossary: Screen("glosariumScreen")
}