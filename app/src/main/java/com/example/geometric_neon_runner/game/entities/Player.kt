package com.example.geometric_neon_runner.game.entities

import android.graphics.RectF

data class Player(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var speed: Float,
    var isAlive: Boolean = true
) {
    fun moveLeft() {
        x -= speed
    }

    fun moveRight() {
        x += speed
    }

    fun moveUp() {
        y -= speed
    }

    fun moveDown() {
        y += speed
    }

    fun getBounds() = RectF(x, y, x + width, y + height)
}