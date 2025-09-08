package com.example.sololevelingapplication.generalUtils

import android.app.AlertDialog
import androidx.compose.animation.core.copy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration
import kotlin.math.abs

val DURATION_HOURS_RANGE = (0..12).toList()
val DURATION_MINUTES_RANGE = (0..59 step 30).toList()

@Composable
fun ScrollablePickerColumn(
    modifier: Modifier = Modifier,
    items: List<Int>,
    initialValue: Int,
    onValueSelected: (selectedValue: Int) -> Unit,
    label: String? = null,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 3
) {
    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val initialIndex = remember(items, initialValue) {
        val index = items.indexOf(initialValue)
        if (index >= 0) index else 0
    }

    LaunchedEffect(key1 = initialIndex) {
        lazyListState.scrollToItem(initialIndex)
    }

    val centralVisibleIndex by remember {
        derivedStateOf {
            if (!lazyListState.isScrollInProgress && lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                val layoutInfo = lazyListState.layoutInfo
                val viewportCenterY =
                    layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                layoutInfo.visibleItemsInfo
                    .minByOrNull { abs(it.offset + it.size / 2 - viewportCenterY) }
                    ?.index
                    ?: initialIndex // Fallback to initialIndex if calculation is tricky during quick scrolls
            } else {
                // While scrolling, we can estimate or stick to the last known good one
                // For simplicity, let's use firstVisibleItemIndex as a rough guide during scroll
                // A more robust solution might involve debouncing or more complex center detection
                lazyListState.firstVisibleItemIndex + (visibleItemsCount / 2)
            }
        }
    }

    LaunchedEffect(centralVisibleIndex, items) {
        if (centralVisibleIndex >= 0 && centralVisibleIndex < items.size) {
            onValueSelected(items[centralVisibleIndex])
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .height(itemHeight * visibleItemsCount) // Set height to show N items
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = lazyListState,
                flingBehavior = snapBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Padding items at the top and bottom to allow first and last actual items to reach the center
                val topBottomPaddingCount = (visibleItemsCount - 1) / 2
                items(topBottomPaddingCount) {
                    Spacer(modifier = Modifier.height(itemHeight))
                }

                itemsIndexed(items) { index, item ->
                    val isSelected = index == centralVisibleIndex

                    Text(
                        text = String.format("%02d", item), // Format with leading zero
                        style = if (isSelected) {
                            MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp, // Adjust size for selected
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp // Adjust size for non-selected
                            )
                        },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        modifier = Modifier
                            .height(itemHeight)
                            .wrapContentHeight(Alignment.CenterVertically) // Vertically center text in its space
                            .alpha(if (isSelected) 1f else 0.5f)
                    )
                }

                items(topBottomPaddingCount) {
                    Spacer(modifier = Modifier.height(itemHeight))
                }
            }

            // Optional: Add lines above and below the central item for visual guidance
            Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // This helps center the dividers
            ) {
                val dividerPadding =
                    (itemHeight * (visibleItemsCount - 1) / 2) - itemHeight / 2 + 2.dp // Adjust based on itemHeight
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // Make divider shorter than picker width
                        .padding(bottom = itemHeight - 4.dp) // Position above center item
                )
                Spacer(modifier = Modifier.height(itemHeight - 8.dp)) // Space for the center item
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    // .padding(top = itemHeight -4.dp) // Position below center item
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun QuestDurationWheelDialog(
    modifier: Modifier = Modifier,
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    onDurationChange: (hours: Int, minutes: Int) -> Unit
) {
    val validInitialMinutes = remember(initialMinutes) {
        DURATION_MINUTES_RANGE.find { it == initialMinutes } ?: DURATION_MINUTES_RANGE.first()
    }

    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMinutes by remember { mutableIntStateOf(validInitialMinutes) }

    LaunchedEffect(selectedHours, selectedMinutes) {
        onDurationChange(selectedHours, selectedMinutes)
    }

    AlertDialog(

    )
}

@Composable
fun QuestDurationWheelPicker(
    modifier: Modifier = Modifier,
    initialHours: Int = 0,
    initialMinutes: Int = 0, // Ensure this is one of the values in DURATION_MINUTES_RANGE
    onDurationChange: (hours: Int, minutes: Int) -> Unit
) {
    // Ensure initialMinutes is a valid value from the range
    val validInitialMinutes = remember(initialMinutes) {
        DURATION_MINUTES_RANGE.find { it == initialMinutes } ?: DURATION_MINUTES_RANGE.first()
    }

    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMinutes by remember { mutableIntStateOf(validInitialMinutes) }

    // Use LaunchedEffect to call onDurationChange when selectedHours or selectedMinutes change
    LaunchedEffect(selectedHours, selectedMinutes) {
        onDurationChange(selectedHours, selectedMinutes)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ScrollablePickerColumn(
            modifier = Modifier.weight(1f),
            items = DURATION_HOURS_RANGE,
            initialValue = selectedHours,
            onValueSelected = { hours ->
                selectedHours = hours
            },
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
            initialValue = selectedMinutes, // Use the validated one
            onValueSelected = { minutes ->
                selectedMinutes = minutes
            },
            label = "Minutes"
        )
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 300)
@Composable
fun QuestDurationWheelPickerPreview() {
    MaterialTheme { // Make sure a MaterialTheme is applied for previews
        Surface(modifier = Modifier.padding(16.dp)) {
            var hours by remember { mutableIntStateOf(1) }
            var minutes by remember { mutableIntStateOf(30) } // 30 is in 0..59 step 15
            Column {
                QuestDurationWheelPicker(
                    initialHours = hours,
                    initialMinutes = minutes,
                    onDurationChange = { h, m ->
                        hours = h
                        minutes = m
                        println("Preview Duration Changed: $h hours, $m minutes")
                    }
                )
                Spacer(Modifier.height(20.dp))
                Text("Current selection: ${String.format("%02d", hours)}h ${String.format("%02d", minutes)}m")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScrollablePickerColumnPreview() {
    MaterialTheme {
        Surface {
            ScrollablePickerColumn(
                items = (0..10).toList(),
                initialValue = 3,
                onValueSelected = { selected ->
                    println("Picker Preview Selected: $selected")
                },
                label = "Count",
                modifier = Modifier.width(100.dp)
            )
        }
    }
}