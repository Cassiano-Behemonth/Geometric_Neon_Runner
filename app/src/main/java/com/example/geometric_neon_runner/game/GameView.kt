package com.example.geometric_neon_runner.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.geometric_neon_runner.game.entities.Enemy
import com.example.geometric_neon_runner.game.entities.Player
import com.example.geometric_neon_runner.game.systems.CollisionSystem
import com.example.geometric_neon_runner.game.systems.ScoreSystem
import com.example.geometric_neon_runner.game.systems.SpawnMode
import com.example.geometric_neon_runner.game.systems.SpawnSystem
import kotlin.math.abs


class GameView(context: Context, private val mode: SpawnMode = SpawnMode.NORMAL) :
    SurfaceView(context), SurfaceHolder.Callback {

    // Systems & entities
    private lateinit var player: Player
    private lateinit var spawnSystem: SpawnSystem
    private val enemies = mutableListOf<Enemy>()
    private val collisionSystem = CollisionSystem()
    private val scoreSystem = ScoreSystem()
    private lateinit var renderer: GameRenderer
    private lateinit var gameLoop: GameLoop

    var gameState: GameState = GameState.Playing

    // Callbacks
    var onGameOver: ((score: Int, time: Int) -> Unit)? = null
    var onScoreChanged: ((score: Int) -> Unit)? = null

    // touch handling
    private var downX = 0f
    private var downY = 0f
    private var isDragging = false

    init {
        holder.addCallback(this)
        // don't start heavy initialization until surfaceCreated (need width/height)
    }

    private fun initSystems() {
        val w = width.takeIf { it > 0 } ?: resources.displayMetrics.widthPixels
        val h = height.takeIf { it > 0 } ?: resources.displayMetrics.heightPixels

        player = Player(w, h, initialLane = 1)
        spawnSystem = SpawnSystem(w, h, mode)
        renderer = GameRenderer(w, h)
        scoreSystem.reset()
        enemies.clear()

        // create GameLoop (needs this GameView)
        gameLoop = GameLoop(this)
    }

    fun startGame() {
        if (!::player.isInitialized) initSystems()
        gameState = GameState.Playing
        gameLoop.start()
    }

    fun stopGame() {
        gameLoop.stop()
    }

    // Update é chamado no loop
    fun update(deltaTime: Float) {
        if (gameState != GameState.Playing) return

        // update player
        player.update(deltaTime)

        // spawn system (populate spawnSystem internal list)
        spawnSystem.update(deltaTime)

        // move enemies list from spawnSystem into our local list, and update them
        // For performance, reuse same enemy objects - spawnSystem holds them as well.
        // We'll sync references
        val spawned = spawnSystem.getEnemies()
        // ensure we reference same list
        synchronized(enemies) {
            // add any new enemies that are not present
            for (s in spawned) {
                if (!enemies.contains(s)) enemies.add(s)
            }
            // update each enemy
            val iter = enemies.iterator()
            while (iter.hasNext()) {
                val e = iter.next()
                e.update(deltaTime)
                if (e.isOffScreen()) {
                    iter.remove()
                    spawnSystem.getEnemies().forEach { } // no-op to avoid new allocations
                }
            }
        }

        // update renderer (background)
        renderer.update(deltaTime)

        // update score
        scoreSystem.update(deltaTime)
        onScoreChanged?.invoke(scoreSystem.getScore())

        // check collisions
        checkCollisions()
    }

    fun render() {
        val canvas: Canvas? = holder.lockCanvas()
        if (canvas != null) {
            try {
                // clear screen black
                canvas.drawRGB(0, 0, 0)

                // render all
                renderer.render(canvas, player, synchronizedListCopy())
                // draw HUD (score/time) - simple
                val paint = renderer // reuse textPaint inside renderer? access not exposed; quick draw below is fine:
                // We'll draw score/time using renderer's textPaint via reflection avoided; instead draw basic text:
                val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFFFFFFF.toInt()
                    textSize = 40f
                }
                canvas.drawText("Score: ${scoreSystem.getScore()}", 20f, 50f, textPaint)
                canvas.drawText("Time: ${scoreSystem.getFormattedTime()}", 20f, 100f, textPaint)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun synchronizedListCopy(): List<Enemy> {
        synchronized(enemies) {
            return enemies.toList()
        }
    }

    // INPUT
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                isDragging = true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - downX
                val dy = event.y - downY
                if (isDragging && kotlin.math.abs(dx) > 40 && kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                    // horizontal swipe
                    handleInput(downX, dx)
                    isDragging = false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    val dx = event.x - downX
                    // treat as tap if small movement
                    if (abs(dx) < 20) {
                        handleTap(event.x)
                    } else {
                        handleInput(downX, dx)
                    }
                }
                isDragging = false
            }
        }
        return true
    }

    private fun handleTap(x: Float) {
        // 3 tap zones: left, middle, right
        val third = width / 3f
        val lane = when {
            x < third -> 0
            x < third * 2 -> 1
            else -> 2
        }
        player.moveToLane(lane)
    }

    private fun handleInput(startX: Float, deltaX: Float) {
        // swipe left/right logic: if deltaX < 0 => move left, >0 move right
        if (deltaX < 0) {
            // left swipe
            player.moveToLane(player.currentLane - 1)
        } else {
            // right swipe
            player.moveToLane(player.currentLane + 1)
        }
    }

    private fun checkCollisions() {
        val plX = player.x
        val plY = player.y
        val threshold = 45f

        synchronized(enemies) {
            val iter = enemies.iterator()
            while (iter.hasNext()) {
                val e = iter.next()
                if (collisionSystem.checkCollision(plX, plY, e, threshold)) {
                    // Game Over
                    gameState = GameState.GameOver
                    // stop the loop safely
                    onGameOver?.invoke(scoreSystem.getScore(), scoreSystem.getTime())
                    // stop game loop
                    gameLoop.stop()
                    break
                }
            }
        }
    }

    // SurfaceHolder.Callback
    override fun surfaceCreated(holder: SurfaceHolder) {
        // initialize and start game
        if (!::player.isInitialized) initSystems()
        // start loop
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // update sizes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // stop loop
        gameLoop.stop()
    }
}