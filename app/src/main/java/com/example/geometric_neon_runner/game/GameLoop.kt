package com.example.geometric_neon_runner.game

import com.example.geometric_neon_runner.game.entities.*
import com.example.geometric_neon_runner.game.systems.*
import com.example.geometric_neon_runner.game.render.GameRenderer

class GameLoop(
    private val renderer: GameRenderer,
    private val screenWidth: Float,
    private val screenHeight: Float
) {
    private val player = Player(screenWidth / 2 - 50f, screenHeight - 200f, 100f, 100f, 15f)
    private val enemies = mutableListOf<Enemy>()
    private val background = Background(0f, -screenHeight, 5f, screenHeight)

    private val spawnSystem = SpawnSystem(screenWidth)
    private val collisionSystem = CollisionSystem()
    private val scoreSystem = ScoreSystem()

    var isRunning = true

    fun update() {
        if (!isRunning) return

        background.update()
        spawnSystem.update(enemies)
        enemies.forEach { it.update() }


        enemies.removeAll { it.isOffScreen(screenHeight) }


        if (collisionSystem.checkCollisions(player, enemies)) {
            isRunning = false
        } else {
            scoreSystem.add(1)
        }
    }

    fun draw(canvas: android.graphics.Canvas) {
        renderer.draw(canvas, player, enemies, background, scoreSystem.score)
    }

    fun movePlayer(dx: Float, dy: Float) {
        player.x += dx
        player.y += dy
    }

    fun reset() {
        enemies.clear()
        scoreSystem.reset()
        isRunning = true
    }
}