package d.spidchenko.canvasgame

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.roundToInt
import kotlin.math.sqrt


class GameView(context: Context?) : SurfaceView(context), Runnable {
    private var game = Game(this)
    private var firstTime = true
    private val gameRunning = true
    private val paddingSize = 100

    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var cellWidth: Int = 0
    private var cellHeight: Int = 0

    private var gameThread: Thread? = null

    private var canvas: Canvas? = null
    private val surfaceHolder: SurfaceHolder = holder

    override fun run() {
        while (gameRunning) {
            draw()
            waitSomeTime()
        }
    }

    fun convertDpToPixel(dp: Float): Float {
        val resources: Resources? = context.resources
        val metrics: DisplayMetrics? = resources?.displayMetrics
        return dp * (metrics?.densityDpi?.div(160f)!!)
    }

    private fun init() {

        canvasWidth = surfaceHolder.surfaceFrame.width()
        canvasHeight = surfaceHolder.surfaceFrame.height()
        canvasCenter = FloatPoint(canvasWidth / 2.0, canvasHeight / 2.0)


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
        game.setMines(Game.Difficulty.MEDIUM)
    }


    private fun fillWithHexagons() {
        //TODO replace magick numbers
        for (q in -7..7) for (r in -6..6) {
            //check if visible
            val newXCord =
                canvasCenter.x + Cell.HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)
            if (newXCord > paddingSize && newXCord < canvasWidth - paddingSize) {
                game.cells.add(Cell(q, r))
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
            //todo NullPointerException here! on sleep ...
            canvas?.drawColor(Color.BLACK)
            game.drawField(canvas!!);

            val tappedCellIdx = game.tapManager.getIndexOfTappedCell()
            if (tappedCellIdx != null) {
                game.cells[tappedCellIdx].uncover()
                game.drawActiveCell(canvas!!, game.cells[tappedCellIdx])
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }



    private fun waitSomeTime() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val TAG = "GameView.LOG_TAG"
        lateinit var canvasCenter: FloatPoint
    }

    init {
        gameThread = Thread(this)
        gameThread?.start()
    }
}