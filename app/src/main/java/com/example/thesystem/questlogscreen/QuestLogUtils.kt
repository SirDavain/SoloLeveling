package com.example.thesystem.questlogscreen

import android.R.attr.enabled
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import com.example.thesystem.Overlay
import com.example.thesystem.QuestDurationWheelPicker
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.questManagement.UiQuest
import com.example.thesystem.questManagement.toQuestEntity
import com.example.thesystem.xpLogic.QuestCategory
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestListItem(
    quest: UiQuest,
    onDoneChange: () -> Unit,
    onTextChange: (String) -> Unit,
    onToggleEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuestFailed: (questId: String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var timeLeftDisplay by remember(quest.timeOfCreation, quest.isDone, quest.deadline) { mutableStateOf("") }

    LaunchedEffect(quest.isBeingEdited) {
        if (quest.isBeingEdited) {
            focusRequester.requestFocus()
        }
    }

    // Time until deadline
    LaunchedEffect(quest.timeOfCreation, quest.isDone, quest.deadline, quest.category) {
        if (!quest.isDone && quest.deadline != null) {
            val deadlineMillis: Long = quest.deadline

            while (true) {
                val currentTimeMillis = System.currentTimeMillis()
                val remainingTimeMillis = deadlineMillis - currentTimeMillis

                if (remainingTimeMillis <= 0) {
                    timeLeftDisplay = "Failed quest"
                    if (!quest.hasFailed) {
                        onQuestFailed(quest.id)
                    }
                    break
                }

                var remainder = remainingTimeMillis // Start with the total remaining milliseconds

                val days = TimeUnit.MILLISECONDS.toDays(remainder)
                remainder -= TimeUnit.DAYS.toMillis(days) // Subtract the milliseconds accounted for by full days

                val hours = TimeUnit.MILLISECONDS.toHours(remainder)
                remainder -= TimeUnit.HOURS.toMillis(hours) // Subtract the milliseconds accounted for by full hours

                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainder)
                remainder -= TimeUnit.MINUTES.toMillis(minutes) // Subtract the milliseconds accounted for by full minutes

                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainder)
                // No need to subtract seconds from remainder if you're not going to milliseconds

                // Ensure formatting uses the calculated components, not the total remaining time for seconds
                timeLeftDisplay = if (days > 0)
                    String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", days, hours, minutes, seconds)
                else
                    String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds)

                delay(1000)

                // Check if quest was completed during delay
                if (quest.isDone) {
                    timeLeftDisplay = "Quest complete"
                    break
                }
            }
        } else if (quest.isDone) {
            timeLeftDisplay = "Quest complete"
        } else if (quest.hasFailed) {
            timeLeftDisplay = "Failed quest"
        } else {
            timeLeftDisplay = ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable( // Use combinedClickable for long press to edit, single click for details (optional)
                onClick = {
                    // Could be used to navigate to a quest detail screen if you had one
                    // For now, perhaps toggle done status on simple click IF NOT editing.
                    if (!quest.isBeingEdited) {
                        // onDoneChange(!quest.isDone) // RadioButton handles it
                    }
                },
                onLongClick = {
                    if (!quest.isBeingEdited) { // Only toggle to edit mode if not already editing
                        onToggleEdit()
                    }
                }
            )
            .padding(vertical = 8.dp, horizontal = 0.dp), // Adjust padding as needed
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = quest.isDone,
            onClick = onDoneChange,
            enabled = !quest.isBeingEdited && !quest.hasFailed, // Disable when editing text or if it has failed
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.width(8.dp))

        if (quest.isBeingEdited) {
            OutlinedTextField(
                value = quest.text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.key == Key.Enter || it.key == Key.Escape) {
                            onSaveEdit()
                            focusManager.clearFocus()
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSaveEdit()
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            onSaveEdit()
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Filled.Check, "Save edit")
                        }
                        IconButton(onClick = { // Cancel edit (revert or just exit)
                            onToggleEdit() // This will set isBeingEdited to false
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Filled.Close, "Cancel edit")
                        }
                        if (quest.isDone || quest.hasFailed) {
                            IconButton(onClick = {
                                onDelete() // Delete a completed quest
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Filled.Delete, "Delete quest")
                            }
                        }
                    }
                }
            )
        } else {
            Text(
                text = quest.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (quest.isDone) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            //Time left before quest fails
            if (timeLeftDisplay.isNotBlank()) {
                Text(
                    text = timeLeftDisplay,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (timeLeftDisplay == "Failed quest") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

interface AppLogger {
    fun d(tag: String, message: String)
}

fun getAllQuestCategoryNames(): List<String> {
    return QuestCategory.entries.map { it.name }
}

fun getAllQuestCategoryDisplayNames(): List<String> {
    return QuestCategory.entries.map { it.category }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestCategoryDropdown(
    modifier: Modifier = Modifier,
    label: String = "Quest Category",
    options: List<String> = getAllQuestCategoryDisplayNames(),
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}
