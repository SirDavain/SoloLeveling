package com.example.thesystem.generalUtils

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

val DURATION_HOURS_RANGE = (0..12).toList()
val DURATION_MINUTES_RANGE = (0..55 step 5).toList()

@Composable
fun ScrollablePickerColumn(
    modifier: Modifier = Modifier,
    items: List<Int>,
    currentValue: Int,
    onValueSelected: (selectedValue: Int) -> Unit,
    label: String? = null,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 3
) {
    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val actualVisibleItemsCount = if (visibleItemsCount % 2 == 0) visibleItemsCount + 1 else visibleItemsCount
    val centralPaddingCount = (actualVisibleItemsCount - 1) / 2

    LaunchedEffect(currentValue, items) {
        val targetIndex = items.indexOf(currentValue)
        if (targetIndex != -1 && targetIndex != lazyListState.firstVisibleItemIndex + centralPaddingCount) {
            if (!lazyListState.isScrollInProgress) {
                lazyListState.animateScrollToItem(targetIndex)
            }
        } else if (targetIndex == -1 && items.isNotEmpty()) {
            if (!lazyListState.isScrollInProgress) {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    // Determines the settled central item
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it } // Only react when scrolling has stopped
            .collect {
                // Calculate the central item based on layout info when scroll stops
                val layoutInfo = lazyListState.layoutInfo
                if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val viewportCenterY = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                    val centralVisibleItem = layoutInfo.visibleItemsInfo
                        .minByOrNull { abs(it.offset + it.size / 2 - viewportCenterY) }

                    centralVisibleItem?.let {
                        val itemIndexInFullList = it.index - centralPaddingCount // Adjust for top padding
                        if (itemIndexInFullList >= 0 && itemIndexInFullList < items.size) {
                            val selectedValue = items[itemIndexInFullList]
                            if (selectedValue != currentValue) { // Only call if value actually changed
                                onValueSelected(selectedValue)
                            }
                        }
                    }
                }
            }
    }

    val centralDisplayedIndex by remember {
        derivedStateOf {
            if (lazyListState.layoutInfo.visibleItemsInfo.isEmpty() || items.isEmpty()) {
                // Default to the index of currentValue if possible, or 0
                items.indexOf(currentValue).takeIf { it != -1 } ?: 0
            } else {
                val layoutInfo = lazyListState.layoutInfo
                val viewportCenterY = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2f
                val centralItemInfo = layoutInfo.visibleItemsInfo
                    .minByOrNull { abs((it.offset + it.size / 2f) - viewportCenterY) }

                (centralItemInfo?.index ?: (lazyListState.firstVisibleItemIndex + centralPaddingCount)) - centralPaddingCount
            }
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
                .height(itemHeight * actualVisibleItemsCount) // Set height to show N items
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
                items(centralPaddingCount) {
                    Spacer(modifier = Modifier.height(itemHeight))
                }

                itemsIndexed(items) { index, item ->
                    val isSelected = index == centralDisplayedIndex

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

                items(centralPaddingCount) { //Bottom padding
                    Spacer(modifier = Modifier.height(itemHeight))
                }
            }

            // Optional: Add lines above and below the central item for visual guidance
            Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // This helps center the dividers
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(bottom = itemHeight - 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                // Spacer for the center item (visual only, doesn't affect selection)
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = itemHeight - 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
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
                currentValue = 3,
                onValueSelected = { selected ->
                    println("Picker Preview Selected: $selected")
                },
                label = "Count",
                modifier = Modifier.width(100.dp)
            )
        }
    }
}