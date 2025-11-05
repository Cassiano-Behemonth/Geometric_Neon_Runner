package com.example.geometric_neon_runner.game.entities

import android.graphics.RectF

data class Enemy(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var speed: Float
) {
    fun update() {
        y += speed
    }

    fun isOffScreen(screenHeight: Float): Boolean {
        return y > screenHeight
    }

    fun getBounds() = RectF(x, y, x + width, y + height)
}