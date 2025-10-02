package com.example.thesystem.workers // Or your preferred package

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.thesystem.QuestDao
import com.example.thesystem.QuestEntity
import com.example.thesystem.UserStatsDao // If needed for XP penalties
import com.example.thesystem.workers.QuestWorker.Companion.WORK_NAME
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
        const val WORK_NAME = "QuestResetWorker"
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
            val questsToDelete = mutableListOf<QuestEntity>()
            val questsToAdd = mutableListOf<QuestEntity>()

            Log.d(WORK_NAME, "Current Time for check: $currentTime")

            allQuests
                .filter { questEntity ->
                    val isTerminated = questEntity.isDone || questEntity.hasFailed

                    val deadlineIsOver = questEntity.deadline?.let { deadlineTime ->
                        val passed = deadlineTime <= currentTime
                        if (passed) {
                            Log.d(WORK_NAME, "Quest '${questEntity.text}' (ID: ${questEntity.id}) deadline $deadlineTime passed at $currentTime.")
                        } else {
                            Log.d(WORK_NAME, "Quest '${questEntity.text}' (ID: ${questEntity.id}) deadline $deadlineTime NOT passed at $currentTime.")
                        }
                        passed
                    } ?: false

                    // Quest should be processed if it's terminated AND its deadline has passed
                    val shouldProcess = isTerminated && deadlineIsOver
                    if (shouldProcess) {
                        Log.d(WORK_NAME, "Quest '${questEntity.text}' (ID: ${questEntity.id}) marked for processing. isDone=${questEntity.isDone}, hasFailed=${questEntity.hasFailed}")
                    }
                    shouldProcess
                }
                .forEach { questEntityToProcess ->
                    Log.d(WORK_NAME, "Processing quest: ${questEntityToProcess.text} (Category: ${questEntityToProcess.category})")
                    when (questEntityToProcess.category) {
                        QuestCategory.ONE_TIME, QuestCategory.BOSS -> {
                            questsToDelete.add(questEntityToProcess)
                            Log.d(WORK_NAME, "ONE_TIME/BOSS quest '${questEntityToProcess.text}' added for deletion.")
                        }
                        QuestCategory.DAILY -> {
                            // Delete old daily quest after XP was added/removed and replace with a new one
                            val newDailyQuest = QuestEntity(
                                id = UUID.randomUUID().toString(),
                                text = questEntityToProcess.text,
                                isDone = false,
                                hasFailed = false,
                                category = QuestCategory.DAILY,
                                timeOfCreation = currentTime,
                                duration = questEntityToProcess.duration,
                                xp = calculateXpForQuest(currentStats.level, questEntityToProcess.category, questEntityToProcess.duration),
                                deadline = calculateNewDeadline(currentTime, "daily"),
                            )
                            questsToAdd.add(newDailyQuest)
                            questsToDelete.add(questEntityToProcess)
                            Log.d(WORK_NAME, "DAILY quest '${questEntityToProcess.text}' reset. New quest prepared.")
                        }
                        QuestCategory.WEEKLY -> {
                            // Delete old weekly quest after XP was added/removed and replace with a new one
                            val newWeeklyQuest = QuestEntity(
                                id = UUID.randomUUID().toString(),
                                text = questEntityToProcess.text,
                                isDone = false,
                                hasFailed = false,
                                category = QuestCategory.WEEKLY,
                                timeOfCreation = currentTime,
                                duration = questEntityToProcess.duration,
                                xp = calculateXpForQuest(currentStats.level, questEntityToProcess.category, questEntityToProcess.duration),
                                deadline = calculateNewDeadline(currentTime, "weekly"),
                            )
                            questsToAdd.add(newWeeklyQuest)
                            questsToDelete.add(questEntityToProcess)
                            Log.d(WORK_NAME, "WEEKLY quest '${questEntityToProcess.text}' reset. New quest prepared.")
                        }
                        else -> {
                            Log.w(WORK_NAME, "Unhandled quest category for reset: ${questEntityToProcess.category} for quest '${questEntityToProcess.text}'")
                            // Decide if these should just be deleted or ignored for reset
                            // If they are done/failed and deadline passed, maybe just delete?
                            if (questEntityToProcess.isDone || questEntityToProcess.hasFailed) {
                                questsToDelete.add(questEntityToProcess)
                                Log.d(WORK_NAME, "Generic terminated quest '${questEntityToProcess.text}' with passed deadline added for deletion.")
                            }
                        }
                    }
                }
            if (questsToDelete.isNotEmpty() || questsToAdd.isNotEmpty()) {
                questDao.resetQuests(questsToDelete, questsToAdd)
                Log.d(WORK_NAME, "DB operations: ${questsToDelete.size} deletes, ${questsToAdd.size} adds.")
            } else {
                Log.d(WORK_NAME, "No quests needed processing for deletion or addition.")
            }

            Log.d(WORK_NAME, "Daily quest check at midnight completed.")
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
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (flag) {
            "daily" -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Deadline is start of the day AFTER the current one
            }
            "weekly" -> {
                calendar.add(Calendar.DAY_OF_MONTH, 7) // Deadline is one week from now
            }
        }
        return calendar.timeInMillis
    }
}
