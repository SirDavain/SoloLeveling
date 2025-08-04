package com.example.sololevelingapplication.questlogscreen

import androidx.activity.result.launch
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.sololevelingapplication.QuestCategory
import com.example.sololevelingapplication.QuestDao
import com.example.sololevelingapplication.QuestEntity
import com.example.sololevelingapplication.questManagement.UiQuest
import com.example.sololevelingapplication.questManagement.toQuestEntity
import com.example.sololevelingapplication.questManagement.toUiQuest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.filterNot
import kotlin.collections.map
import kotlinx.coroutines.flow.*
import kotlin.collections.find

data class QuestUiState(
    val quests: List<UiQuest> = emptyList(), // Now holds UiQuest
    val isLoading: Boolean = false,
    val showAddQuestDialog: Boolean = false
)

@HiltViewModel
class QuestLogViewModel @Inject constructor(
    private val questDao: QuestDao
) : ViewModel() {

    /*private val _uiState = MutableStateFlow(QuestUiState())
    val uiState: StateFlow<QuestUiState> = _uiState.asStateFlow()

    val newQuestText = mutableStateOf("")
    val newQuestCategory = mutableStateOf(QuestCategory.ONE_TIME) // default category

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            questDao.getAllQuests() // This is a Flow
                .map { entities -> entities.map { it.toUiQuest() } } // Map Entity to UiQuest
                .distinctUntilChanged() // Only emit if the list content has changed
                .collect { questList ->
                    _uiState.update { it.copy(quests = questList, isLoading = false) }
                }
        }
    }

    fun onAddQuestClicked() {
        newQuestText.value = "" // Reset for new entry
        newQuestCategory.value = QuestCategory.ONE_TIME // Reset category
        _uiState.update { it.copy(showAddQuestDialog = true) }
    }

    fun onDismissAddQuestDialog() {
        _uiState.update { it.copy(showAddQuestDialog = false) }
    }

    fun onConfirmAddQuest() {
        if (newQuestText.value.isNotBlank()) {
            val newQuestEntity = QuestEntity( // Create QuestEntity
                text = newQuestText.value,
                category = newQuestCategory.value,
                xpCategory = XpCategoryPickerDialog() { }
            )
            viewModelScope.launch {
                questDao.insertQuest(newQuestEntity)
            }
            // UI will update automatically due to the Flow collection
            _uiState.update { it.copy(showAddQuestDialog = false) }
        }
    }

    fun onQuestDoneChanged(questId: String, isDone: Boolean) {
        viewModelScope.launch {
            val questToUpdate = _uiState.value.quests.find { it.id == questId }
            questToUpdate?.let {
                questDao.updateQuest(it.copy(isDone = isDone).toQuestEntity())
            }
        }
        // UI updates via Flow
    }

    fun onToggleEditQuest(questId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                quests = currentState.quests.map { quest ->
                    if (quest.id == questId) {
                        quest.copy(isBeingEdited = !quest.isBeingEdited)
                    } else {
                        // Ensure only one item can be edited at a time if desired
                        quest.copy(isBeingEdited = false)
                    }
                }
            )
        }
    }

    // Temporary text change in UI model
    fun onQuestTextChanged(questId: String, newText: String) {
        _uiState.update { currentState ->
            currentState.copy(
                quests = currentState.quests.map { uiQuest ->
                    if (uiQuest.id == questId && uiQuest.isBeingEdited) {
                        uiQuest.copy(text = newText)
                    } else {
                        uiQuest
                    }
                }
            )
        }
    }


    fun onSaveQuestEdit(questId: String) {
        viewModelScope.launch {
            val editedUiQuest = _uiState.value.quests.find { it.id == questId && it.isBeingEdited }
            editedUiQuest?.let {
                // Save the changes to the database
                questDao.updateQuest(it.toQuestEntity()) // `toQuestEntity` will have updated text
                // Also, update the UI state to turn off editing mode
                _uiState.update { currentState ->
                    currentState.copy(
                        quests = currentState.quests.map { uiQuest ->
                            if (uiQuest.id == questId) {
                                uiQuest.copy(isBeingEdited = false)
                            } else {
                                uiQuest
                            }
                        }
                    )
                }
            }
        }
    }

    fun onDeleteQuest(questId: String) {
        viewModelScope.launch {
            questDao.deleteQuestById(questId)
        }
        // UI updates via Flow
    }*/
}