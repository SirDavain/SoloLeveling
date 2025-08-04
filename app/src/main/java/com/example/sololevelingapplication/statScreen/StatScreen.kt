package com.example.sololevelingapplication.statScreen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.sololevelingapplication.ui.theme.SoloLevelingApplicationTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(
    navController: NavController,
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    // Use 'by' delegate for cleaner access
    val characterName by statsViewModel.characterName
    val characterJob by statsViewModel.characterJob
    val characterLevel by statsViewModel.characterLevel
    val characterTitle by statsViewModel.characterTitle
    val strength by statsViewModel.strength
    val agility by statsViewModel.agility
    val perception by statsViewModel.perception
    val vitality by statsViewModel.vitality
    val intelligence by statsViewModel.intelligence
    val availablePoints by statsViewModel.availablePoints
    val isLoading by statsViewModel.isLoading
    val errorMessage by statsViewModel.errorMessage
    val levelProgress = statsViewModel.levelProgress

    val uiState by statsViewModel.uiState.collectAsState()

    Box( // Use Box to easily show loading or error overlay
        modifier = Modifier
            .fillMaxSize()
            //.padding()
    ) {
        if (isLoading) {
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
                        StatRow(label = "Job", value = characterJob)
                        StatRow(label = "Title", value = characterTitle)
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
                Row () {
                    Text(
                        text = "${statsViewModel.currentXp.value}/${statsViewModel.xpForLevelingUp.value}",
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
                        StatItem(label = "STR", value = strength.toString())
                        // Example: Button to increase STR
                        /*Button(onClick = { viewModel.spendPointOnStrength() },
                            enabled = availablePoints > 0 // Disable if no points
                        ) { Text("+") }*/
                        StatItem(label = "PER", value = perception.toString())
                        StatItem(label = "INT", value = intelligence.toString())
                    }
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                        StatItem(label = "AGI", value = agility.toString())
                        StatItem(label = "VIT", value = vitality.toString())
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
                    /*Text(
                        text = "Available\nAbility\nPoints",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )*/
                    Text(
                        text = availablePoints.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        //fontSize = 10.dp
                    )
                }
            }
        }
    }
}

// Reusable composable for a label-value pair in the first section
@Composable
fun StatRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
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
}