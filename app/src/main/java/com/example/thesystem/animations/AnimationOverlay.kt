package com.example.thesystem.animations

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesystem.Overlay

@Composable
fun LevelUpAnimationOverlay(
    visible: Boolean,
    info: Overlay.LevelUp?,
    onDismiss: () -> Unit
) {
    Log.d("AnimationOverlay", "LevelUpOverlay should be showing up.")
    if (info == null) return

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss),
                //.wrapContentSize(Alignment.Center)
                //.padding(16.dp)
            contentAlignment = Alignment.Center
        ) {
            ParticleBurstOverlay(isVisible = visible)
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {}
                    .animatedCardEdgeGlow(
                        isVisible = visible,
                        glowColor = MaterialTheme.colorScheme.primary,
                        glowWidth = 30.dp,
                        cornerRadius = 16.dp
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LEVEL UP!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "You are now Level ${info.newLevel}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onDismiss) {
                        Text("CONTINUE")
                    }
                }
            }
        }
    }
}

@Composable
fun QuestFailedAnimationOverlay(
    visible: Boolean,
    info: Overlay.QuestFailed?,
    onDismiss: () -> Unit
) {
    if (info == null) return

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {},
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "QUEST FAILED",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "You failed the quest \n\"${info.questText}\"",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    // Add a penalty for failing the quest
                    Text(
                        text = "Penalty: You lost ${info.penaltyXp}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onDismiss) {
                        Text("CONTINUE")
                    }
                }
            }
        }
    }
}

@Composable
fun QuestCompletedAnimationOverlay(
    visible: Boolean,
    info: Overlay.QuestCompleted?,
    onDismiss: () -> Unit
) {
    Log.d("AnimationOverlay", "QuestCompletedOverlay should be showing up.")
    if (info == null) return

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {},
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Optional: Add an image/icon
                    // Image(painter = painterResource(id = R.drawable.ic_level_up_star), contentDescription = "Level Up")
                    Text(
                        text = "QUEST SUCCESS",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "You beat the quest: \n\"${info.questText}\"",
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    // Add a reward for beating the quest
                    Text(
                        text = "Reward: You gained ${info.gainedXp} XP",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onDismiss) {
                        Text("CONTINUE")
                    }
                }
            }
        }
    }
}