package com.example.sololevelingapplication.statScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sololevelingapplication.InputField
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatScreen(
    navController: NavController,
    viewModel: StatScreenViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Status")
                }
            )
        },
        /*bottomBar = {
            Icon(navBarIcons)
        }*/
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row() {
                Text("STR: 555")
                Spacer(modifier = Modifier.width(16.dp))
                Text("AGI: 555")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row() {
                Text("PER: 555")
                Spacer(modifier = Modifier.width(16.dp))
                Text("VIT: 555")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row() {
                Text("INT: 555")
                Spacer(modifier = Modifier.width(16.dp))
                Text("Available \n Ability \n Points")
                Spacer(modifier = Modifier.width(16.dp))
                Text("5")
            }

        }
    }
}