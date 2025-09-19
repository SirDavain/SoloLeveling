package com.example.thesystem.statScreen

import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesystem.Overlay
import com.example.thesystem.QuestDao
import com.example.thesystem.UserStatsDao
import com.example.thesystem.UserStatsEntity
import com.example.thesystem.questManagement.OverlayCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsScreenUiState(
    val stats: UserStatsEntity? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val userStatsDao: UserStatsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsScreenUiState())
    val uiState: StateFlow<StatsScreenUiState> = _uiState.asStateFlow()

    var overlayCoordinator: OverlayCoordinator? = null

    val levelProgress: Float
        get() {
            val stats = _uiState.value.stats
            return if (stats != null && stats.xpToNextLevel > 0) {
                (stats.currentXp.toFloat() / stats.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
        }

    init {
        //load initial data here
        loadCharacterStats()
    }

    private fun loadCharacterStats() {
        viewModelScope.launch {
            val statsExist = userStatsDao.getUserStats().first() != null
            if (!statsExist)
                userStatsDao.saveUserStats(createDefaultStats())

            userStatsDao.getUserStats().collect() { stats ->
                _uiState.update { currentState ->
                    currentState.copy(
                        stats = stats,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun createDefaultStats(): UserStatsEntity {
        return UserStatsEntity(
            level = 1,
            currentXp = 0,
            xpToNextLevel = 100,
            availablePoints = 5, // Start with some points
            strength = 10,
            agility = 10,
            vitality = 10,
            intelligence = 10,
            perception = 10,
            name = "Dave",
            title = "The One Who Overcame Adversity",
            job = "Coach"
        )
    }

    fun spendAbilityPoints(pointsToSpend: Int, chosenAbility: String) {
        val currentStats = _uiState.value.stats

        if (currentStats == null) {
            e("StatsViewModel", "Attempted to spend points but no character stats are loaded.")
            return
        }
        if (currentStats.availablePoints < pointsToSpend) {
            _uiState.update { it.copy(errorMessage = "Not enough available points!") }
            return
        }
        val updatedStats = when (chosenAbility.lowercase()) {
            "strength" -> currentStats.copy(
                strength = currentStats.strength + pointsToSpend,
                availablePoints = currentStats.availablePoints - pointsToSpend
            )
            "agility" -> currentStats.copy(
                agility = currentStats.agility + pointsToSpend,
                availablePoints = currentStats.availablePoints - pointsToSpend
            )
            "perception" -> currentStats.copy(
                perception = currentStats.perception + pointsToSpend,
                availablePoints = currentStats.availablePoints - pointsToSpend
            )
            "vitality" -> currentStats.copy(
                vitality = currentStats.vitality + pointsToSpend,
                availablePoints = currentStats.availablePoints - pointsToSpend
            )
            "intelligence" -> currentStats.copy(
                intelligence = currentStats.intelligence + pointsToSpend,
                availablePoints = currentStats.availablePoints - pointsToSpend
            )
            else -> {
                e("StatsViewModel", "Unknown ability provided: $chosenAbility")
                currentStats
            }
        }
        viewModelScope.launch {
            userStatsDao.updateUserStats(updatedStats)
        }
    }

    fun simulateQuestCompleted() {
        val testQuestText = "Fake a completed quest"
        val testGainedXp = 75

        if (overlayCoordinator == null) {
            Log.e("StatsViewModel", "CRITICAL ERROR: overlayCoordinator is NULL in simulateQuestCompleted!")
        } else {
            Log.d("StatsViewModel", "overlayCoordinator is NOT NULL. Calling showOverlay.")
            overlayCoordinator?.showOverlay( // Use safe call just in case, though the check above should cover it
                Overlay.QuestCompleted(
                    questText = testQuestText,
                    gainedXp = testGainedXp
                )
            )
        }

        overlayCoordinator?.showOverlay(
            Overlay.QuestCompleted(
                questText = testQuestText,
                gainedXp = testGainedXp
            )
        )
        Log.d("StatsViewModel", "Simulating Quest Completed Overlay for: '$testQuestText'")
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun addAbilityPoints() {
        val currentStats = _uiState.value.stats

        if (currentStats == null) {
            e("StatsViewModel", "Attempted to spend points but no character stats are loaded.")
            return
        }

        val updatedStats = currentStats.copy(
            availablePoints = currentStats.availablePoints + 1
        )
        Log.d("StatsViewModel", "Current Ability Points: ${currentStats.availablePoints}\nNew Ability Points: ${updatedStats.availablePoints}")
        viewModelScope.launch {
            userStatsDao.updateUserStats(updatedStats)
        }
    }
}