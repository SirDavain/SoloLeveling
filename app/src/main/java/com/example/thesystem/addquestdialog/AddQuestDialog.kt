package com.example.thesystem.addquestdialog

import android.R.attr.enabled
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thesystem.questlogscreen.capitalizeWords
import com.example.thesystem.xpLogic.QuestCategory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.thesystem.QuestDurationWheelPicker
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.questlogscreen.QuestCategoryDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestFullScreenContent(
    // Passed from ViewModel's UiState
    currentQuestText: String,
    currentQuestCategory: QuestCategory,
    currentHours: Int,
    currentMinutes: Int,
    onQuestTextChanged: (String) -> Unit,
    onCategoryChanged: (QuestCategory) -> Unit,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Quest") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onConfirm,
                    enabled = currentQuestText.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save Quest")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Make content scrollable if it overflows
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = currentQuestText,
                onValueChange = onQuestTextChanged,
                label = { Text("Accept a new quest") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true // Or false if you want multi-line quest text
            )
            Spacer(Modifier.height(24.dp))

            // Get a dropdown of quest categories
            QuestCategoryDropdown(
                selectedOption = currentQuestCategory.category,
                onOptionSelected = { selectedName ->
                    val newCategory = QuestCategory.entries.find { it.category == selectedName }
                    if (newCategory != null) {
                        onCategoryChanged(newCategory)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            Text("Quest Duration:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            QuestDurationWheelPicker(
                currentHours = currentHours,
                currentMinutes = currentMinutes,
                onHoursChanged = onHoursChanged,
                onMinutesChanged = onMinutesChanged,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AddQuestFullScreenDialog(
    showDialog: Boolean,
    // Current values from ViewModel UiState
    currentQuestText: String,
    currentCategory: QuestCategory,
    currentHours: Int,
    currentMinutes: Int,
    // Callbacks to ViewModel methods
    onQuestTextChanged: (String) -> Unit,
    onCategoryChanged: (QuestCategory) -> Unit,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onConfirm: () -> Unit, // call to ViewModel's onConfirmAddQuest
    onDismiss: () -> Unit // call to ViewModel's onDismissAddQuestDialog
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // Crucial for full screen
                dismissOnClickOutside = false, // Optional: prevent dismissal by clicking outside
                dismissOnBackPress = true      // Optional: allow back press to dismiss
            )
        ) {
            // The content of the Dialog will be our AddQuestFullScreenContent
            // It's wrapped in a Surface for theming and elevation (optional but good practice)
            Surface(
                modifier = Modifier.fillMaxSize(), // Content fills the Dialog
                //shape = MaterialTheme.shapes.medium // Or extraLarge for more rounded corners if desired
                // Or RectangleShape for no rounding if true edge-to-edge
            ) {
                AddQuestFullScreenContent(
                    currentQuestText = currentQuestText,
                    currentQuestCategory = currentCategory,
                    currentHours = currentHours,
                    currentMinutes = currentMinutes,
                    onQuestTextChanged = onQuestTextChanged,
                    onCategoryChanged = onCategoryChanged,
                    onHoursChanged = onHoursChanged,
                    onMinutesChanged = onMinutesChanged,
                    onConfirm = onConfirm, // Pass the confirm action
                    onDismiss = onDismiss // Pass the dismiss action
                )
            }
        }
    }
}