package com.example.z

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier

@Composable
fun MapScreen(
    locationHelper: LocationHelper
) {
    val location = locationHelper.locationFlow.collectAsState().value

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                map.move(
                    CameraPosition(Point(55.751244, 37.618423), 11.0f, 0.0f, 0.0f)
                )

                if (location != null) {
                    val currentLocation = Point(location.latitude, location.longitude)
                    map.move(CameraPosition(currentLocation, 15.0f, 0.0f, 0.0f))
                    map.mapObjects.addPlacemark(currentLocation)
                }
            }
        },
        update = { mapView ->
            if (location != null) {
                val currentLocation = Point(location.latitude, location.longitude)
                mapView.map.move(CameraPosition(currentLocation, 15.0f, 0.0f, 0.0f))
                mapView.map.mapObjects.addPlacemark(currentLocation)
            }
        }
    )
}
