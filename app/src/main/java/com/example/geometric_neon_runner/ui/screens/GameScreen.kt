package com.example.geometric_neon_runner.ui.screens


import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.geometric_neon_runner.ui.theme.DarkBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch



class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var running = false
    private var thread: Thread? = null

    init {
        holder.addCallback(this)
    }

    fun startGameLoop() {
        running = true
        thread = Thread {
            while (running) {
                val canvas = holder.lockCanvas()
                try {
                    // desenhar no canvas (placeholder)
                    canvas?.drawColor(android.graphics.Color.BLACK)
                } finally {
                    if (canvas != null) holder.unlockCanvasAndPost(canvas)
                }
                try {
                    Thread.sleep(16) // ~60fps
                } catch (e: InterruptedException) {
                    running = false
                }
            }
        }
        thread?.start()
    }

    fun stopGameLoop() {
        running = false
        try {
            thread?.join(100)
        } catch (e: InterruptedException) {

        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startGameLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGameLoop()
    }
}


class GameViewModel {
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _elapsed = MutableStateFlow(0L) // seconds
    val elapsed: StateFlow<Long> = _elapsed

    fun start(mode: String) {

    }
}


@Composable
fun GameScreen(
        navController: NavController,
        mode: String,
        gameViewModel: GameViewModel = viewModel() as GameViewModel
) {
    val context = LocalContext.current
    val score by gameViewModel.score.collectAsState(initial = 0)
    val elapsed by gameViewModel.elapsed.collectAsState(initial = 0L)
    val coroutineScope = rememberCoroutineScope()

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
    ) {

        AndroidView(
                factory = {
                    GameView(it).apply {

                    }
                },
                modifier = Modifier.fillMaxSize()
        )


        Column(
                modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Mode: $mode", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Score: $score", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Time: ${elapsed}s", style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }


        IconButton(
                onClick = {

                },
                modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
        ) {
            Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White
            )
        }
    }


    LaunchedEffect(key1 = Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)

        }
    }
}
