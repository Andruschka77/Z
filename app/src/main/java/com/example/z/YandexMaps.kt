package com.example.z

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
import kotlinx.coroutines.launch
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    onMapViewReady: (MapView) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    LaunchedEffect(Unit) {
        onMapViewReady(mapView)
    }
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
    val coroutineScope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    val currentLocation = viewModel.currentLocation

    // Функция для добавления метки на карту
    fun addPlacemark(point: Point, map: Map) {
        val mapObjects: MapObjectCollection = map.mapObjects
        placemark?.let { mapObjects.remove(it) }
        placemark = mapObjects.addPlacemark(point)

        // Перемещаем камеру к метке
        map.move(
            com.yandex.mapkit.map.CameraPosition(
                point,
                15.0f,
                0.0f,
                0.0f
            )
        )
    }

    // Добавляем метку при изменении currentLocation
    LaunchedEffect(currentLocation) {
        if (currentLocation != null && map != null) {
            addPlacemark(currentLocation, map!!)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Разрешение предоставлено, начинаем обновление местоположения
            startLocationUpdates(locationHelper, viewModel, map, ::addPlacemark)
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
            startLocationUpdates(locationHelper, viewModel, map,::addPlacemark)


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
                }
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Кнопка профиля
            Button(onClick = onProfileClick) {
                Text("П")
            }

            // Кнопка друзей
            Button(onClick = onFriendsClick) {
                Text("Д")
            }

            // Кнопка сообщений
            Button(onClick = onMessagesClick) {
                Text("С")
            }

            // Кнопка настроек
            Button(onClick = onSettingsClick) {
                Text("Н")
            }
        }
    }
}

fun startLocationUpdates(
    locationHelper: LocationHelper,
    viewModel: MapViewModel,
    map: Map?,
    addPlacemark: (Point, Map) -> Unit
) {
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val point = Point(location.latitude, location.longitude)
                viewModel.updateLocation(point)
                map?.let { map ->
                    addPlacemark(point, map)
                }
            }
        }
    }

    // Устанавливаем минимальные интервалы обновления
    locationHelper.startLocationUpdates(
        locationCallback = locationCallback,
        interval = 1000L, // Обновление каждую секунду
        fastestInterval = 500L // Минимальный интервал 500 мс
    )
}
