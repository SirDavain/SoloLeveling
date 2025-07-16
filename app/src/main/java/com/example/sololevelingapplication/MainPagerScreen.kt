package com.example.sololevelingapplication

import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.parser.Section.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.example.sololevelingapplication.questlogscreen.QuestLogScreen
import com.example.sololevelingapplication.settingsScreen.SettingsScreen
import com.example.sololevelingapplication.statScreen.StatScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPagerScreen(navController: NavController) {
    val pageCount = 2
    val pagerState = rememberPagerState(pageCount = { pageCount })

    var fabOnClick by remember { mutableStateOf<(() -> Unit)?>(null) }
    var fabIcon by remember { mutableStateOf<ImageVector?>(null) }

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> {
                fabIcon = null
                fabOnClick = null
            }
            1 -> {
                fabIcon = Icons.Filled.Add
                fabOnClick = { Log.d("MainPager", "Quest Log FAB clicked") }
            }
            else -> {
                fabIcon = null
                fabOnClick = null
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(getTitleForPage(pagerState.currentPage))
            })
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                        //.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DotsIndicator(
                        totalDots = pageCount,
                        selectedIndex = pagerState.currentPage,
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unSelectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        },
        floatingActionButton = {
            fabIcon?.let { icon ->
                fabOnClick?.let { onClickAction ->
                    FloatingActionButton(onClick = onClickAction) {
                        Icon(icon, contentDescription = "Page Action")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> StatScreen(navController = navController)
                1 -> QuestLogScreen(navController = navController)
                //2 -> SettingsScreen(navController = navController)
            }
        }
    }
}

@Composable
fun getTitleForPage(page: Int): String {
    return when (page) {
        0 -> "Status"
        1 -> "Quest Log"
        //2 -> "Settings"
        else -> "Solo Lvling"
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
    dotSize: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            val color = if (index == selectedIndex) selectedColor else unSelectedColor
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}