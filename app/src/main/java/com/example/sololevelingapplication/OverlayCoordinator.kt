package com.example.sololevelingapplication

import jakarta.inject.Singleton
import kotlinx.coroutines.flow.StateFlow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


/*data class QuestFailedOverlayInfo(
    val questText: String,
    val penaltyXp: Int
)

data class QuestCompletedOverlayInfo(
    val questText: String,
    val receivedXp: Int
)

data class LevelUpOverlayInfo(
    val newLevel: Int,
    val abilityPoint: Int = 1, // receive one point per level-up?
    val statIncreases: String? = null // optional increase of a (random?) stat
)*/

/*interface OverlayCoordinator {
    // Quest failed
    val questFailedOverlayInfo: StateFlow<QuestFailedOverlayInfo?>
    fun showQuestFailedOverlay(info: QuestFailedOverlayInfo)
    fun dismissQuestFailedOverlay()

    // Quest Success
    val questCompletedOverlayInfo: StateFlow<QuestCompletedOverlayInfo?>
    fun showQuestSuccessOverlay(info: QuestSuccessOverlayInfo)
    fun dismissQuestSuccessOverlay()

    // Level Up
    val levelUpOverlayInfo: StateFlow<LevelUpOverlayInfo?>
    fun showLevelUpOverlay(info: LevelUpOverlayInfo)
    fun dismissLevelUpOverlay()
}*/

/*
@Singleton
class DefaultOverlayCoordinator @Inject constructor() : OverlayCoordinator {

    // --- Quest Failed State ---
    private val _questFailedOverlayInfo = MutableStateFlow<QuestFailedOverlayInfo?>(null)
    override val questFailedOverlayInfo: StateFlow<QuestFailedOverlayInfo?> = _questFailedOverlayInfo.asStateFlow()

    override fun showQuestFailedOverlay(info: QuestFailedOverlayInfo) {
        // Optional: Ensure only one overlay is shown at a time by dismissing others
        dismissAllOverlays()
        _questFailedOverlayInfo.value = info
    }

    override fun dismissQuestFailedOverlay() {
        _questFailedOverlayInfo.value = null
    }

    // --- Quest Success State ---
    private val _questSuccessOverlayInfo = MutableStateFlow<QuestSuccessOverlayInfo?>(null)
    override val questSuccessOverlayInfo: StateFlow<QuestSuccessOverlayInfo?> = _questSuccessOverlayInfo.asStateFlow()

    override fun showQuestSuccessOverlay(info: QuestSuccessOverlayInfo) {
        dismissAllOverlays()
        _questSuccessOverlayInfo.value = info
    }

    override fun dismissQuestSuccessOverlay() {
        _questSuccessOverlayInfo.value = null
    }

    // --- Level Up State ---
    private val _levelUpOverlayInfo = MutableStateFlow<LevelUpOverlayInfo?>(null)
    override val levelUpOverlayInfo: StateFlow<LevelUpOverlayInfo?> = _levelUpOverlayInfo.asStateFlow()

    override fun showLevelUpOverlay(info: LevelUpOverlayInfo) {
        dismissAllOverlays()
        _levelUpOverlayInfo.value = info
    }

    override fun dismissLevelUpOverlay() {
        _levelUpOverlayInfo.value = null
    }

    // Helper to ensure only one major overlay is active at a time
    private fun dismissAllOverlays() {
        _questFailedOverlayInfo.value = null
        _questSuccessOverlayInfo.value = null
        _levelUpOverlayInfo.value = null
        // Add any other overlay states here
    }
}*/
