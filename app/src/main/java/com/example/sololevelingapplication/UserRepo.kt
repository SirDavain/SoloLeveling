package com.example.sololevelingapplication

import kotlinx.coroutines.flow.Flow
import jakarta.inject.Inject
import com.example.sololevelingapplication.QuestEntity

class UserRepo @Inject constructor(
    private val questDao: QuestDao,
    private val userStatsDao: UserStatsDao
) {
    fun getAllQuests(): Flow<List<QuestEntity>> = questDao.getAllQuests()
    suspend fun addQuest(quest: QuestEntity) = questDao.insertQuest(quest)

    fun getUserStats(): Flow<List<UserStatsEntity>> = userStatsDao.getUserStats()
    suspend fun saveUserStats(stats: UserStatsEntity) = userStatsDao.saveUserStats(stats)
}