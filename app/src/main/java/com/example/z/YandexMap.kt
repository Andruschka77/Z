package com.example.z

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sulgik.mapkit.MapKit
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.bindToLifecycleOwner
import ru.sulgik.mapkit.compose.rememberAndInitializeMapKit
import ru.sulgik.mapkit.compose.rememberCameraPositionState
import ru.sulgik.mapkit.geometry.Point
import ru.sulgik.mapkit.map.CameraPosition

fun initMapKit() {
    MapKit.setApiKey("60ab69a4-4a2a-489e-bb6b-ae5dc8c7cc67")
}

val startPosition = CameraPosition(Point(55.751225, 37.62954), 17.0f, 150.0f, 30.0f)

@Composable
fun MapScreen() {
    rememberAndInitializeMapKit().bindToLifecycleOwner()
    val cameraPositionState = rememberCameraPositionState { position = startPosition }
    YandexMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize()
    )
}
