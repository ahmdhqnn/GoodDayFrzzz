package org.ahmad.project1app.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.ahmad.project1app.R
import org.ahmad.project1app.navigation.Screen
import org.ahmad.project1app.ui.theme.Project1appTheme
import org.ahmad.project1app.util.ViewModelFactory

const val KEY_ID_MODULE = "idModule"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(navController: NavHostController, id: Long? = null) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
        val viewModel: ReadingViewModel = viewModel(factory=factory)
    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (id == null) return@LaunchedEffect
        val data = viewModel.getModule(id) ?: return@LaunchedEffect
        title = data.title
        content = data.content
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                title = {
                    Text(
                        text = title,
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


    ) { padding ->
        ScreenContent(Modifier.padding(padding), content)
    }
}

@Composable
private fun ScreenContent(modifier: Modifier, content: String) {
    Column (
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),

    ){
        Text(
            text = content,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    lineHeight = 2.em,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReadingPreview() {
    Project1appTheme {
        ReadingScreen(rememberNavController())
    }
}