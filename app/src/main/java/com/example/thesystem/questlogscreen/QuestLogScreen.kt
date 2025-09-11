package com.example.thesystem.questlogscreen

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.xpLogic.QuestCategory
import androidx.compose.runtime.setValue
import com.example.thesystem.addquestdialog.AddQuestFullScreenDialog

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
            }
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
                        QuestCategory.entries.forEach { category ->
                            val questsInCategory = questsByCategory[category]
                            if (!questsInCategory.isNullOrEmpty()) {
                                item {
                                    Text(
                                        text = category.name.replace("_", " ").capitalizeWords(),
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
            AddQuestFullScreenDialog(
                showDialog = uiState.showAddQuestDialog,
                currentQuestText = uiState.newQuestText, // Pass from ViewModel's UiState
                currentCategory = uiState.newQuestCategory, // Pass from ViewModel's UiState
                currentHours = uiState.newQuestHours, // Pass from ViewModel's UiState
                currentMinutes = uiState.newQuestMinutes, // Pass from ViewModel's UiState
                onQuestTextChanged = { newText -> // Lambda calling ViewModel method
                    questViewModel.onNewQuestTextChanged(newText)
                },
                onCategoryChanged = { newCategory -> // Lambda calling ViewModel method
                    questViewModel.onNewQuestCategoryChanged(newCategory)
                },
                onHoursChanged = { newHours -> // Lambda calling ViewModel method
                    questViewModel.onNewQuestHoursChanged(newHours)
                },
                onMinutesChanged = { newMinutes -> // Lambda calling ViewModel method
                    questViewModel.onNewQuestMinutesChanged(newMinutes)
                },
                onConfirm = {
                    questViewModel.onConfirmAddQuest() // ViewModel handles logic and dismissal
                },
                onDismiss = {
                    questViewModel.onDismissAddQuestDialog()
                }
            )
        }
    }
}

// Helper to capitalize words for category titles
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.lowercase().replaceFirstChar(Char::titlecase) }

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
