package com.example.z

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {

    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        locationHelper = LocationHelper(this) // Инициализация LocationHelper с передачей контекста

        setContent {
            MapScreen(locationHelper = locationHelper)
        }

        if (ActivityCompat.checkSelfPermission( // Запрос разрешений на доступ к местоположению
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else { // Если разрешения уже предоставлены, запросим местоположение
            locationHelper.requestLocation()
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}






