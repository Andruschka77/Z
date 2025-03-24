package com.example.z

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    // Текущее местоположение
    private val _currentLocation = MutableStateFlow<Point?>(null)
    val currentLocation: StateFlow<Point?> get() = _currentLocation

    // Обновление местоположения
    fun updateLocation(point: Point) {
        viewModelScope.launch {
            _currentLocation.value = point
        }
    }
}