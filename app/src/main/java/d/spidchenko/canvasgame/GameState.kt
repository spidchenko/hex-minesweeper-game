package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import kotlin.math.abs

class GameState(
    private val gameEngine: GameEngine,
    private val soundEngine: SoundEngine
) {
    enum class Difficulty(val numberOfMines: Int) {
        EASY(5), MEDIUM(10), HARD(20)
    }

    var isPlaying = false
    val cells = mutableListOf<Cell>()
    private val cellsWithBombs = mutableListOf<Cell>()
    private val tapManager: TapManager = TapManager(this)
    private var activeCell: Cell? = null

    private val boldPaint: Paint = Paint().apply {
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
        textSize = gameEngine.convertDpToPixel(28f)
        textAlign = Paint.Align.CENTER
    }

    fun drawField(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)
            cells.forEach { drawSimpleCell(canvas, it) }
            drawActiveCell(canvas)
            graduallyChangeGridColor()
        }
    }

    fun setMines(difficulty: Difficulty) {
        val numberOfMinesToSet = difficulty.numberOfMines
        Log.d(TAG, "setMines: Cells - ${cells.size}. Mines - $numberOfMinesToSet")
        val randomCells = getNRandomCells(numberOfMinesToSet)
        cellsWithBombs.addAll(randomCells)
        cellsWithBombs.forEach { it.hasBomb = true }
        calcNumberOfNearestBombs()
    }

    fun handleClicks() {
        val tappedCellIdx = tapManager.getIndexOfTappedCell()
        if (tappedCellIdx != null) {
            val tappedCell = cells[tappedCellIdx]
            Log.d(TAG, "handleClicks: Tapped Cell: $tappedCell")
            activeCell = tappedCell
            when (MainActivity.clickDuration) {
                ClickDuration.LONG -> tappedCell.flag()
                ClickDuration.SHORT -> {
                    soundEngine.playPop()
                    tappedCell.uncover()
                    if (tappedCell.numBombsAround == 0 && !tappedCell.hasBomb) {
                        uncoverSafeNeighbourCells(tappedCell)
                    }
                }
            }
        }
    }

    private fun getNRandomCells(n: Int): List<Cell> {
        val indexes = MutableList(cells.size) { it } // 0, 1, 2, 3...
        val nRandomCells = mutableListOf<Cell>()
        indexes.shuffle()
        for (i in 0 until n) {
            nRandomCells.add(cells[indexes[i]])
        }
        return nRandomCells
    }

    private fun drawActiveCell(canvas: Canvas) {
        activeCell?.let {
            drawBoldCell(canvas, it)
            activeCell = null
        }
    }

    private fun drawCellState(canvas: Canvas, cell: Cell) {
        when {
            cell.isCovered ->
                drawText(cell, Cell.ICON_COVERED, textPaint, canvas)

            cell.isUncovered && cell.hasBomb -> {
                drawText(cell, Cell.ICON_BOMB, textPaint, canvas)
                setGameIsOver()
            }

            cell.isUncovered && !cell.hasBomb ->
                if (cell.numBombsAround > 0) {
                    drawText(cell, cell.numBombsAround.toString(), textPaint, canvas)
                }

            cell.isFlagged -> {
                drawText(cell, Cell.ICON_FLAG, textPaint, canvas)
                checkWinState()
            }
        }
    }

    private fun drawSimpleCell(canvas: Canvas, cell: Cell, paint: Paint = this.thinPaint) {
        val hexPath = Path()
        hexPath.incReserve(6)
        val firstPointInHexagon = cell.getNthHexCorner(0)
        hexPath.moveTo(firstPointInHexagon.x, firstPointInHexagon.y)
        for (n in 1..5) {
            val nThPoint = cell.getNthHexCorner(n)
            hexPath.lineTo(nThPoint.x, nThPoint.y)
        }
        hexPath.close()
        canvas.drawPath(hexPath, paint)
        drawCellState(canvas, cell)
    }

    private fun graduallyChangeGridColor() {
        val hsvColorValue = FloatArray(3)
        Color.colorToHSV(thinPaint.color, hsvColorValue)
        hsvColorValue[HUE_COMPONENT] += 0.5F
        if (hsvColorValue[HUE_COMPONENT] > 360) {
            hsvColorValue[HUE_COMPONENT] = 0.0F
        }
        thinPaint.color = Color.HSVToColor(hsvColorValue)
    }

    private fun drawBoldCell(canvas: Canvas, cell: Cell) {
        drawSimpleCell(canvas, cell, this.boldPaint)
    }

    private fun drawText(cell: Cell, text: String, textPaint: Paint, canvas: Canvas) {
        val dyForTextAlign = (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(
            text,
            cell.centerPoint.x,
            cell.centerPoint.y - dyForTextAlign,
            textPaint
        )
    }

    private fun calcNumberOfNearestBombs() {
        for (cell in cells) {
            val numberOfNearestBombs = getNeighbours(cell).count(Cell::hasBomb)
            cell.numBombsAround = numberOfNearestBombs
        }
    }

    private fun getNeighbours(cell: Cell): List<Cell> {
        val neighbours = mutableListOf<Cell>()
        for (c in cells) {
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

    private fun checkWinState() {
        if (cellsWithBombs.size > 0) {
            val totalBombsFound = cellsWithBombs.count(Cell::isFlagged)
            if (totalBombsFound == cellsWithBombs.size) {
                isPlaying = false
                // TODO happy music here
                Log.d(TAG, "YOU WON")
            }
        }
    }

    private fun setGameIsOver() {
        soundEngine.playExplosion()
        isPlaying = false
        Log.d(TAG, "drawCellState: GAME OVER")
    }

    companion object {
        private const val TAG = "Game.LOG_TAG"
        private const val HUE_COMPONENT = 0
    }
}