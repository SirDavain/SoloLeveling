package com.example.sololevelingapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.sololevelingapplication.animations.EdgeLightState
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

    fun show(overlay: Overlay) {
        _currentOverlay.value = overlay
    }
    fun dismiss() {
        _currentOverlay.value = Overlay.None
    }

    fun triggerEdgeLighting() {
        viewModelScope.launch {
            _edgeLightNotificationState.value = EdgeLightState.Pulsing
            delay(3000L)
            _edgeLightNotificationState.value = EdgeLightState.Idle
        }
    }
}