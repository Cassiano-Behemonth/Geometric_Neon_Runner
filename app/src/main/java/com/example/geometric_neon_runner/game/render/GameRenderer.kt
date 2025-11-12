package com.example.geometric_neon_runner.game.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.geometric_neon_runner.game.entities.Enemy
import com.example.geometric_neon_runner.game.entities.EnemyShape
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

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 36f
    }

    private val tempRect = RectF()
    private val tempPath = Path()

    fun render(canvas: Canvas, player: Player, enemies: List<Enemy>) {
        drawGradientLines(canvas)
        drawLanes(canvas)
        drawEnemies(canvas, enemies)
        drawPlayer(canvas, player)
    }

    private fun drawGradientLines(canvas: Canvas) {
        // Gradiente simples (preto -> azul escuro)
        canvas.drawRGB(0, 0, 10)

        val spacing = 150f
        var offset = gridOffsetY % spacing
        if (offset < 0) offset += spacing

        var y = -spacing + offset
        var lineCount = 0

        while (y < screenHeight + spacing) {
            // Alterna entre duas cores
            linePaint.color = if (lineCount % 2 == 0) 0x2200FFFF else 0x2200FF88
            linePaint.strokeWidth = 1.5f

            canvas.drawLine(0f, y, screenWidth.toFloat(), y, linePaint)
            y += spacing
            lineCount++
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

    // MÉTODO ATUALIZADO - cores diferentes por forma
    fun drawEnemies(canvas: Canvas, enemies: List<Enemy>) {
        for (e in enemies) {
            // Escolhe cor base pela forma do inimigo
            val baseColor = when (e.shape) {
                EnemyShape.TRIANGLE -> 0xFF00FFFF.toInt()  // Ciano (original)
                EnemyShape.SQUARE -> 0xFFFF00FF.toInt()    // Magenta
                EnemyShape.CIRCLE -> 0xFF00FF88.toInt()    // Verde neon
                EnemyShape.HEXAGON -> 0xFFFFAA00.toInt()   // Laranja
                EnemyShape.DIAMOND -> 0xFFFF0088.toInt()   // Rosa
            }

            // Se estiver na zona de perigo, muda para vermelho
            if (e.isInDangerZone()) {
                e.setColor(0xFFFF5555.toInt())
            } else {
                e.setColor(baseColor)
            }

            e.draw(canvas)
        }
    }

    fun update(deltaTime: Float) {
        val scrollSpeed = 120f
        gridOffsetY += scrollSpeed * deltaTime
    }
}