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
import com.example.thesystem.QuestDurationWheelPicker
import com.example.thesystem.questManagement.UiQuest
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
    onDelete: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var timeLeftDisplay by remember(quest.timeOfCreation, quest.isDone) { mutableStateOf("") }

    LaunchedEffect(quest.isBeingEdited) {
        if (quest.isBeingEdited) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(key1 = quest.timeOfCreation, key2 = quest.isDone, key3 = quest.category) {
        if (!quest.isDone && (quest.category == QuestCategory.ONE_TIME || quest.category == QuestCategory.DAILY)) {
            // For a duration of 24 hours:
            val questDurationMillis = TimeUnit.HOURS.toMillis(24)

            while (true) {
                val currentTimeInMillis = System.currentTimeMillis()
                val timeElapsedInMillis = currentTimeInMillis - quest.timeOfCreation
                var remainingTimeInMillis = questDurationMillis - timeElapsedInMillis

                if (remainingTimeInMillis <= 0) {
                    timeLeftDisplay = "You failed!"
                    break
                }

                // For formatting
                val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeInMillis)
                remainingTimeInMillis -= TimeUnit.HOURS.toMillis(hours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis)
                remainingTimeInMillis -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis)

                timeLeftDisplay = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000) // wait for one second
            }
        } else if (quest.isDone) {
            timeLeftDisplay = "Quest complete"
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
            enabled = !quest.isBeingEdited, // Disable when editing text
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
                        if (quest.isDone) {
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
                    color = if (timeLeftDisplay == "You failed!") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
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
