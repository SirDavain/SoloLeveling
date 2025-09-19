package com.example.thesystem.xpLogic

import com.example.thesystem.UserStatsEntity
import kotlin.math.pow

enum class QuestCategory(val category: String) {
    ONE_TIME("One-time Quest"),
    DAILY("Daily Quest"),
    WEEKLY("Weekly Quest"),
    BOSS("Boss Battle")
}

fun calculateXpForQuest(level: Int, category: QuestCategory, totalTimeInMinutes: Int): Int {
    val baseQuestXp = when (category) {
        QuestCategory.ONE_TIME -> totalTimeInMinutes * 2
        QuestCategory.DAILY -> totalTimeInMinutes * 4
        QuestCategory.WEEKLY -> totalTimeInMinutes * 6
        QuestCategory.BOSS -> totalTimeInMinutes * 10
    }

    val levelBonus = (level - 1) * 5
    return baseQuestXp + levelBonus
}

fun calculateXpForNextLevel(level: Int): Int {
    val baseXp = 100.0
    val exponent = 1.5

    // Formula: base_xp * (level ^ exponent)
    return (baseXp * level.toDouble().pow(exponent)).toInt()
}

fun calculateAbilityPointsForLevelUp(level: Int): Int {
    return if (level % 25 == 0)
        10
    else if (level % 10 == 0)
        5
    else if (level % 5 == 0)
        3
    else if (level % 2 == 0)
        2
    else
        1
}

// XP gain/loss depends on importance/length of a quest
// - Different categories
// - No hardcoding, give the user more flexibility when choosing the duration of their quest
// - XP gain/loss scales automatically with the quest's length (exponential gain/loss)
// - Any longer than 12 hours and it's a Boss Battle
// - Show the calculated XP amount to be gained