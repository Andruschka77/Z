package com.example.z

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.geometry.Point

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(
        onLocationReceived: (Point?) -> Unit,
        requestPermissionLauncher: ActivityResultLauncher<String>? = null
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher?.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            onLocationReceived(null)
            return
        }

        // Получаем последнее известное местоположение
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                val point = Point(location.latitude, location.longitude)
                onLocationReceived(point)
            } else {
                onLocationReceived(null)
            }
        }
    }
}