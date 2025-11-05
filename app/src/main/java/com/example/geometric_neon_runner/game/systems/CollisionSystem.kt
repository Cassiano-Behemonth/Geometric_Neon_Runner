package com.example.geometric_neon_runner.game.systems

import com.example.geometric_neon_runner.game.entities.Player
import com.example.geometric_neon_runner.game.entities.Enemy
import android.graphics.RectF

class CollisionSystem {
    fun checkCollisions(player: Player, enemies: MutableList<Enemy>): Boolean {
        val playerRect = player.getBounds()
        for (enemy in enemies) {
            if (RectF.intersects(playerRect, enemy.getBounds())) {
                return true
            }
        }
        return false
    }
}