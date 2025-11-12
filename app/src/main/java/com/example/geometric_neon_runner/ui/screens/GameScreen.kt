package com.example.geometric_neon_runner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.geometric_neon_runner.data.model.GameMode
import com.example.geometric_neon_runner.game.GameView
import com.example.geometric_neon_runner.game.systems.SpawnMode
import com.example.geometric_neon_runner.ui.color.DarkBackground
import com.example.geometric_neon_runner.ui.navigation.Screen
import com.example.geometric_neon_runner.ui.viewmodels.GameViewModel

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel,
    mode: String
) {
    val context = LocalContext.current
    val currentScore by viewModel.currentScore.collectAsState()
    val shouldNavigateToGameOver by viewModel.shouldNavigateToGameOver.collectAsState()

    // Convert string mode to GameMode enum
    val gameMode = GameMode.fromName(mode)

    // Convert GameMode to SpawnMode for GameView
    val spawnMode = when (gameMode) {
        GameMode.NORMAL -> SpawnMode.NORMAL
        GameMode.HARD -> SpawnMode.HARD
        GameMode.EXTREME -> SpawnMode.EXTREME
    }

    // Set game mode in ViewModel
    LaunchedEffect(gameMode) {
        viewModel.setGameMode(gameMode)
    }

    // Handle navigation to GameOver
    LaunchedEffect(shouldNavigateToGameOver) {
        if (shouldNavigateToGameOver) {
            navController.navigate(
                Screen.GameOver.createRoute(
                    viewModel.finalScore,
                    viewModel.finalTime,
                    gameMode.name
                )
            ) {
                popUpTo(Screen.Game.createRoute(mode)) { inclusive = true }
            }
            viewModel.onNavigatedToGameOver()
        }
    }

    var gameView by remember { mutableStateOf<GameView?>(null) }
    var elapsedTime by remember { mutableStateOf(0) }

    // Timer para atualizar o tempo a cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedTime++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Game View
        AndroidView(
            factory = { ctx ->
                GameView(ctx, spawnMode).apply {
                    gameView = this

                    // Set callbacks
                    onGameOver = { score, time ->
                        viewModel.onGameOver(score, time)
                    }

                    onScoreChanged = { score ->
                        viewModel.updateScore(score)
                    }

                    // Start the game
                    startGame()
                }
            },
            modifier = Modifier.fillMaxSize(),
            onRelease = { view ->
                view.stopGame()
            }
        )

        // HUD Overlay - Card no canto superior esquerdo
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mode
                Text(
                    text = gameMode.displayName.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(android.graphics.Color.parseColor(gameMode.color)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                // Score
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$currentScore",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = formatTime(elapsedTime),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}