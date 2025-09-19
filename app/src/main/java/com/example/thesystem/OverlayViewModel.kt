package com.example.thesystem

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.thesystem.animations.EdgeLightState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

sealed class Overlay {
    data object None : Overlay()
    data class LevelUp(
        val newLevel: Int,
        val abilityPoints: Int?
    ) : Overlay()
    data class QuestCompleted(
        val questText: String,
        val gainedXp: Int
    ) : Overlay()
    data class QuestFailed(
        val questText: String,
        val penaltyXp: Int
    ) : Overlay()
}

@HiltViewModel
class OverlayViewModel @Inject constructor() : ViewModel() {
    private val _currentOverlay = mutableStateOf<Overlay>(Overlay.None)
    val currentOverlay: State<Overlay> = _currentOverlay

    private val _edgeLightNotificationState = MutableStateFlow(EdgeLightState.Idle)
    val edgeLightNotificationState = _edgeLightNotificationState.asStateFlow()

    suspend fun show(overlay: Overlay) {
        if (_currentOverlay.value !is Overlay.None) {
            _currentOverlay.value = Overlay.None
            delay(1)
        }
        _currentOverlay.value = overlay
    }
    fun dismiss() {
        _currentOverlay.value = Overlay.None
    }

    fun triggerEdgeLighting() {
        viewModelScope.launch {
            _edgeLightNotificationState.value = EdgeLightState.Pulsing
            delay(2000L)
            _edgeLightNotificationState.value = EdgeLightState.Idle
        }
    }
}