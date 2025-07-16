package com.example.sololevelingapplication

import androidx.activity.SystemBarStyle.Companion.auto
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val status: String, // active, completed, failed
    val type: String // daily, weekly, etc.
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "currentUser",
    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val strength: Int,
    val agility: Int,
    val perception: Int,
    val vitality: Int,
    val intelligence: Int,
    val availablePoints: Int
)

