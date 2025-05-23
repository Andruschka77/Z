package com.example.z.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.z.model.FriendModel
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _currentLocation = MutableStateFlow<Point?>(null)
    val currentLocation: StateFlow<Point?> get() = _currentLocation

    private val _friends = MutableStateFlow<List<FriendModel>>(emptyList())
    val friends = _friends.asStateFlow()

    // Обновление местоположения
    fun updateLocation(point: Point) {
        viewModelScope.launch {
            _currentLocation.value = point
        }
    }
}