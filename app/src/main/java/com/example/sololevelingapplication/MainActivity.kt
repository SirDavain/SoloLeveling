package com.example.sololevelingapplication

//import android.R.attr.name
import android.R.attr.minHeight
import android.annotation.SuppressLint
import com.example.sololevelingapplication.statScreen.StatScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sololevelingapplication.NavRoutes
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import com.example.sololevelingapplication.ui.theme.SoloLevelingApplicationTheme
import com.example.sololevelingapplication.questlogscreen.QuestLogScreen
import androidx.navigation.compose.composable
import com.example.sololevelingapplication.settingsScreen.SettingsScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoloLevelingApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.STAT_SCREEN,
                ) {
                    composable(NavRoutes.STAT_SCREEN) {
                        StatScreen(navController = navController)
                    }
                    composable(NavRoutes.QUEST_LOG_SCREEN) {
                        QuestLogScreen(navController = navController)
                    }
                    composable(NavRoutes.SETTINGS_SCREEN) {
                        SettingsScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun NavBar() {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        //Icon(Icons.Rounded.Add)
    }
}

@Composable
fun InputField(
    str: String,
    onTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = str,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 48.dp)
                .testTag("input_text_field") // for testing purposes
        )
        Spacer(modifier = Modifier.width(30.dp))
        FloatingActionButton(
            modifier = Modifier
                .testTag("addEditFab"),
            onClick = { /*doSmth()*/ },
            shape = CircleShape,
        ) {
            Log.d("InputField", "FAB for adding something clicked")
            Icon(Icons.Filled.Add, contentDescription = "Add/edit")
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SoloLevelingApplicationTheme {

    }
}*/
