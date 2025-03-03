package com.example.z

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.z.ui.theme.ZTheme
import ru.sulgik.mapkit.MapKit
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.bindToLifecycleOwner
import ru.sulgik.mapkit.compose.rememberAndInitializeMapKit
import ru.sulgik.mapkit.compose.rememberCameraPositionState
import ru.sulgik.mapkit.geometry.Point
import ru.sulgik.mapkit.map.CameraPosition

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZTheme {
                initMapKit()
                MapScreen()
            }
        }
    }
}

