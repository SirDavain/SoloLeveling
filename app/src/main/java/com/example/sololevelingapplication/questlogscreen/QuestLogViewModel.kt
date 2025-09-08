package com.example.sololevelingapplication.questlogscreen

import androidx.activity.result.launch
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
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

}