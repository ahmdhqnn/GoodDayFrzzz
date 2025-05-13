package org.ahmad.project1app.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.ahmad.project1app.R

@Composable
fun CameraPermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(

        text = { Text(text = stringResource(R.string.camera_permission_needed)) },
        confirmButton = {
            TextButton(onClick = {onRequestPermission()}) {
                Text(text = stringResource(R.string.camera_granted))
            }
        },
        dismissButton = {
            TextButton(onClick = {onDismiss()}) {
                Text(text = stringResource(R.string.camera_rejected))
            }
        },
        onDismissRequest = {onDismiss()},
    )
}