package com.example.z

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MapViewModel: ViewModel() {
    var currentLocation: Point? by mutableStateOf(null)
        private set

    fun updateLocation(point: Point) {
        currentLocation = point
    }
}