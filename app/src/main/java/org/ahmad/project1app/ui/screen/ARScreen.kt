package org.ahmad.project1app.ui.screen

import android.Manifest
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.CubeNode
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
    var isTrackingImage by remember { mutableStateOf(false) }
    var detectedImageName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Or another theme color like primaryContainer

                    // Title content color will be MaterialTheme.colorScheme.onSurface by default.
                    // Let's say you want it to always be your primary color:
                    titleContentColor = MaterialTheme.colorScheme.primary,

                    // Navigation icon color will be MaterialTheme.colorScheme.onSurfaceVariant or onSurface by default.
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant, // Or your custom choice

                    // Action icon color will be MaterialTheme.colorScheme.onSurfaceVariant or onSurface by default.
                    actionIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.ar_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
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
                }
            )
        }
    ) { innerPadding ->
        when {
            cameraPermissionState.status.isGranted -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ARCameraView(
                        modifier = Modifier.padding(innerPadding),
                        onImageDetectionStateChanged = { imageName, isTracking ->
                            isTrackingImage = isTracking
                            if (isTracking) {
                                detectedImageName = imageName
                            }
                        },
                        onError = { message ->
                            errorMessage = message
                        }
                    )

                    // Overlay UI showing tracking status
                    if (!isTrackingImage) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Arahkan kamera ke gambar target",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CircularProgressIndicator()
                        }
                    } else {
                        Text(
                            text = "Terdeteksi: $detectedImageName",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 80.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Show error message if any
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            cameraPermissionState.status.shouldShowRationale || showPermissionDialog -> {
                PermissionRationaleDialog(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
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
                        Text("Minta Izin Kamera")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRationaleDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Izin Kamera Diperlukan") },
        text = { Text("Aplikasi membutuhkan izin kamera untuk menampilkan fitur AR") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Berikan Izin")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ARCameraView(
    modifier: Modifier = Modifier,
    onImageDetectionStateChanged: (String, Boolean) -> Unit = { _, _ -> },
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)

    var trackingImageName by remember { mutableStateOf("") }

    // Clean up resources when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            childNodes.clear()
        }
    }

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

                try {
                    val augmentedImageDb = AugmentedImageDatabase(session)
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.target_image
                    )
                    if (bitmap != null) {
                        val imageIndex = augmentedImageDb.addImage("target_image", bitmap, 0.2f)
                        if (imageIndex >= 0) {
                            Log.d("ARScreen", "Gambar berhasil ditambahkan: $imageIndex")
                            augmentedImageDatabase = augmentedImageDb
                        } else {
                            onError("Gagal menambahkan gambar target")
                        }
                    } else {
                        onError("Gambar target tidak ditemukan di drawable")
                    }
                } catch (e: Exception) {
                    Log.e("ARScreen", "Error menyiapkan database gambar", e)
                    onError("Error database gambar: ${e.localizedMessage}")
                }
            }
        },
        onSessionUpdated = { _, updatedFrame ->
            val updatedAugmentedImages = updatedFrame.getUpdatedTrackables(AugmentedImage::class.java)
            for (augmentedImage in updatedAugmentedImages) {
                when (augmentedImage.trackingState) {
                    TrackingState.TRACKING -> {
                        val imageName = augmentedImage.name
                        if (trackingImageName != imageName) {
                            trackingImageName = imageName
                            onImageDetectionStateChanged(imageName, true)
                            Log.d("ARScreen", "Gambar terdeteksi: $imageName")

                            try {
                                // Buat node untuk augmented image
                                val imageNode = AugmentedImageNode(
                                    engine = engine,
                                    augmentedImage = augmentedImage
                                )

                                // Muat model 3D
                                val modelInstance = try {
                                    modelLoader.createModelInstance("models/respiratory_system.glb")
                                } catch (e: Exception) {
                                    Log.e("ARScreen", "Gagal memuat model 3D: ${e.message}", e)
                                    throw Exception("Gagal memuat model, kemungkinan tekstur hilang: ${e.localizedMessage}")
                                }

                                val modelNode = io.github.sceneview.node.ModelNode(
                                    modelInstance = modelInstance
                                ).apply {
                                    scale = Scale(0.01724f) // Mengurangi skala menjadi 1/58 dari ukuran asli
                                    position = Position(0.0f, 0.5f, 0.0f) // Tetap mengangkat model sesuai tinggi asli
                                    rotation = Rotation(
                                        x = 180.0f, // Membalikkan kepala ke atas
                                        y = 180.0f, // Menghadap ke kamera
                                        z = 0.0f
                                    )
                                    isVisible = true
                                    Log.d("ARScreen", "Model posisi: $position, skala: $scale, rotasi: $rotation")
                                }

                                imageNode.addChildNode(modelNode)
                                childNodes.add(imageNode)
                                Log.d("ARScreen", "Model 3D ditampilkan")
                            } catch (e: Exception) {
                                Log.e("ARScreen", "Error menampilkan model 3D", e)
                                onError("Error menampilkan model: ${e.localizedMessage}")

                                // Fallback ke kubus
                                val imageNode = AugmentedImageNode(
                                    engine = engine,
                                    augmentedImage = augmentedImage
                                )
                                val cubeNode = CubeNode(
                                    engine = engine,
                                    size = Scale(0.05f, 0.05f, 0.05f),
                                    center = Position(0.0f, 0.025f, 0.0f)
                                )
                                imageNode.addChildNode(cubeNode)
                                childNodes.add(imageNode)
                            }
                        }
                    }
                    TrackingState.STOPPED -> {
                        if (trackingImageName == augmentedImage.name) {
                            trackingImageName = ""
                            onImageDetectionStateChanged("", false)
                            childNodes.removeIf { it is AugmentedImageNode }
                            Log.d("ARScreen", "Pelacakan gambar berhenti: ${augmentedImage.name}")
                        }
                    }
                    else -> {}
                }
            }
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { _: MotionEvent, _: Node? ->
                childNodes.forEach { node ->
                    if (node is AugmentedImageNode) {
                        node.childNodes.forEach { child ->
                            if (child is io.github.sceneview.node.ModelNode) {
                                child.rotation = io.github.sceneview.math.Rotation(
                                    x = child.rotation.x,
                                    y = child.rotation.y + 90f,
                                    z = child.rotation.z
                                )
                            }
                        }
                    }
                }
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