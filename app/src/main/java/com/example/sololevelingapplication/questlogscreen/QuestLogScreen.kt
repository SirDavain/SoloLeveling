package com.example.sololevelingapplication.questlogscreen

import android.R.attr.bottom
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// This is where the magic happens:
// Displays your current "quests" (habits and routines you set yourself)
// Here you can tick them off and receive XP for it or establish new ones using a FAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestLogScreen(
    navController: NavController,
    viewModel: QuestLogViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status") }
            )
        },
        bottomBar = {
            // TODO: add bottomNavIndicators
            FloatingActionButton(
                onClick = { /*launchAFunction()*/ },
                shape = CircleShape
            ) {
                Log.d("QuestLogScreen", "FAB for adding a habit/routine clicked")
                Icon(Icons.Filled.Add, contentDescription = "Add new habit/routine")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("You don't have any quests yet")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}