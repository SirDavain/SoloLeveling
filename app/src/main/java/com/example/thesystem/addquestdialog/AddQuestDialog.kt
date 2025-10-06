package com.example.thesystem.addquestdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.thesystem.QuestDurationWheelPicker
import com.example.thesystem.questManagement.QuestUiState
import com.example.thesystem.questlogscreen.QuestCategoryDropdown
import com.example.thesystem.xpLogic.QuestCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.SelectableDates

fun formatMillisToDisplayDate(millis: Long?, defaultText: String = "Select Deadline"): String {
    if (millis == null) return defaultText

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = millis

    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault() // Display in local timezone
    return sdf.format(calendar.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestFullScreenContent(
    // Passed from ViewModel's UiState
    currentQuestText: String,
    currentQuestCategory: QuestCategory,
    currentHours: Int,
    currentMinutes: Int,
    userSelectedDate: Long?,
    deadlineMillis: Long?,
    showDeadlinePicker: Boolean,
    onQuestTextChanged: (String) -> Unit,
    onCategoryChanged: (QuestCategory) -> Unit,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onDeadlineSelected: (Long?) -> Unit,
    onShowDeadlinePicker: () -> Unit,
    onDismissDeadlinePicker: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if (showDeadlinePicker) {
        val todayUtcMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineMillis ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayUtcMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = onDismissDeadlinePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeadlineSelected(datePickerState.selectedDateMillis)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeadlinePicker) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                currentCategory = currentQuestCategory,
                onHoursChanged = onHoursChanged,
                onMinutesChanged = onMinutesChanged,
            )
            Spacer(Modifier.height(24.dp))

            Text("Quest Deadline:", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onShowDeadlinePicker,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(formatMillisToDisplayDate(userSelectedDate))
            }
        }
    }
}

@Composable
fun AddQuestFullScreenDialog(
    dialogUiState: QuestUiState,
    onQuestTextChanged: (String) -> Unit,
    onCategoryChanged: (QuestCategory) -> Unit,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onDeadlineSelected: (Long?) -> Unit,
    onShowDeadlinePicker: () -> Unit,
    onDismissDeadlinePicker: () -> Unit,
    onConfirm: () -> Unit, // call to ViewModel's onConfirmAddQuest
    onDismiss: () -> Unit // call to ViewModel's onDismissAddQuestDialog
) {
    if (dialogUiState.showAddQuestDialog) {
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
                    currentQuestText = dialogUiState.newQuestText,
                    currentQuestCategory = dialogUiState.newQuestCategory,
                    currentHours = dialogUiState.newQuestHours,
                    currentMinutes = dialogUiState.newQuestMinutes,
                    deadlineMillis = dialogUiState.deadlineMillis,
                    userSelectedDate = dialogUiState.newQuestUserSelectedDate,
                    showDeadlinePicker = dialogUiState.showDeadlinePicker,
                    onQuestTextChanged = onQuestTextChanged,
                    onCategoryChanged = onCategoryChanged,
                    onHoursChanged = onHoursChanged,
                    onMinutesChanged = onMinutesChanged,
                    onDeadlineSelected = onDeadlineSelected,
                    onShowDeadlinePicker = onShowDeadlinePicker,
                    onDismissDeadlinePicker = onDismissDeadlinePicker,
                    onConfirm = onConfirm,
                    onDismiss = onDismiss
                )
            }
        }
    }
}