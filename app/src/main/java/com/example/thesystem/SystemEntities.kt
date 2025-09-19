package com.example.thesystem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.thesystem.xpLogic.QuestCategory

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
    val timeOfCreation: Long,
    @ColumnInfo(defaultValue = "0")
    val duration: Int
    // Do I need this?
    /*@ColumnInfo(defaultValue = "0")
    val deadline: Long, */
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "currentUser",
    var name: String,
    var title: String,
    var job: String,
    var level: Int,
    var currentXp: Int,
    var xpToNextLevel: Int,
    var availablePoints: Int,
    var strength: Int,
    var agility: Int,
    var perception: Int,
    var vitality: Int,
    var intelligence: Int
)

