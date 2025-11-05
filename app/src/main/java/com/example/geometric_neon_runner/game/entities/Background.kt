package com.example.geometric_neon_runner.game.entities

data class Background(
    var y1: Float,
    var y2: Float,
    var speed: Float,
    val screenHeight: Float
) {
    fun update() {
        y1 += speed
        y2 += speed

        if (y1 > screenHeight) y1 = y2 - screenHeight
        if (y2 > screenHeight) y2 = y1 - screenHeight
    }
}