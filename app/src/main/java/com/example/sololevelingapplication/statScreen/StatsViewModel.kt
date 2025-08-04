package com.example.sololevelingapplication.statScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class StatsScreenUiState(
    val strength: Int = 10,
    val agility: Int = 10,
    val intelligence: Int = 10,
    val perception: Int = 10,
    val vitality: Int = 10,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    //params
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsScreenUiState())
    val uiState: StateFlow<StatsScreenUiState> = _uiState

    // --- Character Info ---
    private val _characterName = mutableStateOf("David") // Initial default value
    val characterName: State<String> = _characterName

    private val _currentXp = mutableStateOf(55)
    val currentXp: State<Int> = _currentXp

    private val _xpForLevelingUp = mutableStateOf(100)
    val xpForLevelingUp: State<Int> = _xpForLevelingUp

    val levelProgress: Float
        get() {
            return if (_xpForLevelingUp.value > 0) {
                (_currentXp.value.toFloat() / _xpForLevelingUp.value.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
        }

    private val _characterJob = mutableStateOf("Personal Trainer")
    val characterJob: State<String> = _characterJob

    private val _characterLevel = mutableStateOf(100)
    val characterLevel: State<Int> = _characterLevel

    private val _characterTitle = mutableStateOf("The One Who Overcame Adversity")
    val characterTitle: State<String> = _characterTitle

    // --- Core Stats ---
    private val _strength = mutableStateOf(555)
    val strength: State<Int> = _strength

    private val _agility = mutableStateOf(555)
    val agility: State<Int> = _agility

    private val _perception = mutableStateOf(555)
    val perception: State<Int> = _perception

    private val _vitality = mutableStateOf(555)
    val vitality: State<Int> = _vitality

    private val _intelligence = mutableStateOf(555)
    val intelligence: State<Int> = _intelligence

    // --- Ability Points ---
    private val _availablePoints = mutableStateOf(5)
    val availablePoints: State<Int> = _availablePoints

    // --- Other potential states ---
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        //load initial data here
    }

    // Example function to load data (if it's not hardcoded)
    private fun loadCharacterStats() {
        _isLoading.value = true
        // viewModelScope.launch {
        //     try {
        //         val stats = characterRepository.getCharacterStats() // Assuming repository call
        //         _characterName.value = stats.name
        //         _characterJob.value = stats.job
        //         _characterLevel.value = stats.level
        //         // ... update other states
        //         _errorMessage.value = null
        //     } catch (e: Exception) {
        //         _errorMessage.value = "Failed to load stats: ${e.message}"
        //     } finally {
        //         _isLoading.value = false
        //     }
        // }
    }

    // Example function to update a stat (e.g., if user can spend points)
    fun spendPointOnStrength(pointsToSpend: Int = 1) {
        if (_availablePoints.value >= pointsToSpend) {
            _strength.value += pointsToSpend
            _availablePoints.value -= pointsToSpend
        } else {
            // Handle error: not enough points
            _errorMessage.value = "Not enough available points!"
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}