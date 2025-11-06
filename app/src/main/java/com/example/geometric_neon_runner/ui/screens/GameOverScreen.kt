package com.example.geometric_neon_runner.ui.screens


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.geometric_neon_runner.ui.components.NeonButton
import com.example.geometric_neon_runner.ui.theme.DarkBackground
import kotlinx.coroutines.delay



@Composable
fun GameOverScreen(
        navController: NavController,
        score: Int,
        timeSeconds: Int,
        mode: String
) {
    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(24.dp),
            contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            NeonGameOverText()

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Score: $score", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                    Text(text = "Time: ${timeSeconds}s", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    Text(text = "Mode: $mode", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            NeonButton(text = "PLAY AGAIN") {
                navController.navigate("game/$mode") {
                    popUpTo("game") { inclusive = true }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeonButton(text = "MENU") {
                navController.navigate("menu") {
                    popUpTo("menu") { inclusive = true }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeonButton(text = "RANKING") {
                navController.navigate("ranking/$mode")
            }
        }
    }
}

@Composable
private fun NeonGameOverText() {

    val infiniteTransition = rememberInfiniteTransition()
    val glow by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
            )
    )

    Text(
            text = "GAME OVER",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
            modifier = Modifier
                    .padding(8.dp)
                    .drawBehind {
                        // Glow effect: draw multiple stroked text-like rectangles as a faux glow
                        // (visual effect — for production, use shadow or layered composables)
                        drawRect(color = Color(0xFFFF00FF).copy(alpha = 0.06f * glow), size = this.size)
                    }
    )
}
