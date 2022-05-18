package d.spidchenko.canvasgame

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt



class GameView(context: Context?) : SurfaceView(context), Runnable {
    private var firstTime = true
    private val gameRunning = true
    private val paddingSize = 100

    var canvasWidth: Int = 0
    var canvasHeight: Int = 0
    var cellWidth: Int = 0
    var cellHeight: Int = 0

    //TODO set number of cells here
    private val cells = mutableListOf<Cell>()
    private var gameThread: Thread? = null
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val thinPaint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val textPaint: Paint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
        textSize = convertDpToPixel(28f, context)
        textAlign = Paint.Align.CENTER
    }

    private var canvas: Canvas? = null
    private val surfaceHolder: SurfaceHolder = holder

    override fun run() {
        while (gameRunning) {
            draw()
            waitSomeTime()
        }
    }

    private fun init() {

        canvasWidth = surfaceHolder.surfaceFrame.width()
        canvasHeight = surfaceHolder.surfaceFrame.height()
        canvasCenter = FloatPoint(canvasWidth / 2f, canvasHeight / 2f)


        Log.d(TAG, "init: center = ${canvasCenter.x} ${canvasCenter.y}. Dimensions: $canvasWidth $canvasHeight")

        cellWidth = (Cell.HEX_SIZE * sqrt(3.0)).roundToInt()
        cellHeight = (1.5 * Cell.HEX_SIZE).roundToInt()

        val numRows = (canvasWidth - 2 * paddingSize) / cellWidth
        val numColumns = (canvasHeight - 2 * paddingSize) / cellHeight
        Log.d(TAG, "init: numRows = $numRows")
        Log.d(TAG, "init: numColumns = $numColumns")
        fillWithHexagons()
        //TODO set mines after first turn
        setMines(Game.Difficulty.MEDIUM)
    }

    private fun setMines(difficulty: Game.Difficulty) {
        val numberOfMines = difficulty.numberOfMines
        val indexes = IntArray(cells.size) { it } // 0, 1, 2, 3...
        Log.d(TAG, "setMines: Cells - ${cells.size}. Mines - $numberOfMines")
        // using shuffle to get n random cells
        indexes.shuffle()
        for (i in 0..numberOfMines) {
            cells[indexes[i]].hasBomb = true
        }
    }

    private fun fillWithHexagons() {
        //TODO replace magick numbers
        for (q in -7..7) for (r in -6..6) {
            //check if visible
            val newXCord =
                    canvasCenter.x + Cell.HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)
            if (newXCord > paddingSize && newXCord < canvasWidth - paddingSize) {
                cells.add(Cell(q, r))
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
            for (hexagon in cells) {

                hexagon.draw(canvas!!, thinPaint, textPaint)
            }
            if (MainActivity.tapPosition != null) {
                val tap = MainActivity.tapPosition
                var nearestHexIndex = 0
                var minDistance = Double.MAX_VALUE
                for (i in cells.indices) {
                    val distance = sqrt(
                            (cells[i].centerPoint.x - tap!!.x).toDouble()
                                    .pow(2.0) + (cells[i].centerPoint.y - tap.y).toDouble().pow(2.0)
                    )
                    if (distance < minDistance) {
                        minDistance = distance
                        nearestHexIndex = i
                    }
                }
                if (minDistance < Cell.HEX_SIZE) {
                    cells[nearestHexIndex].uncover()
                    //TODO make simple - bold border
                    cells[nearestHexIndex].draw(canvas!!, paint, textPaint)
                }
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

        private fun convertDpToPixel(dp: Float, context: Context?): Float {
            val resources: Resources? = context?.resources
            val metrics: DisplayMetrics? = resources?.displayMetrics
            return dp * (metrics?.densityDpi?.div(160f)!!)
        }
    }

    init {
        gameThread = Thread(this)
        gameThread?.start()
    }
}