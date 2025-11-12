package com.example.geometric_neon_runner.game.systems

import com.example.geometric_neon_runner.game.entities.ENEMY_SIZE
import com.example.geometric_neon_runner.game.entities.Enemy
import com.example.geometric_neon_runner.game.entities.PLAYER_SIZE
import kotlin.math.hypot
import kotlin.random.Random


enum class SpawnMode { NORMAL, HARD, EXTREME }

class SpawnSystem(
    private val screenWidth: Int,
    private val screenHeight: Int,
    var mode: SpawnMode = SpawnMode.NORMAL
) {
    private val enemiesMutable = mutableListOf<Enemy>()
    private var spawnTimer = 0f

    var spawnInterval: Float = when (mode) {
        SpawnMode.NORMAL -> 1.1f
        SpawnMode.HARD -> 0.9f
        SpawnMode.EXTREME -> 0.7f
    }

    private val enemySpeed: Float
        get() = when (mode) {
            SpawnMode.NORMAL -> 400f
            SpawnMode.HARD -> 600f
            SpawnMode.EXTREME -> 800f
        }

    fun update(deltaTime: Float) {
        spawnTimer += deltaTime
        spawnInterval = when (mode) {
            SpawnMode.NORMAL -> 1.1f
            SpawnMode.HARD -> 0.9f
            SpawnMode.EXTREME -> 0.7f
        }
        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval
            spawnPattern()
        }

    }

    private fun spawnEnemy(lane: Int) {
        val enemy = Enemy(screenWidth, screenHeight, lane, startY = -ENEMY_SIZE, speed = enemySpeed)
        enemiesMutable.add(enemy)
    }

    private fun spawnPattern() {
        val r = Random.nextFloat()
        if (r <= 0.6f) {
            // single enemy
            val lane = Random.nextInt(0, 3)
            spawnEnemy(lane)
        } else {
            // two enemies, leaving 1 lane free
            val free = Random.nextInt(0, 3)
            for (lane in 0..2) {
                if (lane != free) spawnEnemy(lane)
            }
        }
    }

    fun getEnemies(): List<Enemy> = enemiesMutable

    fun removeEnemy(enemy: Enemy) {
        enemiesMutable.remove(enemy)
    }

    fun clear() {
        enemiesMutable.clear()
        spawnTimer = 0f
    }
}

class CollisionSystem {

    fun checkCollision(playerX: Float, playerY: Float, enemy: Enemy, threshold: Float = 45f): Boolean {
        val dx = enemy.x - playerX
        val dy = enemy.y - playerY
        val dist = hypot(dx.toDouble(), dy.toDouble())
        return dist < threshold
    }
}

class ScoreSystem {
    var score: Int = 0
        private set
    private var timeSeconds: Float = 0f
    val pointsPerSecond = 15f

    // Método que retorna a pontuação

    fun update(deltaTime: Float) {
        timeSeconds += deltaTime
        score = (timeSeconds * pointsPerSecond).toInt()
    }

    // Retorna o tempo em segundos
    fun getTime(): Int = timeSeconds.toInt()

    // Formata o tempo para minutos e segundos
    fun getFormattedTime(): String {
        val total = getTime()
        val mm = total / 60
        val ss = total % 60
        return String.format("%02d:%02d", mm, ss)
    }

    // Reseta o sistema de pontuação
    fun reset() {
        timeSeconds = 0f
        score = 0
    }
}