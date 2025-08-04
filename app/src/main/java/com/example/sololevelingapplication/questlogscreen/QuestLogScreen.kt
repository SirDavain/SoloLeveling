package com.example.sololevelingapplication.questlogscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.example.sololevelingapplication.QuestCategory
import com.example.sololevelingapplication.QuestDao
import com.example.sololevelingapplication.questManagement.QuestManagementViewModel
import com.example.sololevelingapplication.questManagement.UiQuest

// This is where the magic happens:
// Displays your current "quests" (habits and routines you set yourself)
// Here you can tick them off and receive XP for it or establish new ones using a FAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestLogScreen(
    navController: NavController,
    questViewModel: QuestManagementViewModel = viewModel()
) {
    val uiState by questViewModel.uiState.collectAsState()

    // Group quests by category
    val questsByCategory = remember(uiState.quests) {
        uiState.quests.groupBy { it.category }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
            } /*else {
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
            }*/
            else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.quests.isEmpty()) {
                        item {
                            Text(
                                "No quests here yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp).fillMaxWidth()
                            )
                        }
                    } else {
                        QuestCategory.entries.forEach { category -> // changed values() to entries
                            val questsInCategory = questsByCategory[category]
                            if (!questsInCategory.isNullOrEmpty()) {
                                item {
                                    Text(
                                        text = category.name.replace("_", "").capitalizeWords(),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                    )
                                }
                                items(questsInCategory, key = { it.id }) { quest ->
                                    QuestListItem(
                                        quest = quest,
                                        onDoneChange = { isDone ->
                                            questViewModel.onQuestDoneChanged(quest.id, isDone)
                                        },
                                        onTextChange = { newText ->
                                            questViewModel.onQuestTextChanged(quest.id, newText)
                                        },
                                        onToggleEdit = { questViewModel.onToggleEditQuest(quest.id) },
                                        onSaveEdit = { questViewModel.onSaveQuestEdit(quest.id) },
                                        onDelete = { questViewModel.onDeleteQuest(quest.id) }
                                    )
                                    HorizontalDivider(
                                        Modifier,
                                        DividerDefaults.Thickness,
                                        DividerDefaults.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (uiState.showAddQuestDialog) {
            AddQuestDialog(
                newQuestText = questViewModel.newQuestText.value,
                onTextChange = { questViewModel.newQuestText.value = it },
                selectedCategory = questViewModel.newQuestCategory.value,
                onCategoryChange = { questViewModel.newQuestCategory.value = it },
                selectedTimeFrame = questViewModel.newQuestXpCategory.value,
                onXpCategoryChange = { newXpCategory ->
                    questViewModel.onNewQuestXpCategoryChanged(newXpCategory)
                },
                onDismiss = { questViewModel.onDismissAddQuestDialog() },
                onConfirm = { questViewModel.onConfirmAddQuest() }
            )
        }
    }
}

// Helper to capitalize words for category titles
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.lowercase().capitalize() }


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

/*
@Preview
@Composable
fun QuestLogScreenPreview () {
    val previewViewModel = QuestManagementViewModel(questDao = QuestDao).apply {
        //uses default values
    }
    SoloLevelingApplicationTheme {
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp, top = 16.dp)
        ) {
            QuestLogScreen(
                navController = rememberNavController(),
                questViewModel = previewViewModel
            )
        }
    }

}*/
