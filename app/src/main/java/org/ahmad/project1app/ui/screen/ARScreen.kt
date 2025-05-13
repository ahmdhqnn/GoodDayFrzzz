package org.ahmad.project1app.ui.screen

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import org.ahmad.project1app.R
import org.ahmad.project1app.navigation.Screen
import org.ahmad.project1app.ui.theme.Project1appTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(navController: NavHostController) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner = LocalLifecycleOwner.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (!cameraPermissionState.status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                title = {
                    Text(
                        text = stringResource(R.string.ar_title),
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
                            contentDescription = "Back"
                        )
                    }
                },

            )
        }
    ) { innerPadding ->
        when {
            cameraPermissionState.status.isGranted -> {
                ARCameraView(
                    modifier = Modifier.padding(innerPadding),
                    onImageDetected = { imageName ->
                        Log.d("ARScreen", "Image detected: $imageName")
                    }
                )
            }
            cameraPermissionState.status.shouldShowRationale || showPermissionDialog-> {
                // Show rationale dialog if permission was denied before
                CameraPermissionDialog (
                    onRequestPermission = {
                        val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        showPermissionDialog = false
                    },
                    onDismiss = { showPermissionDialog = false }
                )

            }
            else -> {

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.camera_permission_needed),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { showPermissionDialog = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.ask_camera))
                    }
                }
            }
        }
    }


}




@Composable
fun ARCameraView(
    modifier: Modifier = Modifier,
    onImageDetected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)

    // Untuk melacak gambar augmented yang terdeteksi
    val augmentedImageNodes = remember { mutableMapOf<String, AugmentedImageNode>() }
    val frame = remember { mutableStateOf<Frame?>(null) }

    ARScene(
        modifier = modifier.fillMaxSize(),
        engine = engine,
        view = view,
        cameraNode = cameraNode,
        childNodes = childNodes,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        collisionSystem = collisionSystem,
        planeRenderer = true,
        sessionConfiguration = { session, config ->
            config.apply {
                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                focusMode = Config.FocusMode.AUTO

                // Setup augmented image database
                try {
                    val augmentedImageDb = AugmentedImageDatabase(session)
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.target_image
                    )
                    if (bitmap != null) {
                        val imageIndex = augmentedImageDb.addImage("target_ikan", bitmap)
                        if (imageIndex >= 0) { // Perubahan disini: periksa imageIndex >= 0 bukan added
                            Log.d(
                                "ARScreen",
                                "Image successfully added to database with index: $imageIndex"
                            )
                            augmentedImageDatabase = augmentedImageDb
                        } else {
                            Log.e("ARScreen", "Failed to add image to database")
                        }
                    } else {
                        Log.e("ARScreen", "Could not load image bitmap")
                    }
                } catch (e: Exception) {
                    Log.e("ARScreen", "Error setting up augmented image database", e)
                }
            }
        },
        onSessionUpdated = { _, updatedFrame ->
            frame.value = updatedFrame

            // Process augmented images
            val updatedAugmentedImages =
                updatedFrame.getUpdatedTrackables(AugmentedImage::class.java)

            for (augmentedImage in updatedAugmentedImages) {
                when (augmentedImage.trackingState) {
                    TrackingState.TRACKING -> {
                        if (!augmentedImageNodes.containsKey(augmentedImage.name)) {
                            onImageDetected(augmentedImage.name)
                            Log.d("ARScreen", "Image tracking: ${augmentedImage.name}")

                            try {
                                val node = AugmentedImageNode(
                                    engine = engine,
                                    augmentedImage = augmentedImage
                                )

                                val modelUri =
                                    "android.resource://${context.packageName}/${R.raw.respiratory_system}"
                                val modelNode = ModelNode(
                                    modelInstance = modelLoader.createModelInstance(modelUri)
                                ).apply {
                                    scale = Scale(1f) // Ubah skala sementara
                                    position = Position(0f, 0f, 0f) // Ubah posisi sementara
                                }

                                node.addChildNode(modelNode)
                                childNodes.add(node)
                                augmentedImageNodes[augmentedImage.name] = node

                                Log.d(
                                    "ARScreen",
                                    "Augmented Image Center Pose: ${augmentedImage.centerPose.translation.contentToString()}"
                                )
                                Log.d("ARScreen", "Model Node Position: ${modelNode.position}")
                                Log.d("ARScreen", "Model Node Scale: ${modelNode.scale}")
                                Log.d("ARScreen", "Added model for image: ${augmentedImage.name}")
                            } catch (e: Exception) {
                                Log.e("ARScreen", "Error creating augmented image node", e)
                            }
                        }
                    }

                    TrackingState.STOPPED -> {
                        augmentedImageNodes.remove(augmentedImage.name)?.let { node ->
                            childNodes.remove(node)
                            Log.d("ARScreen", "Removed node for image: ${augmentedImage.name}")
                        }
                    }

                    else -> {}
                }
            }
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { motionEvent: MotionEvent, node: Node? ->
                // Optional: handle taps on the AR scene
                Log.d("ARScreen", "Tap detected on ${node?.name ?: "empty space"}")
            }
        )
    )
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ARPreview() {
    Project1appTheme {
        ARScreen(rememberNavController())
    }
}