package com.example.geometric_neon_runner.game.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.geometric_neon_runner.game.entities.Enemy
import com.example.geometric_neon_runner.game.entities.Player

class GameRenderer(
    var screenWidth: Int,
    var screenHeight: Int
) {

    private var gridOffsetY = 0f


    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x2200FFFF
        strokeWidth = 2f
        style = Paint.Style.STROKE
        setShadowLayer(10f, 0f, 0f, 0x4400FFFF)
    }

    private val lanePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x33FFFFFF.toInt()
        strokeWidth = 4f
        style = Paint.Style.STROKE
        setShadowLayer(14f, 0f, 0f, 0x2200FFFF)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 36f
    }

    private val tempRect = RectF()
    private val tempPath = Path()

    fun render(canvas: Canvas, player: Player, enemies: List<Enemy>) {

        drawBackground(canvas)

        drawLanes(canvas)

        drawEnemies(canvas, enemies)

        drawPlayer(canvas, player)

    }

    fun drawBackground(canvas: Canvas) {

        val spacing = 80f


        var offset = gridOffsetY % spacing
        if (offset < 0) offset += spacing


        var y = -spacing + offset
        while (y < screenHeight + spacing) {
            canvas.drawLine(0f, y, screenWidth.toFloat(), y, gridPaint)
            y += spacing
        }


        var x = 0f
        while (x <= screenWidth) {
            canvas.drawLine(x, 0f, x, screenHeight.toFloat(), gridPaint)
            x += spacing
        }
    }

    fun drawLanes(canvas: Canvas) {

        val leftSep = screenWidth * 0.375f
        val rightSep = screenWidth * 0.625f

        canvas.drawLine(leftSep, 0f, leftSep, screenHeight.toFloat(), lanePaint)
        canvas.drawLine(rightSep, 0f, rightSep, screenHeight.toFloat(), lanePaint)
    }

    fun drawPlayer(canvas: Canvas, player: Player) {
        player.draw(canvas)
    }

    fun drawEnemies(canvas: Canvas, enemies: List<Enemy>) {
        for (e in enemies) {

            if (e.isInDangerZone()) {
                e.setColor(0xFFFF5555.toInt())
            } else {
                e.setColor(0xFF00FFFF.toInt())
            }
            e.draw(canvas)
        }
    }

    fun update(deltaTime: Float) {

        val scrollSpeed = 120f
        gridOffsetY += scrollSpeed * deltaTime
    }
}