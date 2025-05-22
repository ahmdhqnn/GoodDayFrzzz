package org.ahmad.project1app.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.ahmad.project1app.R
import org.ahmad.project1app.model.Glossary
import org.ahmad.project1app.navigation.Screen
import org.ahmad.project1app.ui.theme.Project1appTheme
import org.ahmad.project1app.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(navController: NavHostController) {
    val lazyListState = rememberLazyListState() // Create LazyListState
    val coroutineScope = rememberCoroutineScope() // Create CoroutineScope
    val showScrollToTopButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(

                title = {
                    Text(
                        text = stringResource(R.string.glossary_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, // Or another theme color like primaryContainer
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant, // Or your custom choice
                    actionIconContentColor = MaterialTheme.colorScheme.secondary // Example: using secondary color
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    MenuDropdown(
                        text1 = stringResource(R.string.animation_title),
                        text2 = stringResource(R.string.ar_title),
                        text3 = stringResource(R.string.module_title),
                        screen1 = Screen.Visual,
                        screen2 = Screen.Augmented,
                        screen3 = Screen.Modul,
                        navController = navController
                    )
                },
            )
        },
        floatingActionButton = { // Add the FAB to the Scaffold
            // Show button only if not at the top
            if (showScrollToTopButton) {
                ScrollToTopButton(onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                })
            }
        },
        floatingActionButtonPosition = FabPosition.Center // Optional: Position
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            lazyListState = lazyListState // Pass the state to ScreenContent
        )
    }
}

@Composable
fun ScrollToTopButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = stringResource(R.string.scroll_to_top_cd), // Use string resource
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    lazyListState: LazyListState // Receive the LazyListState
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: GlossaryViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    if (data.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { /* Empty state content */ }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 70.dp)
        ) {
            val grouped = data.groupBy { it.title[0] }
            grouped.forEach { initial, data ->
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = initial.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                items(data) { glossaryItem -> // Renamed 'it' for clarity
                    ListItem(glossary = glossaryItem) {
                        navController.navigate(Screen.Reading.withId(glossaryItem.module_id))
                    }
                }
            }
        }
    }
}


@Composable
private fun ListItem(glossary: Glossary, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }

    ) {
        Text(
            text = glossary.title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)

        )
        Text(
            text = glossary.desc,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )

    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun GlossaryPreview() {
    Project1appTheme {
        GlossaryScreen(rememberNavController())
    }
}

