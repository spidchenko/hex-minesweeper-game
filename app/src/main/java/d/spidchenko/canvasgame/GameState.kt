package d.spidchenko.canvasgame

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import d.spidchenko.canvasgame.particles.ParticleSystem

class GameState(
    private val context: Context,
    private val gameStarter: GameStarter,
) {
    enum class Difficulty(val numberOfMines: Int) {
        EASY(5), MEDIUM(10), HARD(20)
    }

    var isThreadRunning = false
        private set
    var isPaused = true
        private set
    var isGameOver = true
        private set
    var isGameWon = true
        private set
    var isDrawing = false
        private set

    var isInitialized = false
        private set

    var activeCell: Cell? = null

    val textSize = convertDpToPixel(28f)

    val cells = mutableListOf<Cell>()
    val cellsWithBombs = mutableListOf<Cell>()


    fun initLevel(screenX: Int, screenY: Int) {
        gameStarter.initLevel(screenX, screenY)
        isInitialized = true
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isGameOver = false
        isGameWon = false
        isPaused = false
    }

    fun stopEverything() {
        isPaused = true
        isGameOver = true
        isThreadRunning = false
    }

    fun startThread() {
        isThreadRunning = true
    }

    fun startDrawing() {
        isDrawing = true
    }

    fun stopDrawing() {
        isDrawing = false
    }

    fun startNewGame() {
        stopDrawing()
        resume()
        startDrawing()
    }


    fun checkGameOverState(soundEngine: SoundEngine, particleSystem: ParticleSystem) {
        if (!isGameOver || !isGameWon) {
            when {
                checkGameWin() -> {
                    isGameWon = true
                    isThreadRunning = false
                    // TODO happy music here
                    Log.d(TAG, "YOU WON")
                }

                chekGameLost() -> {
                    isGameOver = true
                    isPaused = true
                    val explodedCell = cellsWithBombs.find { it.isUncovered }
                    soundEngine.playExplosion()
                    explodedCell?.let { particleSystem.emmitParticles(it.centerPoint) }
                    Log.d(TAG, "GAME OVER")
                }
            }
        }
    }


    private fun checkGameWin(): Boolean {
//        if (cellsWithBombs.size > 0) {
        val totalBombsFound = cellsWithBombs.count(Cell::isFlagged)
        return (totalBombsFound == cellsWithBombs.size)
//        }
    }

    private fun chekGameLost(): Boolean = cellsWithBombs.any { it.isUncovered }

    private fun convertDpToPixel(dp: Float): Float {
        val resources: Resources? = context.resources
        val metrics: DisplayMetrics? = resources?.displayMetrics
        return dp * (metrics?.densityDpi?.div(160f)!!)
    }

    companion object {
        private const val TAG = "Game.LOG_TAG"
        private const val HUE_COMPONENT = 0
    }
}