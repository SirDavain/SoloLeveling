package com.example.sololevelingapplication.questManagement

import android.R.attr.category
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sololevelingapplication.QuestCategory
import com.example.sololevelingapplication.QuestDao
import com.example.sololevelingapplication.QuestEntity
import com.example.sololevelingapplication.XpCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class UiQuest(
    val id: String,
    var text: String,
    var isDone: Boolean,
    val category: QuestCategory,
    val xpCategory: XpCategory?,
    var isBeingEdited: Boolean = false,
    val timeOfCreation: Long
)

fun QuestEntity.toUiQuest(): UiQuest {
    return UiQuest(
        id = id,
        text = text,
        isDone = isDone,
        category = category,
        xpCategory = this.xpCategory,
        //isBeingEdited = false
        timeOfCreation = timeOfCreation
    )
}

fun UiQuest.toQuestEntity(): QuestEntity {
    return QuestEntity(
        id = id,
        text = text,
        isDone = isDone,
        category = category,
        xpCategory = this.xpCategory,
        timeOfCreation = timeOfCreation
    )
}


data class QuestManagementUiState( // Renamed from QuestLogUiState for clarity
    val quests: List<UiQuest> = emptyList(),
    val isLoading: Boolean = false,
    val showAddQuestDialog: Boolean = false
    // Any other quest-related UI state needed by screens using this VM
)

@HiltViewModel
class QuestManagementViewModel @Inject constructor(
    private val questDao: QuestDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestManagementUiState())
    val uiState: StateFlow<QuestManagementUiState> = _uiState.asStateFlow()

    // For the add quest dialog
    val newQuestText = mutableStateOf("")
    val newQuestCategory = mutableStateOf(QuestCategory.ONE_TIME) // Default category
    val newQuestXpCategory = mutableStateOf<XpCategory>(XpCategory.HALF_HOUR_OR_LESS) // Default category

    init {
        loadQuests()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            questDao.getAllQuests()
                .map { entities -> entities.map { it.toUiQuest() } }
                .distinctUntilChanged()
                .collect { questList ->
                    _uiState.update { it.copy(quests = questList, isLoading = false) }
                }
        }
    }

    // This is the function your FAB in MainPagerScreen will call
    fun onAddQuestClicked() { // Renamed for clarity from MainPagerScreen's perspective
        if (_uiState.value.showAddQuestDialog) return // Avoid re-opening if already open

        newQuestText.value = ""
        newQuestCategory.value = QuestCategory.ONE_TIME
        _uiState.update { it.copy(showAddQuestDialog = true) }
    }

    fun onDismissAddQuestDialog() {
        _uiState.update { it.copy(showAddQuestDialog = false) }
    }

    fun onNewQuestXpCategoryChanged(xpCategory: XpCategory) {
        newQuestXpCategory.value = xpCategory
    }

    fun onConfirmAddQuest() {
        val currentTime = System.currentTimeMillis()

        if (newQuestText.value.isNotBlank()) {
            val newQuestEntity = QuestEntity(
                text = newQuestText.value,
                category = newQuestCategory.value,
                xpCategory = newQuestXpCategory.value,
                timeOfCreation = currentTime
            )
            viewModelScope.launch {
                questDao.insertQuest(newQuestEntity)
            }
            // UI will update automatically due to the Flow collection
            _uiState.update { it.copy(showAddQuestDialog = false) } // Close dialog
            Log.d("Adding quest", "Creation time is $currentTime")
        }
    }

    fun onQuestDoneChanged(questId: String, isDone: Boolean) {
        viewModelScope.launch {
            val questToUpdate = _uiState.value.quests.find { it.id == questId }
            questToUpdate?.let {
                questDao.updateQuest(it.copy(isDone = isDone).toQuestEntity())
            }
        }
    }

    fun onToggleEditQuest(questId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                quests = currentState.quests.map { quest ->
                    if (quest.id == questId) {
                        quest.copy(isBeingEdited = !quest.isBeingEdited)
                    } else {
                        quest.copy(isBeingEdited = false) // Only one editable at a time
                    }
                }
            )
        }
    }

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
                questDao.updateQuest(it.toQuestEntity())
                _uiState.update { currentState -> // Turn off editing mode in UI
                    currentState.copy(
                        quests = currentState.quests.map { uiQ ->
                            if (uiQ.id == questId) uiQ.copy(isBeingEdited = false) else uiQ
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
    }

    val selectedXpCategoryForDialog = mutableStateOf<XpCategory>(XpCategory.HALF_HOUR_OR_LESS)

    fun updateSelectedXpCategory(newCategory: XpCategory) {
        selectedXpCategoryForDialog.value = newCategory
    }
}
