package org.ahmad.project1app.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.ahmad.project1app.R
import org.ahmad.project1app.model.Module
import org.ahmad.project1app.navigation.Screen
import org.ahmad.project1app.ui.theme.Project1appTheme
import org.ahmad.project1app.util.ViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(navController: NavHostController) {
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
                        screen3 = Screen.Glosarium,
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
    ) { innerPadding ->
        ScreenContent(Modifier.padding(innerPadding), navController)

    }
}



@Composable
private fun ScreenContent(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: ModuleViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(data) {
            ListItem(module = it) {
                navController.navigate(Screen.Reading.withId(it.id))
            }


        }
    }
}

@Composable
private fun ListItem(module: Module, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
            .fillMaxWidth()

    ) {
        Text(
            text = module.title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)

        )

    }

}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ModulePreview() {
    Project1appTheme {
        ModuleScreen(rememberNavController())
    }
}