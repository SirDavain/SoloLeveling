package com.example.thesystem.questlogscreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import com.example.thesystem.QuestDao
import com.example.thesystem.questManagement.UiQuest

data class QuestUiState(
    val quests: List<UiQuest> = emptyList(), // Now holds UiQuest
    val isLoading: Boolean = false,
    val showAddQuestDialog: Boolean = false
)

@HiltViewModel
class QuestLogViewModel @Inject constructor(
    private val questDao: QuestDao
) : ViewModel() {

}