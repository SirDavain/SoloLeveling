package com.example.thesystem

import com.example.thesystem.statScreen.StatScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.thesystem.animations.FullScreenEdgeLightingEffect
import com.example.thesystem.animations.LevelUpAnimationOverlay
import com.example.thesystem.animations.QuestFailedAnimationOverlay
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.animations.QuestCompletedAnimationOverlay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheSystem()
        }
    }
}

@Composable
fun TheSystem() {

    val overlayViewModel: OverlayViewModel = hiltViewModel()
    val questManagementViewModel: QuestManagementViewModel = hiltViewModel()
    val navController = rememberNavController()
    val edgeLightState by overlayViewModel.edgeLightNotificationState.collectAsState()

    SoloLevelingApplicationTheme {
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
                        composable("mainPager") { MainPagerScreen(navController) }
                        composable(NavRoutes.STAT_SCREEN) {
                            StatScreen(navController = navController)
                        }
                        composable(NavRoutes.QUEST_LOG_SCREEN) {
                            QuestLogScreen(navController = navController)
                        }
                        composable(NavRoutes.SETTINGS_SCREEN) {
                            SettingsScreen(navController = navController)
                        }
                    }
                }
                questManagementViewModel.CheckAndFailOverdueQuests(overlayViewModel)

                // --- Display Overlays ---

                val currentOverlay by overlayViewModel.currentOverlay
                when (val overlay = currentOverlay) {
                    is Overlay.QuestFailed -> QuestFailedAnimationOverlay(
                        visible = true,
                        info = Overlay.QuestFailed(overlay.questText, -42),
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                    is Overlay.LevelUp -> LevelUpAnimationOverlay(
                        visible = true,
                        info = Overlay.LevelUp(3, 1),
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                    is Overlay.QuestCompleted -> QuestCompletedAnimationOverlay(
                        visible = true,
                        info = Overlay.QuestCompleted(overlay.questText, 42),
                        onDismiss = { overlayViewModel.dismiss() }
                    )
                    Overlay.None -> { }
                }
            }
        }
    }
}

@Composable
fun InputField(
    str: String,
    onTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = str,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 48.dp)
                .testTag("input_text_field") // for testing purposes
        )
        Spacer(modifier = Modifier.width(30.dp))
        FloatingActionButton(
            modifier = Modifier
                .testTag("addEditFab"),
            onClick = { /*doSmth()*/ },
            shape = CircleShape,
        ) {
            Log.d("InputField", "FAB for adding something clicked")
            Icon(Icons.Filled.Add, contentDescription = "Add/edit")
        }
    }
}
