package com.example.sololevelingapplication

import androidx.compose.runtime.Composable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

enum class XpCategory {
    HALF_HOUR_OR_LESS,
    HOUR_OR_LESS,
    TWO_HOURS_OR_LESS,
    THREE_HOURS_OR_LESS,
    FOUR_HOURS_OR_LESS,
    MORE_THAN_FOUR_HOURS
}

/*sealed class XpCategory {
    abstract val displayName: String
    abstract val xpAmount: Int

    data object HalfHourOrLess : XpCategory() {
        override val displayName: String = "≤ 30 minutes"
        override val xpAmount: Int = 21
    }
    data object HourOrLess : XpCategory() {
        override val displayName: String = "≤ 1 hour"
        override val xpAmount: Int = 42
    }
    data object TwoHoursOrLess : XpCategory() {
        override val displayName: String = "≤ 2 hours"
        override val xpAmount: Int = 63
    }
    data object ThreeHoursOrLess : XpCategory() {
        override val displayName: String = "≤ 3 hours"
        override val xpAmount: Int = 84
    }
    data object FourHoursOrLess : XpCategory() {
        override val displayName: String = "≤ 4 hours"
        override val xpAmount: Int = 95
    }
    data object MoreThanFourHours : XpCategory() {
        override val displayName: String = "≥ 4 hours"
        override val xpAmount: Int = 121
    }
    companion object {
        val entries: List<XpCategory> = listOf(
            HalfHourOrLess,
            HourOrLess,
            TwoHoursOrLess,
            ThreeHoursOrLess,
            FourHoursOrLess,
            MoreThanFourHours
        )
    }
}*/

@Composable
fun XpCategoryPickerDialog(
    selectedTimeFrame: XpCategory,
    onXpCategoryChange: (XpCategory) -> Unit,
    onDismiss: () -> Unit
) {

}

/*fun getXpInfo(timeFrame: XpCategory): String {
    return "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
}*/

/*fun getXpInfo(timeFrame: XpCategory): String {
    return when (timeFrame) {
        is XpCategory.HalfHourOrLess -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
        is XpCategory.HourOrLess -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
        is XpCategory.TwoHoursOrLess -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
        is XpCategory.ThreeHoursOrLess -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
        is XpCategory.FourHoursOrLess -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
        is XpCategory.MoreThanFourHours -> "Accomplishing a quest that is ${timeFrame.displayName} long gives you ${timeFrame.xpAmount} XP"
    }
}*/

enum class QuestCategory {
    ONE_TIME,
    DAILY,
    WEEKLY,
    BOSS_BATTLE
}
