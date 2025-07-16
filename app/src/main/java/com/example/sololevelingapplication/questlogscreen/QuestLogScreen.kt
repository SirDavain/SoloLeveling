package com.example.sololevelingapplication.questlogscreen

import android.R
import android.R.attr.bottom
import android.R.attr.onClick
import android.R.attr.top
import androidx.compose.foundation.layout.RowScope
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sololevelingapplication.ui.theme.SoloLevelingApplicationTheme
import kotlin.apply
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.example.sololevelingapplication.MainPagerScreen

// This is where the magic happens:
// Displays your current "quests" (habits and routines you set yourself)
// Here you can tick them off and receive XP for it or establish new ones using a FAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestLogScreen(
    navController: NavController,
    viewModel: QuestLogViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp))
            } else {
                val oneTimeQuests = listOf("Walk 10.000 steps", "Journal", "Wake up on time")
                val dailyQuests = listOf("Run 10km", "Do 100 Push Ups", "100 Sit-Ups", "100 Squats")
                val weeklyQuests = listOf("Workout six times", "Practice the piano")

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item { QuestSectionTitle("One-time") }
                    if (oneTimeQuests.isEmpty()) {
                        item { EmptyQuestText() }
                    } else {
                        items(oneTimeQuests) { quest -> QuestItem(quest) }
                    }
                    item { QuestSectionTitle("Daily") }
                    if (dailyQuests.isEmpty()) {
                        item { EmptyQuestText() }
                    } else {
                        items(dailyQuests) { quest -> QuestItem(quest) }
                    }

                    item { QuestSectionTitle("Weekly") }
                    if (weeklyQuests.isEmpty()) {
                        item { EmptyQuestText() }
                    } else {
                        items(weeklyQuests) { quest -> QuestItem(quest) }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun QuestItem(questTitle: String) {
    Text(
        text = "- $questTitle",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun EmptyQuestText() {
    Text(
        text = "There are no quests here yet.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Preview
@Composable
fun QuestLogScreenPreview () {
    val previewViewModel = QuestLogViewModel().apply {
        //uses default values
    }
    SoloLevelingApplicationTheme {
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp, top = 16.dp)
        ) {
            QuestLogScreen(
                navController = rememberNavController(),
                viewModel = previewViewModel
            )
        }
    }

}