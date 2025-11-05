package com.example.geometric_neon_runner.game

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.geometric_neon_runner.game.render.GameRenderer

class GameView(context: Context, private val screenWidth: Float, private val screenHeight: Float) :
    SurfaceView(context), SurfaceHolder.Callback {

    private val renderer = GameRenderer()
    private val gameLoop = GameLoop(renderer, screenWidth, screenHeight)
    private var gameThread: Thread? = null

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop.isRunning = true
        gameThread = Thread {
            while (gameLoop.isRunning) {
                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    gameLoop.update()
                    gameLoop.draw(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
                Thread.sleep(16) // ~60 FPS
            }
        }
        gameThread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.isRunning = false
        gameThread?.join()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
}