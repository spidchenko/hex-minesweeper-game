package d.spidchenko.canvasgame

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.roundToInt
import kotlin.math.sqrt


class GameEngine(context: Context) : SurfaceView(context), Runnable {
    private val surfaceHolder: SurfaceHolder = holder
    private val paddingSize = 100

    private val soundEngine = SoundEngine(context)
    private var gameState = GameState(this, soundEngine)
    private var firstTime = true
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var cellWidth: Int = 0
    private var cellHeight: Int = 0
    private lateinit var gameThread: Thread
    private var canvas: Canvas? = null

    override fun run() {
        while (gameState.isPlaying) {
            // TODO add pause game logic
            draw()
            gameState.handleClicks()
            waitSomeTime()
        }
    }

    fun pause() {
        Log.d(TAG, "pause")
        gameState.isPlaying = false
        gameThread.join()
    }

    fun resume() {
        Log.d(TAG, "resume")
        gameState.isPlaying = true
        // TODO Need to use Kotlin coroutines instead of Threads
        gameThread = Thread(this)
        gameThread.start()
    }

    fun convertDpToPixel(dp: Float): Float {
        val resources: Resources? = context.resources
        val metrics: DisplayMetrics? = resources?.displayMetrics
        return dp * (metrics?.densityDpi?.div(160f)!!)
    }

    private fun init() {
        canvasWidth = surfaceHolder.surfaceFrame.width()
        canvasHeight = surfaceHolder.surfaceFrame.height()
        canvasCenter = PointF(
            surfaceHolder.surfaceFrame.exactCenterX(),
            surfaceHolder.surfaceFrame.exactCenterY()
        )

        Log.d(
            TAG,
            "init: center = ${canvasCenter.x} ${canvasCenter.y}. Dimensions: $canvasWidth $canvasHeight"
        )

        cellWidth = (Cell.HEX_SIZE * sqrt(3.0)).roundToInt()
        cellHeight = (1.5 * Cell.HEX_SIZE).roundToInt()

        val numRows = (canvasWidth - 2 * paddingSize) / cellWidth
        val numColumns = (canvasHeight - 2 * paddingSize) / cellHeight
        Log.d(TAG, "init: numRows = $numRows")
        Log.d(TAG, "init: numColumns = $numColumns")
        fillWithHexagons()
        //TODO set mines after first turn
        gameState.setMines(GameState.Difficulty.HARD)
    }

    private fun fillWithHexagons() {
        for (q in -ROWS / 2..ROWS / 2) for (r in -CELLS_IN_A_ROW / 2..CELLS_IN_A_ROW / 2) {
            //check if visible
            val newXCord =
                canvasCenter.x + Cell.HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)
            if (newXCord > paddingSize && newXCord < canvasWidth - paddingSize) {
                gameState.cells.add(Cell(q.toByte(), r.toByte()))
            }
        }
    }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {
            if (firstTime) {
                init()
                firstTime = false
            }
            canvas = surfaceHolder.lockCanvas()
            gameState.drawField(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun waitSomeTime() {
        try {
            Thread.sleep(SLEEP_MILLS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "GameView.LOG_TAG"
        private const val CELLS_IN_A_ROW = 11
        private const val ROWS = 13
        private const val SLEEP_MILLS = 17L
        lateinit var canvasCenter: PointF
    }

//    init {
//        gameThread = Thread(this)
//        gameThread?.start()
//    }
}