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
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import android.Manifest
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.z.R
import com.example.z.utils.LocationHelper
import com.example.z.viewmodel.MapViewModel
import com.yandex.mapkit.map.CameraPosition

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
    onMessagesClick: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    var map by remember { mutableStateOf<Map?>(null) }
    var placemark by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    val locationHelper = remember { LocationHelper(context) }
    val currentLocation by viewModel.currentLocation.collectAsState()
    var zoomLevel by remember { mutableStateOf(15.0f) } // Уровень масштабирования

    // Функция для добавления метки на карту
    fun addPlacemark(point: Point, map: Map) {
        try {
            val mapObjects: MapObjectCollection = map.mapObjects
            placemark?.let { mapObjects.remove(it) } // Удаляем старую метку, если она есть
            placemark = mapObjects.addPlacemark(point) // Добавляем новую метку
        } catch (e: RuntimeException) {
            Log.e("MapError", "Failed to add placemark: ${e.message}")
        }
    }

    // Функция для перемещения камеры к точке
    fun moveCameraToLocation(point: Point, map: Map, zoom: Float = zoomLevel) {
        try {
            map.move(
                CameraPosition(
                    point,
                    zoom, // Уровень приближения
                    0.0f,  // Азимут
                    0.0f   // Наклон
                )
            )
        } catch (e: RuntimeException) {
            Log.e("MapError", "Failed to move camera: ${e.message}")
        }
    }

    // Перемещаем камеру при изменении currentLocation
    LaunchedEffect(currentLocation) {
        if (currentLocation != null && map != null) {
            addPlacemark(currentLocation!!, map!!)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Разрешение предоставлено, начинаем обновление местоположения
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
            // Запрашиваем разрешение
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Разрешение уже предоставлено, начинаем обновление местоположения
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

            // Используем последнее известное местоположение, если оно доступно
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

        // Прозрачный Box для обработки жестов у левого края экрана
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp) // Ширина области для жестов
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        // Изменяем уровень масштабирования
                        zoomLevel = (zoomLevel - dragAmount / 100).coerceIn(5.0f, 20.0f)
                        currentLocation?.let { point ->
                            map?.let { map ->
                                moveCameraToLocation(point, map, zoomLevel)
                            }
                        }
                    }
                }
        )

        // Прозрачный Box для обработки жестов у правого края экрана
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp) // Ширина области для жестов
                .align(Alignment.TopEnd)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        // Изменяем уровень масштабирования
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
                .padding(bottom = 16.dp) // Отступ снизу больше, чтобы не мешать кнопке "Назад"
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

        // 4 кнопки навигации (справа сверху)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Кнопка профиля
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

            // Кнопка друзей
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

            // Кнопка сообщений
            IconButton(
                onClick = onMessagesClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.messages),
                    contentDescription = "Мое местоположение",
                )
            }

            // Кнопка настроек
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