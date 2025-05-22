package org.ahmad.project1app.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import org.ahmad.project1app.R
import org.ahmad.project1app.navigation.Screen

@Composable
fun MenuDropdown(
    text1: String,
    text2: String,
    text3: String,
    screen1: Screen,
    screen2: Screen,
    screen3: Screen,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(R.string.menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text1) },
                onClick = {
                    navController.navigate(screen1.route)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(text2) },
                onClick = {
                    navController.navigate(screen2.route)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(text3) },
                onClick = {
                    navController.navigate(screen3.route)
                    expanded = false
                }
            )
        }
    }
}