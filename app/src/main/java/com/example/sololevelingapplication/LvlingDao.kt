package com.example.sololevelingapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE type = :questType")
    fun getQuestByType(questType: String): Flow<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Delete
    suspend fun deleteQuest(quest: QuestEntity)
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun getUserStats(userId: String = "currentUser"): Flow<List<UserStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserStats(stats: UserStatsEntity)

    @Update
    fun updateUserStats(stats: UserStatsEntity)
}