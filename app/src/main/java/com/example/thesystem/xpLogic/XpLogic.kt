package com.example.thesystem.xpLogic

enum class QuestCategory(val category: String) {
    ONE_TIME("One-time Quest"),
    DAILY("Daily Quest"),
    WEEKLY("Weekly Quest"),
    BOSS("Boss Battle")
}

fun calculateXpForQuest(category: QuestCategory, totalTimeInMinutes: Int): Int {
    return when (category) {
        QuestCategory.ONE_TIME -> totalTimeInMinutes * 10
        QuestCategory.DAILY -> totalTimeInMinutes * 15
        QuestCategory.WEEKLY -> totalTimeInMinutes * 20
        QuestCategory.BOSS -> totalTimeInMinutes * 30
    }
}

// XP gain/loss depends on importance/length of a quest
// - Different categories
// - No hardcoding, give the user more flexibility when choosing the duration of their quest
// - XP gain/loss scales automatically with the quest's length (exponential gain/loss)
// - Start with 21 XP for <= half an hour and calculate based on how long it takes (up to 10h?)
// - Any longer than that and it's a Boss Battle
// - Show the calculated XP amount to be gained