package com.example.thesystem.workers // Or your preferred package

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.thesystem.QuestDao
import com.example.thesystem.UserStatsDao // If needed for XP penalties
import com.example.thesystem.questManagement.OverlayCoordinator // If you show overlays from worker
import com.example.thesystem.questManagement.QuestManagementViewModel // For toUiQuest, etc.
import com.example.thesystem.Overlay
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class FailedQuestWorker @AssistedInject constructor(
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
        Log.d(WORK_NAME, "Starting daily check for failed quests.")
        try {
            val currentStats = userStatsDao.getUserStats().first() // Fetch current stats
            if (currentStats == null) {
                Log.e(WORK_NAME, "User stats not found, cannot process quests for XP.")
                return Result.retry() // Or Result.failure() if this is a persistent issue
            }

            val allQuests = questDao.getAllQuests().first() // Get all quest entities
            val currentTime = System.currentTimeMillis()

            allQuests.filter { !it.isDone && !it.hasFailed } // Only check active, non-failed quests
                .forEach { questEntity ->
                    // Your logic to determine if a quest has failed based on its deadline
                    // Assuming QuestEntity has 'deadline: Long?' and 'duration: Int' (in minutes)
                    var questFailed = false
                    if (questEntity.deadline != null && questEntity.deadline < currentTime) {
                        // Deadline is in the past
                        questFailed = true
                        Log.d(WORK_NAME, "Quest '${questEntity.text}' (ID: ${questEntity.id}) failed due to missed deadline.")
                    }
                    // else if (questEntity.category != QuestCategory.ONE_TIME) { // Example: Duration-based failure for dailies/weeklies
                    //    val timeSinceCreation = currentTime - questEntity.timeOfCreation
                    //    val durationMillis = TimeUnit.MINUTES.toMillis(questEntity.duration.toLong())
                    //    if (durationMillis > 0 && timeSinceCreation > durationMillis) {
                    //        questFailed = true
                    //        Log.d(WORK_NAME, "Quest '${questEntity.text}' (ID: ${questEntity.id}) failed due to exceeding duration.")
                    //    }
                    // }


                    if (questFailed) {
                        // Mark quest as failed in DB
                        questDao.updateQuest(questEntity.copy(hasFailed = true, isDone = false))

                        // Optional: Apply XP Penalty directly (background safe)
                        val penalty = questEntity.xp / 2 // Or your penalty logic
                        if (penalty > 0) {
                            val newXp = (currentStats.currentXp - penalty).coerceAtLeast(0)
                            userStatsDao.updateUserStats(currentStats.copy(currentXp = newXp))
                            Log.d(WORK_NAME, "Applied penalty of $penalty XP for failed quest '${questEntity.text}'. New XP: $newXp")
                        }
                        // Showing overlays directly from a background worker is tricky.
                        // Consider a system notification for failed quests instead,
                        // or have the UI observe the quest status and show feedback when the app is next opened.
                    }
                }
            Log.d(WORK_NAME, "Daily check for failed quests completed.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Error during failed quest check", e)
            return Result.failure() // Or Result.retry()
        }
    }
}
