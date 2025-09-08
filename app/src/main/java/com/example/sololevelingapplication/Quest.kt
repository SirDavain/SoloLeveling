package com.example.sololevelingapplication

import androidx.compose.runtime.Composable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

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
