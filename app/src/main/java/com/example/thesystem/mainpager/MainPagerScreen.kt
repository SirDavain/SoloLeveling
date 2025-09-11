package com.example.thesystem.mainpager

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.example.thesystem.questlogscreen.QuestLogScreen
import com.example.thesystem.statScreen.StatScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.thesystem.questManagement.QuestManagementViewModel
import com.example.thesystem.statScreen.StatsViewModel

private const val STAT_PAGE_INDEX = 0
private const val QUEST_LOG_PAGE_INDEX = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPagerScreen(
    navController: NavController,
    questManagementViewModel: QuestManagementViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val pageCount = 2
    val pagerState = rememberPagerState(pageCount = { pageCount })

    var fabOnClick by remember { mutableStateOf<(() -> Unit)?>(null) }
    var fabIcon by remember { mutableStateOf<ImageVector?>(null) }
    var fabContentDescription by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pagerState.currentPage, questManagementViewModel, statsViewModel) {
        when (pagerState.currentPage) {
            STAT_PAGE_INDEX -> {
                fabIcon = null
                fabOnClick = null
                fabContentDescription = null
            }
            QUEST_LOG_PAGE_INDEX -> {
                fabIcon = Icons.Filled.Add
                fabOnClick = {
                    Log.d("MainPager", "Quest Log FAB clicked")
                    questManagementViewModel.onAddQuestClicked()
                }
                fabContentDescription = "Add a Quest"
            }
            else -> {
                fabIcon = null
                fabOnClick = null
                fabContentDescription = null
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText = getTitleForPage(pagerState.currentPage)
                    Log.d("MainPagerScreen", "TopAppBar title: '$titleText'")
                    Text(
                        text = titleText,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))
                        DotsIndicator(
                            totalDots = pageCount,
                            selectedIndex = pagerState.currentPage,
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unSelectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        // Need to find a better solution - works hardcoded for now
                        Spacer(Modifier.weight(.65f))
                    }
                },
                floatingActionButton = {
                    if (fabIcon != null && fabOnClick != null) {
                        FloatingActionButton(
                            onClick = fabOnClick!!,
                            modifier = Modifier
                                .size(56.dp),
                                //.padding(8.dp),
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp) // a little shadow?
                        ) {
                            Icon(fabIcon!!, contentDescription = fabContentDescription)
                        }
                    } else {
                        Spacer(Modifier.size(56.dp))
                    }
                },
                containerColor = Color.Transparent
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                STAT_PAGE_INDEX -> StatScreen(
                    navController = navController,
                    statsViewModel = statsViewModel
                )
                QUEST_LOG_PAGE_INDEX -> QuestLogScreen(
                    navController = navController,
                    questViewModel = questManagementViewModel
                )
                //2 -> SettingsScreen(navController = navController)
            }
        }
    }
}

@Composable
fun getTitleForPage(page: Int): String {
    Log.d("Main Pager Screen","Page number is $page")
    return when (page) {
        0 -> "Status"
        1 -> "Quest Log"
        //2 -> "Settings"
        else -> {
            Log.d("Main Pager Screen","Else statement for $page reached")
            "Solo Lvling"
        }
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