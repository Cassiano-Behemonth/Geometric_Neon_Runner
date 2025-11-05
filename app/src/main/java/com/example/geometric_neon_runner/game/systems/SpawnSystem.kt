package com.example.geometric_neon_runner.game.systems

import com.example.geometric_neon_runner.game.entities.Enemy
import kotlin.random.Random

class SpawnSystem(
    private val screenWidth: Float
) {
    private var spawnTimer = 0
    private val spawnInterval = 50

    fun update(enemies: MutableList<Enemy>) {
        spawnTimer++
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0
            val enemyX = Random.nextFloat() * (screenWidth - 100f)
            enemies.add(Enemy(enemyX, -100f, 100f, 100f, 8f))
        }
    }
}