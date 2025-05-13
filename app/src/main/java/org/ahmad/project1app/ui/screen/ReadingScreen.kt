package org.ahmad.project1app.ui.screen

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.ahmad.project1app.R
import org.ahmad.project1app.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                title = {
                    Text(
                        text = stringResource(R.string.module_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    MenuDropdown(
                        text1 = stringResource(R.string.animation_title),
                        text2 = stringResource(R.string.ar_title),
                        text3 = stringResource(R.string.glossary_title),
                        screen1 = Screen.Visual,
                        screen2 = Screen.Augmented,
                        screen3 = Screen.Glossary,
                        navController = navController
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = Color.Black,
                modifier = Modifier.offset(y = (-100).dp)
            ) {
                Icon(Icons.Rounded.Build, stringResource(R.string.glossary))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

    }
}