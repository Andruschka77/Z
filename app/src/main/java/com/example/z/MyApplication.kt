package com.example.z

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("60ab69a4-4a2a-489e-bb6b-ae5dc8c7cc67")
        MapKitFactory.initialize(this)
    }
}