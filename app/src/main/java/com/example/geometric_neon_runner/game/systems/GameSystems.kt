package com.example.geometric_neon_runner.game.systems

import com.example.geometric_neon_runner.game.entities.ENEMY_SIZE
import com.example.geometric_neon_runner.game.entities.Enemy
import com.example.geometric_neon_runner.game.entities.EnemyShape
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
    private var elapsedTime = 0f
    private var currentSpeed = 0f // Cache da velocidade atual

    var spawnInterval: Float = when (mode) {
        SpawnMode.NORMAL -> 1.0f
        SpawnMode.HARD -> 0.8f
        SpawnMode.EXTREME -> 0.6f
    }

    // Velocidades base
    private val baseSpeed: Float
        get() = when (mode) {
            SpawnMode.NORMAL -> 1000f
            SpawnMode.HARD -> 1500f
            SpawnMode.EXTREME -> 1900f
        }

    // Velocidade máxima
    private val maxSpeed: Float
        get() = when (mode) {
            SpawnMode.NORMAL -> 1800f
            SpawnMode.HARD -> 2500f
            SpawnMode.EXTREME -> 3200f
        }

    init {
        currentSpeed = baseSpeed // Inicializa com velocidade base
    }

    // Atualiza a velocidade a cada segundo (não a cada frame)
    private var speedUpdateTimer = 0f

    fun update(deltaTime: Float) {
        elapsedTime += deltaTime
        spawnTimer += deltaTime
        speedUpdateTimer += deltaTime

        // Atualiza velocidade apenas 1x por segundo (otimização)
        if (speedUpdateTimer >= 1f) {
            speedUpdateTimer = 0f
            val speedIncrease = (elapsedTime / 30f) * (baseSpeed * 0.1f)
            currentSpeed = (baseSpeed + speedIncrease).coerceAtMost(maxSpeed)
        }

        // Ajusta o intervalo de spawn
        spawnInterval = when (mode) {
            SpawnMode.NORMAL -> (1.0f - (elapsedTime / 120f) * 0.3f).coerceAtLeast(0.7f)
            SpawnMode.HARD -> (0.8f - (elapsedTime / 120f) * 0.2f).coerceAtLeast(0.6f)
            SpawnMode.EXTREME -> (0.6f - (elapsedTime / 120f) * 0.15f).coerceAtLeast(0.45f)
        }

        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval
            spawnPattern()
        }
    }

    private fun spawnEnemy(lane: Int) {
        val randomShape = EnemyShape.values().random()

        val enemy = Enemy(
            screenWidth,
            screenHeight,
            lane,
            startY = -ENEMY_SIZE,
            speed = currentSpeed, // Usa velocidade em cache
            shape = randomShape
        )
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
        elapsedTime = 0f
        speedUpdateTimer = 0f
        currentSpeed = baseSpeed
    }

    fun getDebugInfo(): String {
        return "Speed: ${currentSpeed.toInt()} | Interval: ${"%.2f".format(spawnInterval)}s"
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

    fun update(deltaTime: Float) {
        timeSeconds += deltaTime
        score = (timeSeconds * pointsPerSecond).toInt()
    }

    fun getTime(): Int = timeSeconds.toInt()

    fun getFormattedTime(): String {
        val total = getTime()
        val mm = total / 60
        val ss = total % 60
        return String.format("%02d:%02d", mm, ss)
    }

    fun reset() {
        timeSeconds = 0f
        score = 0
    }
}