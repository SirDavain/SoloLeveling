package com.example.thesystem.questManagement

import android.R.attr.duration
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesystem.OverlayViewModel
import com.example.thesystem.QuestDao
import com.example.thesystem.QuestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import jakarta.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.thesystem.Overlay
import kotlinx.coroutines.isActive
import androidx.compose.runtime.getValue
import com.example.thesystem.xpLogic.QuestCategory
import com.example.thesystem.xpLogic.calculateXpForQuest
import kotlinx.coroutines.delay

data class UiQuest(
    val id: String,
    var text: String,
    var isDone: Boolean,
    val category: QuestCategory,
    var hasFailed: Boolean,
    var xp: Int,
    var isBeingEdited: Boolean = false,
    val timeOfCreation: Long,
    val duration: Int
)

fun QuestEntity.toUiQuest(): UiQuest {
    return UiQuest(
        xp = 0,
        id = id,
        text = text,
        isDone = isDone,
        duration = duration,
        category = category,
        hasFailed = hasFailed,
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
        timeOfCreation = timeOfCreation,
        duration = duration
    )
}


data class QuestManagementUiState(
    val quests: List<UiQuest> = emptyList(),
    val isLoading: Boolean = false,
    val showAddQuestDialog: Boolean = false,
    val newQuestText: String = "",
    val newQuestCategory: QuestCategory = QuestCategory.ONE_TIME,
    val newQuestHours: Int = 0,
    val newQuestMinutes: Int = 0,
    // Any other quest-related UI state needed by screens using this VM
)

@HiltViewModel
class QuestManagementViewModel @Inject constructor(
    private val questDao: QuestDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestManagementUiState())
    val uiState: StateFlow<QuestManagementUiState> = _uiState.asStateFlow()

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
    fun onAddQuestClicked() {
        if (_uiState.value.showAddQuestDialog) return // Avoid re-opening if already open
        Log.d("QuestLogScreenFAB", "onAddQuestClicked")

        _uiState.update {
            it.copy(
                showAddQuestDialog = true,
                newQuestText = "",
                newQuestCategory = QuestCategory.ONE_TIME,
                newQuestHours = 0,
                newQuestMinutes = 0
                )
        }
    }

    fun onDismissAddQuestDialog() {
        _uiState.update { it.copy(showAddQuestDialog = false) }
    }

    fun onNewQuestCategoryChanged(newCategory: QuestCategory) {
        _uiState.update { it.copy(newQuestCategory = newCategory) }
    }

    fun onNewQuestTextChanged(newText: String) {
        _uiState.update { it.copy(newQuestText = newText) }
    }

    fun onNewQuestHoursChanged(hours: Int) {
        _uiState.update { it.copy(newQuestHours = hours) }
    }

    fun onNewQuestMinutesChanged(minutes: Int) {
        _uiState.update { it.copy(newQuestMinutes = minutes) }
    }

    fun onConfirmAddQuest() {
        Log.d("QuestManagementVM", "onConfirmAddQuest called")
        val currentUiState = _uiState.value
        if (currentUiState.newQuestText.isBlank()) return

        val currentTime = System.currentTimeMillis()

        val totalTimeInMinutes: Int =
            if (currentUiState.newQuestHours == 0)
                currentUiState.newQuestMinutes
            else
                (currentUiState.newQuestHours * 60) + currentUiState.newQuestMinutes
        if (totalTimeInMinutes == 0) {
            // make "Quest Duration" flash briefly
            return
        }

        val newQuestEntity = QuestEntity(
            text = currentUiState.newQuestText,
            category = currentUiState.newQuestCategory,
            duration = totalTimeInMinutes,
            xp = calculateXpForQuest(
                currentUiState.newQuestCategory,
                totalTimeInMinutes),
            timeOfCreation = currentTime
            // add deadline (using calendar composable) - only day, always midnight
            // dynamic countdown = deadline - currentTime
        )
        viewModelScope.launch {
            questDao.insertQuest(newQuestEntity)
        }
        _uiState.update {
            it.copy(
                showAddQuestDialog = false
                //quests = it.quests + newQuestEntity,
            )
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val readableTime = sdf.format(Date(currentTime))
        Log.d("Adding quest", "Creation time is $currentTime, readable: $readableTime")

        Log.d("onConfirmAddQuest", "Quest Duration is: ${currentUiState.newQuestHours}h ${currentUiState.newQuestMinutes}m (In total: ${newQuestEntity.duration})")

        Log.d("onConfirmAddQuest", "Amount of XP to be gained is ${newQuestEntity.xp}")
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