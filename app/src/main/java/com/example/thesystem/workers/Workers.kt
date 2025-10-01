package com.example.thesystem.workers // Or your preferred package

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.thesystem.QuestDao
import com.example.thesystem.QuestEntity
import com.example.thesystem.UserStatsDao // If needed for XP penalties
import com.example.thesystem.xpLogic.QuestCategory
import com.example.thesystem.xpLogic.calculateXpForQuest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

@HiltWorker
class QuestWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val questDao: QuestDao,
    private val userStatsDao: UserStatsDao // Inject if you apply penalties directly
    // You might not be able to directly use OverlayCoordinator here easily
    // as it's tied to UI. Consider notifications or other background-safe feedback.
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "FailedQuestCheckWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "Starting daily check at midnight.")
        try {
            val currentStats = userStatsDao.getUserStats().first() // Fetch current stats
            if (currentStats == null) {
                Log.e(WORK_NAME, "User stats not found, cannot process quests.")
                return Result.retry() // Or Result.failure() if this is a persistent issue
            }

            val allQuests = questDao.getAllQuests().first() // Get all quest entities
            val currentTime = System.currentTimeMillis()

            allQuests.filter { it.isDone || it.hasFailed } // Only check completed or failed quests
                .forEach { questEntity ->
                    when (questEntity.category.name) {
                        "ONE_TIME", "BOSS" -> {
                            questDao.deleteQuest(questEntity)
                        }
                        "DAILY" -> {
                            // Delete old quest after XP was added/removed and replace with a new one
                            val newDailyQuest = QuestEntity(
                                id = UUID.randomUUID().toString(),
                                text = questEntity.text,
                                isDone = false,
                                hasFailed = false,
                                category = QuestCategory.DAILY,
                                timeOfCreation = currentTime,
                                duration = questEntity.duration,
                                xp = calculateXpForQuest(currentStats.level, questEntity.category, questEntity.duration),
                                deadline = calculateNewDeadline(currentTime, "daily"),
                            )
                            questDao.insertQuest(newDailyQuest)
                            questDao.deleteQuest(questEntity)
                        }
                        "WEEKLY" -> {
                            // Delete old quest after XP was added/removed and replace with a new one
                            val newWeeklyQuest = QuestEntity(
                                id = UUID.randomUUID().toString(),
                                text = questEntity.text,
                                isDone = false,
                                hasFailed = false,
                                category = QuestCategory.DAILY,
                                timeOfCreation = currentTime,
                                duration = questEntity.duration,
                                xp = calculateXpForQuest(currentStats.level, questEntity.category, questEntity.duration),
                                deadline = calculateNewDeadline(currentTime, "weekly"),
                            )
                            questDao.insertQuest(newWeeklyQuest)
                            questDao.deleteQuest(questEntity)
                        }
                    }
                }
            Log.d(WORK_NAME, "Daily quest check at midnight.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Error during midnight quest check", e)
            return Result.failure() // Or Result.retry()
        }
    }

    private fun calculateNewDeadline(currentTimeMillis: Long, flag: String): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = currentTimeMillis // Current time after midnight

        // Set to the end of the *current* day (which is the new day for the daily quest)
        // Effectively, this means the start of the next day in UTC.
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        if (flag == "daily") {
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Deadline is start of the day AFTER the current one

        // flesh out weekly logic more
        } else if (flag == "weekly") {
            calendar.add(Calendar.DAY_OF_MONTH, 7) // Deadline is one week from now
        }
        return calendar.timeInMillis
    }
}
