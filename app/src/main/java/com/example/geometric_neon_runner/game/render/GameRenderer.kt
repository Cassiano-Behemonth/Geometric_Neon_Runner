package com.example.geometric_neon_runner.game.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.geometric_neon_runner.game.entities.*

class GameRenderer {

    private val paint = Paint()

    fun draw(canvas: Canvas, player: Player, enemies: List<Enemy>, background: Background, score: Int) {

        canvas.drawColor(Color.BLACK)


        paint.color = Color.GREEN
        canvas.drawRect(player.getBounds(), paint)


        paint.color = Color.RED
        enemies.forEach { canvas.drawRect(it.getBounds(), paint) }


        paint.color = Color.WHITE
        paint.textSize = 48f
        canvas.drawText("Score: $score", 50f, 80f, paint)
    }
}