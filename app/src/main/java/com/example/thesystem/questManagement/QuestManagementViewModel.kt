package com.example.thesystem.questManagement

import android.icu.util.Calendar
import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.thesystem.UserStatsDao
import com.example.thesystem.UserStatsEntity
import com.example.thesystem.statScreen.StatsScreenUiState
import com.example.thesystem.xpLogic.QuestCategory
import com.example.thesystem.xpLogic.calculateAbilityPointsForLevelUp
import com.example.thesystem.xpLogic.calculateXpForNextLevel
import com.example.thesystem.xpLogic.calculateXpForQuest
import kotlinx.coroutines.delay

data class UiQuest(
    var xp: Int,
    val id: String,
    var text: String,
    val duration: Int,
    val deadline: Long?,
    var isDone: Boolean,
    var hasFailed: Boolean,
    val timeOfCreation: Long,
    val category: QuestCategory,
    var isBeingEdited: Boolean = false
)

fun QuestEntity.toUiQuest(userLevel: Int): UiQuest {
    return UiQuest(
        xp = calculateXpForQuest(userLevel, category, duration),
        id = id,
        text = text,
        isDone = isDone,
        duration = duration,
        deadline = deadline,
        category = category,
        hasFailed = hasFailed,
        timeOfCreation = timeOfCreation
    )
}

fun UiQuest.toQuestEntity(): QuestEntity {
    return QuestEntity(
        xp = 0,
        id = id,
        text = text,
        isDone = isDone,
        duration = duration,
        deadline = deadline,
        category = category,
        hasFailed = hasFailed,
        timeOfCreation = timeOfCreation
    )
}


data class QuestUiState(
    val quests: List<UiQuest> = emptyList(),
    val isLoading: Boolean = false,
    val showAddQuestDialog: Boolean = false,
    val newQuestText: String = "",
    val newQuestCategory: QuestCategory = QuestCategory.ONE_TIME,
    val newQuestHours: Int = 0,
    val newQuestMinutes: Int = 0,
    val deadlineMillis: Long? = null,
    val showDeadlinePicker: Boolean = false
    // Any other quest-related UI state needed by screens using this VM
)

