package com.example.thesystem

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.thesystem.workers.QuestWorker
import javax.inject.Inject
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.work.Configuration

@HiltAndroidApp
class TheSYSTEM : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        scheduleDailyQuestCheck()
    }
    private fun scheduleDailyQuestCheck() {
        val workManager = WorkManager.getInstance(applicationContext)

        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0) // Midnight
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (dueTime.before(currentTime) || dueTime.timeInMillis == currentTime.timeInMillis) {
            // If it's already past midnight or exactly midnight, schedule for tomorrow's midnight
            dueTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

        val dailyCheckRequest =
            PeriodicWorkRequestBuilder<QuestWorker>(1, TimeUnit.DAYS) // Repeat every 1 day
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Set initial delay to next midnight
                // .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()) // Optional constraints
                .build()

        workManager.enqueueUniquePeriodicWork(
            QuestWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if you want to update the worker
            dailyCheckRequest
        )
        Log.d("Application", "Scheduled daily failed quest check with initial delay: $initialDelay ms")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG) // Optional: For WorkManager logs
            .build()

    /*override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }*/
}