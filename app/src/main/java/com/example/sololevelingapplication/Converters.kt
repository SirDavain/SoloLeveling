package com.example.sololevelingapplication

import androidx.room.TypeConverter
import com.example.sololevelingapplication.xpLogic.QuestCategory

class Converters {
    /*@TypeConverter
    fun fromXpCategory(category: XpCategory?): String? {
        return when (category) {
            is XpCategory.HalfHourOrLess -> "HALF_HOUR"
            is XpCategory.HourOrLess -> "ONE_HOUR"
            is XpCategory.TwoHoursOrLess -> "TWO_HOURS"
            is XpCategory.ThreeHoursOrLess -> "THREE_HOURS"
            is XpCategory.FourHoursOrLess -> "FOUR_HOURS"
            is XpCategory.MoreThanFourHours -> "FOUR_HOURS_PLUS"
            null -> null
        }
    }

    @TypeConverter
    fun toXpCategory(value: String?): XpCategory? {
        return when (value) {
            "HALF_HOUR" -> XpCategory.HalfHourOrLess
            "ONE_HOUR" -> XpCategory.HourOrLess
            "TWO_HOURS" -> XpCategory.TwoHoursOrLess
            "THREE_HOURS" -> XpCategory.ThreeHoursOrLess
            "FOUR_HOURS" -> XpCategory.FourHoursOrLess
            "FOUR_HOURS_PLUS" -> XpCategory.MoreThanFourHours
            else -> null
        }
    }*/

    /*@TypeConverter
    fun fromXpCategory(xpCategory: XpCategory): String {
        return xpCategory.name
    }

    @TypeConverter
    fun toXpCategory(name: String?): XpCategory? {
        return name?.let {
            try {
                XpCategory.valueOf(it)
            } catch (e: IllegalArgumentException) {
                // Handle cases where the stored string doesn't match an enum constant
                // (e.g., if you changed enum names later)
                null
            }
        }
    }*/

    @TypeConverter
    fun fromQuestCategory(questCategory: QuestCategory?): String? {
        return questCategory?.name
    }

    @TypeConverter
    fun toQuestCategory(name: String?): QuestCategory? {
        return name?.let {
            try {
                QuestCategory.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}