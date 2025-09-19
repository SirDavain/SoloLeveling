package com.example.thesystem

import android.R.attr.onClick
import android.graphics.Paint
import com.example.thesystem.statScreen.StatScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import com.example.thesystem.ui.theme.SoloLevelingApplicationTheme
import com.example.thesystem.questlogscreen.QuestLogScreen
import androidx.navigation.compose.composable
import com.example.thesystem.mainpager.MainPagerScreen
import com.example.thesystem.settingsScreen.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.thesystem.animations.FullScreenEdgeLightingEffect
import com.example.thesystem.animations.LevelUpAnimationOverlay
import com.example.thesystem.animations.QuestFailedAnimationOverlay
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.animations.QuestCompletedAnimationOverlay
import com.example.thesystem.questManagement.OverlayCoordinator
import com.example.thesystem.statScreen.StatsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val statsViewModel: StatsViewModel by viewModels()
    private val questManagementViewModel: QuestManagementViewModel by viewModels()
    private val overlayViewModel: OverlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ViewModelTrace", "MainActivity onCreate: statsViewModel hashCode = ${statsViewModel.hashCode()}")

        val coordinator = object : OverlayCoordinator {
            override fun showOverlay(overlay: Overlay) {
                CoroutineScope(Dispatchers.Main.immediate).launch {
                    Log.d("OverlayCoordinator", "Coordinator called with ${overlay::class.java.simpleName}")
                    overlayViewModel.show(overlay)
                    Log.d("OverlayCoordinator", "Coordinator finished overlayViewModel.show()")
                }
            }
        }
        questManagementViewModel.overlayCoordinator = coordinator
        statsViewModel.overlayCoordinator = coordinator
        Log.d("ViewModelTrace", "MainActivity onCreate: statsViewModel.overlayCoordinator SET on hashCode = ${statsViewModel.hashCode()}")

        enableEdgeToEdge()
        setContent {
            SoloLevelingApplicationTheme {
                TheSystem(
                    overlayViewModel = overlayViewModel,
                    questManagementViewModel = questManagementViewModel,
                    statsViewModel = statsViewModel
                )
            }
        }
    }
}

@Composable
fun TheSystem(
    overlayViewModel: OverlayViewModel,
    questManagementViewModel: QuestManagementViewModel,
    statsViewModel: StatsViewModel
) {

    Log.d("ViewModelTrace", "TheSystem composable: statsViewModel hashCode = ${statsViewModel.hashCode()}")

    val navController = rememberNavController()
    val currentOverlayState by overlayViewModel.currentOverlay
    val edgeLightState by overlayViewModel.edgeLightNotificationState.collectAsState()

    Log.d("TheSystemComposable", "Recomposing. Current Overlay State: ${currentOverlayState::class.java.simpleName}")

    Box(modifier = Modifier.fillMaxSize()) {
        FullScreenEdgeLightingEffect(
            edgeLightState = edgeLightState,
            pulseColor = MaterialTheme.colorScheme.secondary
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "mainPager",
                ) {
                    composable("mainPager") {
                        MainPagerScreen(
                            navController,
                            questManagementViewModel = questManagementViewModel,
                            statsViewModel = statsViewModel
                        )
                    }
                    composable(NavRoutes.STAT_SCREEN) {
                        StatScreen(
                            navController = navController,
                            statsViewModel = statsViewModel
                        )
                    }
                    composable(NavRoutes.QUEST_LOG_SCREEN) {
                        QuestLogScreen(
                            navController = navController,
                            questViewModel = questManagementViewModel
                        )
                    }
                    composable(NavRoutes.SETTINGS_SCREEN) {
                        SettingsScreen(navController = navController)
                    }
                }
            }
            // questManagementViewModel.CheckAndFailOverdueQuests()

            // --- Display Overlays ---

            /*when (val overlay = currentOverlayState) { // Use the observed state
                is Overlay.LevelUp -> {
                    LevelUpAnimationOverlay(
                        visible = true, // Simplified: always visible when this branch is taken
                        info = overlay,
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                }
                is Overlay.QuestCompleted -> {
                    Log.d("TheSystemComposable", "WHEN: QuestCompleted branch")
                    QuestCompletedAnimationOverlay(
                        visible = true, // Simplified
                        info = overlay,
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                }
                is Overlay.QuestFailed -> {
                    QuestFailedAnimationOverlay(
                        visible = true, // Simplified
                        info = overlay,
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                }
                Overlay.None -> {
                    Log.d("TheSystemComposable", "WHEN: None branch")
                    // No overlay to show
                }
            }*/

            // 1. Level Up Overlay
            LevelUpAnimationOverlay(
                visible = currentOverlayState is Overlay.LevelUp,
                info = currentOverlayState as? Overlay.LevelUp, // Safe cast
                onDismiss = { overlayViewModel.dismiss() }
            )

            // 2. Quest Completed Overlay
            QuestCompletedAnimationOverlay(
                visible = currentOverlayState is Overlay.QuestCompleted,
                info = currentOverlayState as? Overlay.QuestCompleted,
                onDismiss = { overlayViewModel.dismiss() }
            )

            // 3. Quest Failed Overlay
            QuestFailedAnimationOverlay(
                visible = currentOverlayState is Overlay.QuestFailed,
                info = currentOverlayState as? Overlay.QuestFailed,
                onDismiss = { overlayViewModel.dismiss() }
            )
        }
    }
}
