package com.example.sololevelingapplication

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun QuestDurationInput(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    onDurationSet: (hours: Int, minutes: Int) -> Unit
) {
    val context = LocalContext.current
    var selectedHours by remember { mutableStateOf(initialHours) }
    var selectedMinutes by remember { mutableStateOf(initialMinutes) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedHours = hourOfDay
            selectedMinutes = minute
            onDurationSet(hourOfDay, minute)
        },
        selectedHours,
        selectedMinutes,
        true // true for 24-hour format, false for AM/PM
    )

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selected Duration: ${String.format("%02d", selectedHours)}h ${String.format("%02d", selectedMinutes)}m",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { timePickerDialog.show() } // Allow clicking text also
        )
        Button(onClick = { timePickerDialog.show() }) {
            Text("Set Quest Duration")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestDurationInputPreview() {
    MaterialTheme {
        Surface {
            QuestDurationInput { hours, minutes ->
                // Handle the duration in preview if needed
                println("Preview Duration: $hours hours, $minutes minutes")
            }
        }
    }
}