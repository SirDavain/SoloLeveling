package com.example.sololevelingapplication.questlogscreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class QuestLogViewModel @Inject constructor(
    //params
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private fun loadQuests() {
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
}