package com.example.z

import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yandex.mapkit.geometry.Point

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Функция для начала обновления местоположения
    fun startLocationUpdates(
        onLocationReceived: (Point) -> Unit,
        onPermissionDenied: () -> Unit,
        interval: Long = 1000L, // Интервал обновления (1 секунда)
        fastestInterval: Long = 500L // Минимальный интервал (500 мс)
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Запрашиваем разрешение, если оно не предоставлено
            onPermissionDenied()
            return
        }

        // Создаем LocationRequest с минимальными интервалами
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            interval
        ).apply {
            setMinUpdateIntervalMillis(fastestInterval)
        }.build()

        // Создаем LocationCallback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationReceived(Point(location.latitude, location.longitude))
                }
            }
        }

        // Запрашиваем обновления местоположения
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    // Функция для получения последнего известного местоположения
    fun getLastKnownLocation(onLocationReceived: (Point?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    onLocationReceived(Point(it.latitude, it.longitude))
                }
            }
        }
    }
}
