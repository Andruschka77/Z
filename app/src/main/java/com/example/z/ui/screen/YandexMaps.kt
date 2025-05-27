package com.example.z.ui.screen

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.z.R
import com.example.z.utils.LocationHelper
import com.example.z.viewmodel.MapViewModel
import com.yandex.mapkit.map.CameraPosition
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Shader
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    onMapViewReady: (MapView) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
    DisposableEffect(Unit) {
        onMapViewReady(mapView)
        mapView.onStart()
        onDispose {
            mapView.onStop()
        }
    }
}

@Composable
fun YandexMapWithLocationMarker(
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFriendsClick: () -> Unit,
    isUser: Boolean = true,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    var map by remember { mutableStateOf<Map?>(null) }
    var placemark by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    val locationHelper = remember { LocationHelper(context) }
    val currentLocation by viewModel.currentLocation.collectAsState()
    var zoomLevel by remember { mutableStateOf(15.0f) }
    val friends by viewModel.friends.collectAsState()
    var currentUserMarker by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Функция для обновления анимации
    fun updateMarkerAnimation(context: Context, hasFriends: Boolean) {
        animationJob?.cancel()

        if (hasFriends && currentUserMarker != null) {
            animationJob = viewModel.viewModelScope.launch {
                var showFirstImage = true
                while (isActive) { // Используем isActive для безопасной отмены
                    withContext(Dispatchers.Main) {
                        currentUserMarker?.setIcon(
                            ImageProvider.fromResource(
                                context,
                                if (showFirstImage) R.drawable.profile else R.drawable.friends_ava
                            )
                        )
                    }
                    showFirstImage = !showFirstImage
                    delay(1000)
                }
            }
        } else {
            currentUserMarker?.setIcon(
                ImageProvider.fromResource(context, R.drawable.friends_ava)
            )
        }
    }

    // Функция для добавления метки на карту
    fun addPlacemark(point: Point, map: Map) {
        try {
            placemark?.let { map.mapObjects.remove(it) }

            val width = 160
            val height = 200
            val pointerHeight = 40
            val circleRadius = width / 2f
            val circleCenterX = width / 2f
            val circleCenterY = height - pointerHeight - circleRadius

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)

            val bgPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = "#4285F4".toColorInt()
            }

            canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, bgPaint)

            Path().apply {
                moveTo(circleCenterX - 15, height - pointerHeight.toFloat())
                lineTo(circleCenterX, height.toFloat())
                lineTo(circleCenterX + 15, height - pointerHeight.toFloat())
                close()
                canvas.drawPath(this, bgPaint)
            }

            ContextCompat.getDrawable(context, R.drawable.profile)?.let { drawable ->
                val photoBitmap = createBitmap(width, width)
                Canvas(photoBitmap).apply {
                    drawable.setBounds(0, 0, width, width)
                    drawable.draw(this)
                }

                val borderPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.WHITE
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 7f
                }
                canvas.drawCircle(circleCenterX, circleCenterY, circleRadius - 2, borderPaint)

                val photoPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    shader = BitmapShader(photoBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                canvas.drawCircle(circleCenterX, circleCenterY, circleRadius - 9, photoPaint)
            }


            placemark = map.mapObjects.addPlacemark(
                point,
                ImageProvider.fromBitmap(bitmap),
                IconStyle().apply {
                    anchor = android.graphics.PointF(0.5f, 1f)
                    scale = 1.1f
                    zIndex = 100f
                }
            ).apply {
                currentUserMarker = this
                addTapListener { _, _ ->
                    if (isUser) {
                        onProfileClick()
                    } else {
                        onFriendsClick()
                    }
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("MapError", "Failed to create placemark", e)
        }
    }

    // Функция для перемещения камеры к точке
    fun moveCameraToLocation(point: Point, map: Map, zoom: Float = zoomLevel) {
        try {
            map.move(
                CameraPosition(
                    point,
                    zoom,
                    0.0f,
                    0.0f
                )
            )
        } catch (e: RuntimeException) {
            Log.e("MapError", "Failed to move camera: ${e.message}")
        }
    }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null && map != null) {
            addPlacemark(currentLocation!!, map!!)
        }
    }

    LaunchedEffect(friends) {
        friends.forEach { friend ->
            try {
                val (lat, lon) = friend.coordinates.split(",")
                val point = Point(lat.toDouble(), lon.toDouble())
                addPlacemark(point = point, map = map!!,)
            } catch (e: Exception) {
                Log.e("Map", "Invalid coordinates: ${friend.login}")
            }
        }
    }

    LaunchedEffect(friends.isNotEmpty()) {
        updateMarkerAnimation(context, friends.isNotEmpty())
    }

    DisposableEffect(Unit) {
        onDispose {
            animationJob?.cancel()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationHelper.startLocationUpdates(
                onLocationReceived = { point ->
                    viewModel.updateLocation(point)
                    map?.let { map ->
                        addPlacemark(point, map)
                    }
                },
                onPermissionDenied = {
                    println("Разрешение на доступ к местоположению не предоставлено")
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationHelper.startLocationUpdates(
                onLocationReceived = { point ->
                    viewModel.updateLocation(point)
                    map?.let { map ->
                        addPlacemark(point, map)
                    }
                },
                onPermissionDenied = {
                    println("Разрешение на доступ к местоположению не предоставлено")
                }
            )

            locationHelper.getLastKnownLocation { point ->
                point?.let {
                    viewModel.updateLocation(it)
                    map?.let { map ->
                        addPlacemark(it, map)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        YandexMapView(
            modifier = Modifier.fillMaxSize(),
            onMapViewReady = { mapView ->
                map = mapView.map
                currentLocation?.let { point ->
                    addPlacemark(point, mapView.map)
                    moveCameraToLocation(currentLocation!!, map!!)
                }
            }
        )

        // Жесты слева
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        zoomLevel = (zoomLevel - dragAmount / 100).coerceIn(5.0f, 20.0f)
                        currentLocation?.let { point ->
                            map?.let { map ->
                                moveCameraToLocation(point, map, zoomLevel)
                            }
                        }
                    }
                }
        )

        // Жесты справа
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp)
                .align(Alignment.TopEnd)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        zoomLevel = (zoomLevel - dragAmount / 100).coerceIn(5.0f, 20.0f)
                        currentLocation?.let { point ->
                            map?.let { map ->
                                moveCameraToLocation(point, map, zoomLevel)
                            }
                        }
                    }
                }
        )

        // Кнопка для перемещения к местоположению
        IconButton(
            onClick = {
                currentLocation?.let { point ->
                    map?.let { map ->
                        moveCameraToLocation(point, map)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .size(60.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Image(
                painter = painterResource(R.drawable.location),
                contentDescription = "Мое местоположение",
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Мое местоположение",
                )
            }

            IconButton(
                onClick = onFriendsClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.friends),
                    contentDescription = "Мое местоположение",
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "Мое местоположение",
                )
            }
        }
    }
}