package com.example.sololevelingapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SoloLevelingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}