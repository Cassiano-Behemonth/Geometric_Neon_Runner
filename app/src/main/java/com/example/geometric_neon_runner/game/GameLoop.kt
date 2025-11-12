package com.example.geometric_neon_runner.game

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
            var deltaTime = (currentTime - lastTime) / 1000f
            lastTime = currentTime

            // CORREÇÃO DO LAG: Limita o deltaTime máximo
            // Se o frame demorar muito (lag), não deixa os objetos "pularem"
            if (deltaTime > 0.05f) { // Máximo de 50ms (20 FPS mínimo)
                deltaTime = 0.05f
            }

            try {
                gameView.update(deltaTime)
                gameView.render()
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            // Tenta manter ~60 FPS
            try {
                Thread.sleep(16)
            } catch (ie: InterruptedException) {
                // ignorar
            }
        }
    }
}