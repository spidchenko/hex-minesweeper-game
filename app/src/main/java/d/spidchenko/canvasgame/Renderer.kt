package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceView
import d.spidchenko.canvasgame.particles.ParticleSystem

class Renderer(
    surfaceView: SurfaceView
) {
    private lateinit var canvas: Canvas
    private val surfaceHolder = surfaceView.holder
    private val paint = Paint()


    fun draw(
        fps: Long,
        gameState: GameState,
        particleSystem: ParticleSystem,
    ) {
        if (surfaceHolder.surface.isValid) {
            if (!gameState.isInitialized) {
                gameState.initLevel(
                    surfaceHolder.surfaceFrame.width(),
                    surfaceHolder.surfaceFrame.height()
                )
            }
            canvas = surfaceHolder.lockCanvas()
            canvas.drawColor(Color.BLACK)

            if (gameState.isDrawing) {
                canvas.drawColor(Color.BLACK)

                gameState.cells.forEach {
                    it.draw(
                        canvas,
                        paint,
                        gameState.textSize,
                        particleSystem
                    )
                }

                gameState.activeCell?.let {
                    it.draw(
                        canvas,
                        paint,
                        gameState.textSize,
                        particleSystem,
                        Cell.CellOutline.BOLD
                    )
                    gameState.activeCell = null
                }
            }

            if (gameState.isGameOver) {
                //
            }

            if (particleSystem.isRunning) {
                particleSystem.draw(canvas, paint)
                particleSystem.update(fps)
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    companion object {
        private const val TAG = "Renderer.LOG_TAG"
    }
}