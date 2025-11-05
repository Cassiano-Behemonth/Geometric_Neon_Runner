package com.example.geometric_neon_runner.game.systems

class ScoreSystem {
    var score = 0
        private set

    fun add(points: Int) {
        score += points
    }

    fun reset() {
        score = 0
    }
}