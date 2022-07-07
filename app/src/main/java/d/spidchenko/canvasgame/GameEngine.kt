package d.spidchenko.canvasgame

import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.SurfaceView
import d.spidchenko.canvasgame.particles.ParticleSystem
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt


class GameEngine(context: Context) : SurfaceView(context), GameStarter, Runnable {
    private val paddingSize = 100
    private val soundEngine = SoundEngine(context)
    private val gameState = GameState(context, this)
    private val particleSystem = ParticleSystem(20)
    private val renderer = Renderer(this)
    private val tapManager: TapManager = TapManager(gameState)
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var currentFPS: Long = 0L

    private lateinit var gameThread: Thread

    override fun run() {
        while (gameState.isThreadRunning) {
            val frameStartTime = System.currentTimeMillis()
            renderer.draw(currentFPS, gameState, particleSystem)
            handleClicks()
            val timeThisFrame = System.currentTimeMillis() - frameStartTime
            if (timeThisFrame >= 1) {
                currentFPS = MILLIS_IN_SECOND / timeThisFrame
            }
        }
    }

    fun startThread() {
        Log.d(TAG, "startThread: ")
        gameState.startThread()
        gameThread = Thread(this)
        gameThread.start()
    }

    fun stopThread() {
        Log.d(TAG, "stopThread: ")
        gameState.stopEverything()
        gameThread.join()
    }

    override fun initLevel(screenX: Int, screenY: Int) {
        this.canvasWidth = screenX
        this.canvasHeight = screenY

        canvasCenter = PointF(screenX / 2F, screenY / 2F)

        Log.d(
            TAG,
            "init: center = ${canvasCenter.x} ${canvasCenter.y}. Dimensions: $canvasWidth $canvasHeight"
        )

        val cellWidth = (Cell.HEX_SIZE * sqrt(3.0)).roundToInt()
        val cellHeight = (1.5 * Cell.HEX_SIZE).roundToInt()

        val numRows = (canvasWidth - 2 * paddingSize) / cellWidth
        val numColumns = (canvasHeight - 2 * paddingSize) / cellHeight
        Log.d(TAG, "init: numRows = $numRows")
        Log.d(TAG, "init: numColumns = $numColumns")
        fillWithHexagons()
        //TODO set mines after first turn
        setMines(GameState.Difficulty.HARD)
        gameState.startNewGame()
    }


    private fun handleClicks() {
        val tappedCellIdx = tapManager.getIndexOfTappedCell()
        tappedCellIdx?.let {
            val tappedCell = gameState.cells[tappedCellIdx]
            Log.d(TAG, "handleClicks: Tapped Cell: $tappedCell")
            gameState.activeCell = tappedCell
            when (MainActivity.clickDuration) {

                ClickDuration.LONG -> tappedCell.flag()

                ClickDuration.SHORT -> {
                    if (!tappedCell.isFlagged){
                        soundEngine.playPop()
                        tappedCell.uncover()
                        if (tappedCell.numBombsAround == 0 && !tappedCell.hasBomb) {
                            uncoverSafeNeighbourCells(tappedCell)
                        }
                    }
                }
            }
        }
    }

    private fun uncoverSafeNeighbourCells(cell: Cell) {
        for (c in getNeighbours(cell)) {
            if (c.isCovered) {
                c.uncover()
                if (c.hasNoBombsAround) {
                    uncoverSafeNeighbourCells(c)
                }
            }
        }
    }

    private fun getNeighbours(cell: Cell): List<Cell> {
        val neighbours = mutableListOf<Cell>()
        for (c in gameState.cells) {
            // TODO how to simplify this condition? MB with x;y;z coordinates
            if ((abs(cell.q - c.q) == 1 && cell.r == c.r) ||
                (abs(cell.q - c.q) == 1 && abs(cell.r - c.r) == 1 && (cell.q - c.q + (cell.r - c.r)) == 0) ||
                (abs(cell.r - c.r) == 1 && cell.q == c.q)
            ) {
                neighbours.add(c)
            }
        }
        return neighbours
    }

    private fun calcNumberOfNearestBombs() {
        for (cell in gameState.cells) {
            val numberOfNearestBombs = getNeighbours(cell).count(Cell::hasBomb)
            cell.numBombsAround = numberOfNearestBombs
        }
    }

    private fun setMines(difficulty: GameState.Difficulty) {
        val numberOfMinesToSet = difficulty.numberOfMines
        Log.d(TAG, "setMines: Cells - ${gameState.cells.size}. Mines - $numberOfMinesToSet")
        val randomCells = getNRandomCells(numberOfMinesToSet)
        gameState.cellsWithBombs.addAll(randomCells)
        gameState.cellsWithBombs.forEach { it.hasBomb = true }
        calcNumberOfNearestBombs()
    }


    private fun getNRandomCells(n: Int): List<Cell> {
        val indexes = MutableList(gameState.cells.size) { it } // 0, 1, 2, 3...
        val nRandomCells = mutableListOf<Cell>()
        indexes.shuffle()
        for (i in 0 until n) {
            nRandomCells.add(gameState.cells[indexes[i]])
        }
        return nRandomCells
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

    companion object {
        private const val TAG = "GameView.LOG_TAG"
        const val MILLIS_IN_SECOND = 1000
        private const val CELLS_IN_A_ROW = 11
        private const val ROWS = 13
        lateinit var canvasCenter: PointF
    }
}