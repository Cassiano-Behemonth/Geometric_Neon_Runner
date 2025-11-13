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
        var lastTime = System.nanoTime() // MUDADO para nanoTime (mais preciso)
        while (running) {
            val currentTime = System.nanoTime()
            var deltaTime = (currentTime - lastTime) / 1_000_000_000f // nanosegundos para segundos
            lastTime = currentTime

            // CORREÇÃO DO LAG: Limita o deltaTime de forma mais suave
            // Evita "pulos" mas permite compensação gradual
            if (deltaTime > 0.033f) { // Máximo de 33ms (30 FPS mínimo)
                deltaTime = 0.033f
            }

            // Ignora frames muito pequenos (podem causar instabilidade)
            if (deltaTime < 0.001f) {
                deltaTime = 0.001f
            }

            try {
                gameView.update(deltaTime)
                gameView.render()
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            // Tenta manter ~60 FPS com sleep mais preciso
            val frameTime = (System.nanoTime() - currentTime) / 1_000_000 // em ms
            val targetFrameTime = 16L // ~60 FPS
            val sleepTime = targetFrameTime - frameTime

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime)
                } catch (ie: InterruptedException) {
                    // ignorar
                }
            }
        }
    }
}