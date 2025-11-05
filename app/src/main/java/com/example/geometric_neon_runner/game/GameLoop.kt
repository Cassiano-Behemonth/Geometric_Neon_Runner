package com.example.geometric_neon_runner.game

import com.example.geometric_neon_runner.game.entities.*
import com.example.geometric_neon_runner.game.systems.*
import com.example.geometric_neon_runner.game.render.GameRenderer

class GameLoop(private val gameView: GameView) : Runnable {
    @Volatile
    private var running = false
    private var thread: Thread? = null

    fun start() {
        if (running) return
        running = true
        thread = Thread(this, "GameLoopThread")
        thread?.start()
    }

    fun stop() {
        running = false
        try {
            thread?.join(2000)
        } catch (e: InterruptedException) {
            // ignorar
        } finally {
            thread = null
        }
    }

    override fun run() {
        var lastTime = System.currentTimeMillis()
        while (running) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 1000f
            lastTime = currentTime

            try {
                gameView.update(deltaTime)
                gameView.render()
            } catch (t: Throwable) {

                t.printStackTrace()
            }


            try {
                Thread.sleep(16)
            } catch (ie: InterruptedException) {

            }
        }
    }
}