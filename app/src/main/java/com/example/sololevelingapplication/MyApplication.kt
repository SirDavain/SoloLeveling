package com.example.sololevelingapplication

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import androidx.multidex.MultiDexApplication

@HiltAndroidApp
class TheSYSTEM : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}