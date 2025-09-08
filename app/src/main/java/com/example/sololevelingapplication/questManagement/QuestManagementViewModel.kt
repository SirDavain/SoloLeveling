package com.example.sololevelingapplication.questManagement

import android.R.attr.category
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sololevelingapplication.OverlayViewModel
import com.example.sololevelingapplication.QuestDao
import com.example.sololevelingapplication.QuestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import jakarta.inject.Inject
import kotlin.text.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.sololevelingapplication.Overlay
import kotlinx.coroutines.isActive
import androidx.compose.runtime.getValue
import com.example.sololevelingapplication.xpLogic.QuestCategory
import kotlinx.coroutines.delay

data class UiQuest(
    val id: String,
    var text: String,
    var isDone: Boolean,
    val category: QuestCategory,
    var hasFailed: Boolean,
    var xp: Int,
    var isBeingEdited: Boolean = false,
    val timeOfCreation: Long
)

fun QuestEntity.toUiQuest(): UiQuest {
    return UiQuest(
        id = id,
        text = text,
        isDone = isDone,
        category = category,
        hasFailed = hasFailed,
        xp = 0,
        timeOfCreation = timeOfCreation
    )
}

fun UiQuest.toQuestEntity(): QuestEntity {
    return QuestEntity(
        id = id,
        text = text,
        isDone = isDone,
        category = category,
        hasFailed = hasFailed,
        xp = 0,
        timeOfCreation = timeOfCreation
    )
}


data class QuestManagementUiState(
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

    // This is the function the FAB in MainPagerScreen will call
    fun onAddQuestClicked() { // Renamed for clarity from MainPagerScreen's perspective
        if (_uiState.value.showAddQuestDialog) return // Avoid re-opening if already open

        newQuestText.value = ""
        newQuestCategory.value = QuestCategory.ONE_TIME
        _uiState.update { it.copy(showAddQuestDialog = true) }
    }

    fun onDismissAddQuestDialog() {
        _uiState.update { it.copy(showAddQuestDialog = false) }
    }

    fun onNewQuestCategoryChanged(questCategory: QuestCategory) {
        newQuestCategory.value = questCategory
    }

    fun onConfirmAddQuest() {
        Log.d("QuestManagementVM", "onConfirmAddQuest called")
        val currentTime = System.currentTimeMillis()

        if (newQuestText.value.isNotBlank()) {
            val newQuestEntity = QuestEntity(
                text = newQuestText.value,
                category = newQuestCategory.value,
                xp = 0,
                timeOfCreation = currentTime
            )
            viewModelScope.launch {
                questDao.insertQuest(newQuestEntity)
            }
            // UI will update automatically due to the Flow collection
            _uiState.update { it.copy(showAddQuestDialog = false) } // Close dialog

            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val readableTime = sdf.format(Date(currentTime))
            Log.d("Adding quest", "Creation time is $currentTime, readable: $readableTime")
        } else { // <-- Add this else block for debugging
            Log.d("Adding quest", "Quest text is blank, not adding.")
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

    val selectedQuestCategoryForDialog = mutableStateOf<QuestCategory>(QuestCategory.ONE_TIME)

    fun updateSelectedQuestCategory(newCategory: QuestCategory) {
        selectedQuestCategoryForDialog.value = newCategory
    }

    /*fun completeQuest(quest: UiQuest) {
        viewModelScope.launch {
            val rewards = 42
            questDao.updateQuest(quest.copy(isDone = true, hasFailed = false).toQuestEntity())

            overlayCoordinator.showQuestSuccessOverlay(
                QuestSuccessOverlayInfo(questText = quest.text, receivedXp = rewards)
            )
        }
    } */

    @Composable
    fun CheckAndFailOverdueQuests(
        overlayViewModel: OverlayViewModel
    ) {
        val questsState by uiState.collectAsState()
        val currentQuests: List<UiQuest> = questsState.quests

        LaunchedEffect(Unit) {
            while (isActive) {
                Log.d("OverDueCheck", "Checking for overdue quests.")
                val currentTime = System.currentTimeMillis()
                currentQuests.filter { !it.isDone && !it.hasFailed }.forEach { quest ->
                    // need to update duration
                    val duration = 1 // getQuestDurationMillis(quest.category)
                    if (duration > 0 && (currentTime - quest.timeOfCreation) > duration) {
                        val penalty = quest.xp / 2
                        val updatedQuest = quest.copy(hasFailed = true)
                        updateQuestInDB(updatedQuest.toQuestEntity())

                        overlayViewModel.show(Overlay.QuestFailed(updatedQuest.text, penalty))
                        // actually subtract the lost XP
                    }
                }
                delay(60000L)
            }
        }
    }

    private fun updateQuestInDB(questEntity: QuestEntity) {
        viewModelScope.launch {
            questDao.insertQuest(questEntity)
        }
    }

    /*private fun getQuestDurationMillis(category: QuestCategory): Long {
        return when (category) {
            QuestCategory.DAILY -> java.util.concurrent.TimeUnit.HOURS.toMillis(24)
            QuestCategory.ONE_TIME -> {
                when (xpCategory) { // Example: Duration based on XP for ONE_TIME quests
                    XpCategory.HALF_HOUR_OR_LESS -> java.util.concurrent.TimeUnit.MINUTES.toMillis(30)
                    XpCategory.HOUR_OR_LESS -> java.util.concurrent.TimeUnit.HOURS.toMillis(1)
                    XpCategory.TWO_HOURS_OR_LESS -> java.util.concurrent.TimeUnit.HOURS.toMillis(2)
                    XpCategory.THREE_HOURS_OR_LESS -> java.util.concurrent.TimeUnit.HOURS.toMillis(3)
                    XpCategory.FOUR_HOURS_OR_LESS -> java.util.concurrent.TimeUnit.HOURS.toMillis(4)
                    else -> 0L // Or some default
                }
            }
            else -> 0L // No duration for REPEATABLE or if undefined
        }
    }*/
    //deprecated: penalty will just be half the xp to be gained
    /* private fun determinePenaltyFor(quest: UiQuest): Int {
        return when (quest.xpCategory) {
            XpCategory.HALF_HOUR_OR_LESS -> 10
            XpCategory.HOUR_OR_LESS -> 25
            XpCategory.TWO_HOURS_OR_LESS -> 50
            XpCategory.THREE_HOURS_OR_LESS -> 100
            else -> 150
        }
    } */
}