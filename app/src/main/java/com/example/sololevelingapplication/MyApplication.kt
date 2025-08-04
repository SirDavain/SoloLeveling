package com.example.sololevelingapplication

import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import androidx.multidex.MultiDexApplication
import androidx.multidex.MultiDex

/*@HiltAndroidApp
class SoloLevelingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}*/

@HiltAndroidApp
class SoloLevelingApplication : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}