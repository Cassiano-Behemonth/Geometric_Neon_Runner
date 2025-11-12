package com.example.geometric_neon_runner.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.geometric_neon_runner.data.model.GameMode
import com.example.geometric_neon_runner.game.GameView
import com.example.geometric_neon_runner.game.systems.SpawnMode
import com.example.geometric_neon_runner.ui.color.DarkBackground
import com.example.geometric_neon_runner.ui.navigation.Screen
import com.example.geometric_neon_runner.ui.viewmodels.GameViewModel
import kotlinx.coroutines.delay

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

        // HUD Overlay
        Column(
                modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
        ) {
            Text(
                    text = "Mode: ${gameMode.displayName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Score: $currentScore",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
            )
        }

        // Pause button
        IconButton(
                onClick = {
                    viewModel.pauseGame()
                    // TODO: Show pause dialog
                },
                modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
        ) {

        }
    }
}