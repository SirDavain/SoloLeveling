package com.example.sololevelingapplication.questlogscreen

import org.junit.Assert
import org.junit.Test

interface AppLogger {
    fun d(tag: String, message: String)
}

class XpCategoryLoggerTest {
    @Test
    fun getAllXpCategoryNames_returnsCorrectNames() {
        val expectedNames = listOf("ONE_TIME", "DAILY", "WEEKLY", "BOSS")
        val actualNames = getAllQuestCategoryNames()
        Assert.assertEquals(expectedNames, actualNames)
    }

    @Test
    fun getAllXpCategoryDisplayNames_returnsCorrectDisplayNames() {
        val expectedDisplayNames = listOf("One-time Quest", "Daily Quest", "Weekly Quest", "Boss Battle")
        val actualDisplayNames = getAllQuestCategoryDisplayNames()
        Assert.assertEquals(expectedDisplayNames, actualDisplayNames)
    }
}