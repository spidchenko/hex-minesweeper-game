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
//            canvas.drawColor(Color.BLACK)

            if (gameState.isDrawing) {
                canvas.drawColor(Color.BLACK)

                gameState.cells.forEach {
                    it.draw(
                        canvas,
                        paint,
                        gameState.textSize,
                    )
                }

                gameState.activeCell?.let {
                    it.draw(
                        canvas,
                        paint,
                        gameState.textSize,
                        Cell.CellOutline.BOLD
                    )
                    gameState.activeCell = null
                }
            }

            if (gameState.isGameOver) {
                // TODO Make separate function for this logic
                if (alpha < 200) alpha+=10
                paint.color = Color.argb(alpha, 0,0,0)
                canvas.drawRect(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), paint)
                paint.textSize = 150F
                paint.color = Color.argb(alpha, 255,0,0)
                // TODO Use res. string here
                canvas.drawText("Game Over", canvas.width/ 2F, canvas.height / 2F, paint)
            }

            if (particleSystem.isRunning) {
                particleSystem.draw(canvas, paint)
                particleSystem.update(fps)
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    companion object {
        var alpha = 0
        private const val TAG = "Renderer.LOG_TAG"
    }
}