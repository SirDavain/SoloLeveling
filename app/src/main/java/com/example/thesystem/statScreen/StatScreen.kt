package com.example.thesystem.statScreen

import android.annotation.SuppressLint
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.thesystem.ui.theme.SoloLevelingApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesystem.Overlay
import com.example.thesystem.OverlayViewModel
import com.example.thesystem.animations.LevelUpAnimationOverlay
import com.example.thesystem.questManagement.QuestManagementViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(
    navController: NavController,
    questViewModel: QuestManagementViewModel = viewModel(),
    statsViewModel: StatsViewModel = viewModel(),
    overlayViewModel: OverlayViewModel = viewModel()
) {
    val levelProgress = statsViewModel.levelProgress

    var showLevelUpOverlay by remember { mutableStateOf(false) }
    var levelUpOverlayInfo by remember { mutableStateOf<Overlay.LevelUp?>(null) }

    val statsUiState by statsViewModel.uiState.collectAsStateWithLifecycle()

    //val uiState by questViewModel.uiState.collectAsState()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.padding()
    ) {
        if (statsUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Overall padding for content
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name, Job, Level, Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            Text(
                                text = "100",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row {
                            Text(
                                text = "Level",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        StatRow(label = "Job", value = statsUiState.stats?.job)
                        StatRow(label = "Title", value = statsUiState.stats?.title)
                    }
                }

                // Level Progress Bar
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier.weight(1f),
                        /*color = ,
                        trackColor = */
                    )
                }

                // Level Progress value
                Row () {
                    Text(
                        text = "${statsUiState.stats?.currentXp}/${statsUiState.stats?.xpToNextLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Core Stats
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                        StatItem(label = "STR", value = statsUiState.stats?.strength.toString())
                        // Example: Button to increase STR
                        /*Button(onClick = { viewModel.spendPointOnStrength() },
                            enabled = availablePoints > 0 // Disable if no points
                        ) { Text("+") }*/
                        StatItem(label = "PER", value = statsUiState.stats?.perception.toString())
                        StatItem(label = "INT", value = statsUiState.stats?.intelligence.toString())
                    }
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                        StatItem(label = "AGI", value = statsUiState.stats?.agility.toString())
                        StatItem(label = "VIT", value = statsUiState.stats?.vitality.toString())
                    }
                }

                // Section 3: Available Ability Points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column (
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Available")
                        Text("Ability")
                        Text("Points")
                    }
                    Text(
                        text = (statsUiState.stats?.availablePoints ?: 0).toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        //fontSize = 10.dp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Toast test
                Button(
                    onClick = {
                        Toast.makeText(context, "This is a toast", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Show a toast")
                }

                Spacer(Modifier.height(10.dp))

                // Level Up Overlay test
                Button(
                    onClick = {
                        val newLevelToShow = statsUiState.stats?.level
                        levelUpOverlayInfo = Overlay.LevelUp(newLevelToShow!!, 1)
                        showLevelUpOverlay = true
                    }
                ) {
                    Text("Level Up Overlay")
                }

                Spacer(Modifier.height(10.dp))

                // Simulate spending points
                Button(
                    onClick = {
                        statsViewModel.spendAbilityPoints(1, "strength")
                    },
                    enabled = (statsUiState.stats?.availablePoints ?: 0) > 0,
                ) {
                    Text("Increase strength")
                }

                Spacer(Modifier.height(10.dp))

                // Increase ability points
                Button(
                    onClick = {
                        statsViewModel.addAbilityPoints()
                    }
                ) {
                    Text("Add points")
                }
            }
            if (showLevelUpOverlay && levelUpOverlayInfo != null) {
                LevelUpAnimationOverlay(
                    visible = true,
                    info = levelUpOverlayInfo!!,
                    onDismiss = {
                        showLevelUpOverlay = false
                        levelUpOverlayInfo = null
                        overlayViewModel.dismiss()
                    }
                )
                overlayViewModel.triggerEdgeLighting()
            }
        }
    }
}

// Reusable composable for a label-value pair in the first section
@Composable
fun StatRow(label: String, value: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(text = value.toString(), style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(modifier = Modifier.height(4.dp)) // Small space between these stat rows
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp) // Fixed width for labels for alignment
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/*
@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun StatScreenPreview() {

    val previewViewModel = StatsViewModel().apply {
        //use default values
    }
    SoloLevelingApplicationTheme {
        StatScreen(
            navController = rememberNavController(),
            statsViewModel = previewViewModel
        )
    }
}*/
