package com.example.sololevelingapplication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.sololevelingapplication.questManagement.UiQuest
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

/*data class FailedQuestInfo(
    val quest: UiQuest,
    val penaltyXp: Int
)*/

/*
@HiltViewModel
class AnimationViewModel @Inject constructor() : ViewModel() {
    private val _failedQuestToShow = mutableStateOf<FailedQuestInfo?>(null)
    val failedQuestToShow: State<FailedQuestInfo?> = _failedQuestToShow

    fun showQuestFailedOverlay(quest: UiQuest, penaltyXp: Int) {
        _failedQuestToShow.value = FailedQuestInfo(quest, penaltyXp)
    }

    fun dismissQuestFailedOverlay() {
        _failedQuestToShow.value = null
    }
} */