package com.example.z

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yandex.mapkit.geometry.Point

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Функция для начала обновления местоположения
    fun startLocationUpdates(
        locationCallback: LocationCallback,
        interval: Long = 5000L,
        fastestInterval: Long = 2000L,
        requestPermissionLauncher: (() -> Unit)? = null
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Запрашиваем разрешение, если оно не предоставлено
            requestPermissionLauncher?.invoke()
            return
        }

        // Создаем LocationRequest с минимальными интервалами
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            interval
        ).apply {
            setMinUpdateIntervalMillis(fastestInterval)
        }.build()

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
