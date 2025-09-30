package com.example.thesystem

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thesystem.generalUtils.DURATION_HOURS_RANGE
import com.example.thesystem.generalUtils.DURATION_MINUTES_RANGE
import com.example.thesystem.generalUtils.ScrollablePickerColumn

@Composable
fun QuestDurationWheelPicker(
    modifier: Modifier = Modifier,
    currentHours: Int,
    currentMinutes: Int,
    onHoursChanged: (hours: Int) -> Unit,
    onMinutesChanged: (minutes: Int) -> Unit,
) {
    // Ensure initialMinutes is a valid value from the range
    val validCurrentMinutes = remember(currentMinutes) {
        DURATION_MINUTES_RANGE.find { it == currentMinutes } ?: DURATION_MINUTES_RANGE.first()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ScrollablePickerColumn(
            modifier = Modifier.weight(1f),
            items = DURATION_HOURS_RANGE,
            currentValue = currentHours,
            onValueSelected = onHoursChanged,
            label = "Hours"
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        ScrollablePickerColumn(
            modifier = Modifier.weight(1f),
            items = DURATION_MINUTES_RANGE,
            currentValue = validCurrentMinutes,
            onValueSelected = onMinutesChanged,
            label = "Minutes"
        )
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 300)
@Composable
fun QuestDurationWheelPickerPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            // For preview, manage state locally
            var hours by remember { mutableIntStateOf(1) }
            var minutes by remember { mutableIntStateOf(30) }

            Column {
                QuestDurationWheelPicker(
                    currentHours = hours,
                    currentMinutes = minutes,
                    onHoursChanged = { h -> hours = h },
                    onMinutesChanged = { m -> minutes = m }
                )
                Spacer(Modifier.height(20.dp))
                Text("Preview Selection: ${String.format("%02d", hours)}h ${String.format("%02d", minutes)}m")
            }
        }
    }
}