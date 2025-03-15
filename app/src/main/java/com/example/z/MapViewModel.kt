package com.example.z

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    var currentLocation: Point? by mutableStateOf(null)
        private set

    private val _isSatelliteMode = MutableStateFlow(false)
    val isSatelliteMode: StateFlow<Boolean> = _isSatelliteMode

    fun updateLocation(point: Point) {
        currentLocation = point
    }

    fun updateMapStyle(isSatellite: Boolean) {
        viewModelScope.launch {
            _isSatelliteMode.emit(isSatellite)
        }
    }
}