@HiltViewModel
class QuestManagementViewModel @Inject constructor(
    private val questDao: QuestDao,
    private val userStatsDao: UserStatsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestUiState())
    val uiState: StateFlow<QuestUiState> = _uiState.asStateFlow()

    private val _statsUiState = MutableStateFlow(StatsScreenUiState())
    val statsUiState: StateFlow<StatsScreenUiState> = _statsUiState.asStateFlow()

    var overlayCoordinator: OverlayCoordinator? = null

    init {
        loadQuests()
        //observeUserStatsForLevelUp()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            val currentStats = userStatsDao.getUserStats().first()
            if (currentStats == null)
                return@launch
            _uiState.update { it.copy(isLoading = true) }
            questDao.getAllQuests()
                .map { entities -> entities.map { it.toUiQuest(currentStats.level) } }
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
                newQuestMinutes = 0,
                deadlineMillis = null,
                showDeadlinePicker = false
            )
        }
    }

    fun onDeadlineSelected(millis: Long?) {
        _uiState.update { it.copy(deadlineMillis = millis, showDeadlinePicker = false) }
    }

    fun onShowDeadlinePicker() {
        _uiState.update { it.copy(showDeadlinePicker = true) }
    }

    fun onDismissDeadlinePicker() {
        _uiState.update { it.copy(showDeadlinePicker = false) }
    }

    fun onDismissAddQuestDialog() {
        _uiState.update { it.copy(showAddQuestDialog = false, showDeadlinePicker = false) }
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
        viewModelScope.launch {
            val currentUiState = _uiState.value
            val currentStats = userStatsDao.getUserStats().first()

            if (currentUiState.newQuestText.isBlank() || currentStats == null) {
                e("QuestManagementVM", "Cannot add quest. Text is blank or stats are missing.")
                return@launch
            }

            val currentTime = System.currentTimeMillis()
            val totalTimeInMinutes: Int =
                (currentUiState.newQuestHours * 60) + currentUiState.newQuestMinutes
            if (totalTimeInMinutes == 0)
                // TODO make "Quest Duration flash briefly
                return@launch

            val newQuestEntity = QuestEntity(
                text = currentUiState.newQuestText,
                category = currentUiState.newQuestCategory,
                duration = totalTimeInMinutes,
                xp = calculateXpForQuest(
                    currentStats.level,
                    currentUiState.newQuestCategory,
                    totalTimeInMinutes),
                deadline = currentUiState.deadlineMillis,
                timeOfCreation = currentTime
                // add deadline (using calendar composable) - only day, always midnight
                // dynamic countdown = deadline - currentTime
            )
            questDao.insertQuest(newQuestEntity)

            _uiState.update {
                it.copy(
                    showAddQuestDialog = false
                )
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val readableTime = sdf.format(Date(currentTime))
            Log.d("Adding quest", "Creation time is $currentTime, readable: $readableTime")

            Log.d("onConfirmAddQuest", "Quest Duration is: ${currentUiState.newQuestHours}h ${currentUiState.newQuestMinutes}m (In total: ${newQuestEntity.duration})")

            Log.d("onConfirmAddQuest", "Amount of XP to be gained is ${newQuestEntity.xp}")
        }
    }

    fun formatMillisToDateString(millis: Long?): String {
        if (millis == null) return "Not Set"
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
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

    fun onQuestSuccess(questId: String) {
        viewModelScope.launch {
            val questToComplete = _uiState.value.quests.find { it.id == questId }
            if (questToComplete == null) {
                e("QuestManagementVM", "onQuestSuccess: Quest with ID $questId not found.")
                return@launch
            }

            // Show QuestCompleted Overlay
            overlayCoordinator?.showOverlay(
                Overlay.QuestCompleted(
                    questText = questToComplete.text,
                    gainedXp = questToComplete.xp
                )
            )

            questDao.updateQuest(
                questToComplete.copy(isDone = true, hasFailed = false).toQuestEntity()
            )

            val currentStats = userStatsDao.getUserStats().first()
            if (currentStats == null) {
                e("QuestManagementVM", "Attempted to increase XP but stats aren't loaded.")
                return@launch
            }

            // Calculate New XP and Update the Database
            val statsWithNewXp = currentStats.copy(currentXp = currentStats.currentXp + questToComplete.xp)
            userStatsDao.updateUserStats(statsWithNewXp)
            Log.d("QuestManagementVM", "Completed quest. XP is now ${statsWithNewXp.currentXp}")

            Log.d("QuestManagementVM", "Delaying before level up check...")
            delay(3000L)

            val statsAfterQuestCompleted = userStatsDao.getUserStats().first()
            if (statsAfterQuestCompleted != null) {
                Log.d("QuestManagementVM", "Checking for level up with XP: ${statsAfterQuestCompleted.currentXp}")
                checkForLevelUp(statsAfterQuestCompleted)
            } else {
                Log.e("QuestManagementVM", "Stats became null after delay, cannot check for level up.")
            }
        }
    }

    private fun checkForLevelUp(stats: UserStatsEntity) {
        var currentStats = stats.copy()
        var hasLeveledUp = false

        while (currentStats.currentXp >= currentStats.xpToNextLevel) {
            hasLeveledUp = true

            val remainingXp = currentStats.currentXp - currentStats.xpToNextLevel
            val newLevel = currentStats.level + 1

            val newXpRequirement = calculateXpForNextLevel(newLevel)
            val gainedAbilityPoints = calculateAbilityPointsForLevelUp(newLevel)

            currentStats = currentStats.copy(
                level = newLevel,
                currentXp = remainingXp,
                xpToNextLevel = newXpRequirement,
                availablePoints = currentStats.availablePoints + gainedAbilityPoints
            )
            Log.d("QuestManagementVM", "LEVEL UP! New Level: $newLevel. Gained $gainedAbilityPoints points.")

            overlayCoordinator?.showOverlay(
                Overlay.LevelUp(
                    newLevel = newLevel,
                    abilityPoints = gainedAbilityPoints
                )
            )

            // Possibly add a delay in order for multiple level ups to occur back-to-back
        }

        if (hasLeveledUp) {
            viewModelScope.launch {
                userStatsDao.updateUserStats(currentStats)
            }
        }

    }

    @Composable
    fun CheckAndFailOverdueQuests() {
        val questsState by uiState.collectAsState()
        val currentQuests: List<UiQuest> = questsState.quests

        LaunchedEffect(Unit) {
            while (isActive) {
                Log.d("OverdueCheck", "Checking for overdue quests.")
                val currentTime = System.currentTimeMillis()
                currentQuests.filter { !it.isDone && !it.hasFailed }.forEach { quest ->
                    val durationInMillis = quest.duration * 60 * 1000L // getQuestDurationMillis(quest.category)
                    if (durationInMillis > 0 && (System.currentTimeMillis() - quest.timeOfCreation) > durationInMillis) {
                        val penalty = quest.xp / 2
                        val updatedQuest = quest.copy(hasFailed = true)
                        updateQuestInDB(updatedQuest.toQuestEntity())

                        overlayCoordinator?.showOverlay(
                            Overlay.QuestFailed(
                                questText = updatedQuest.text,
                                penaltyXp = penalty
                            )
                        )
                        // TODO: actually subtract the lost XP
                    }
                }
                delay(60000L)
            }
        }
    }

    private fun updateQuestInDB(questEntity: QuestEntity) {
        viewModelScope.launch {
            questDao.updateQuest(questEntity)
        }
    }
}

interface OverlayCoordinator {
    fun showOverlay(overlay: Overlay)
}