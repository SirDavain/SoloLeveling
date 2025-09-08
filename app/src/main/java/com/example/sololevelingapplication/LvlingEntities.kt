package com.example.sololevelingapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sololevelingapplication.xpLogic.QuestCategory

@Entity(tableName = "quests")
@TypeConverters(Converters::class)
data class QuestEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(), // a unique ID
    var text: String,
    var isDone: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    var hasFailed: Boolean = false,
    var category: QuestCategory,
    @ColumnInfo(defaultValue = "0")
    var xp: Int,
    @ColumnInfo(defaultValue = "0")
    val timeOfCreation: Long
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "currentUser",
    var level: Int,
    var currentXp: Int,
    var xpToNextLevel: Int,
    var strength: Int,
    var agility: Int,
    val perception: Int,
    val vitality: Int,
    val intelligence: Int,
    val availablePoints: Int
)

