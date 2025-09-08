package com.example.sololevelingapplication.xpLogic

enum class QuestCategory(val category: String) {
    ONE_TIME("One-time Quest"),
    DAILY("Daily Quest"),
    WEEKLY("Weekly Quest"),
    BOSS("Boss Battle")
}

// XP gain/loss depends on importance/length of a quest
// - Different categories
// - No hardcoding, give the user more flexibility when choosing the duration of their quest
// - XP gain/loss scales automatically with the quest's length (exponential gain/loss)
// - Start with 21 XP for <= half an hour and calculate based on how long it takes (up to 10h?)
// - Any longer than that and it's a Boss Battle
// - Show the calculated XP amount to be gained