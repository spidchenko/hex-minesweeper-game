package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import kotlin.math.abs

class Game(gameView: GameView) {
    enum class Difficulty(val numberOfMines: Int) {
        EASY(5), MEDIUM(10), HARD(20)
    }

    var isRunning = true
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
        textSize = gameView.convertDpToPixel(28f)
        textAlign = Paint.Align.CENTER
    }

    fun drawField(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)
            for (hexagon in cells) {
                drawSimpleCell(canvas, hexagon)
            }
            drawActiveCell(canvas)
        }
    }

    fun setMines(difficulty: Difficulty) {
        val numberOfMines = difficulty.numberOfMines
        val indexes = IntArray(cells.size) { it } // 0, 1, 2, 3...
        Log.d(TAG, "setMines: Cells - ${cells.size}. Mines - $numberOfMines")
        // using shuffle to get n random cells
        indexes.shuffle()
        for (i in 0 until numberOfMines) {
            cells[indexes[i]].hasBomb = true
            cellsWithBombs.add(cells[indexes[i]])
        }
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
                    tappedCell.uncover()
                    if (tappedCell.numBombsAround == 0L && !tappedCell.hasBomb) {
                        uncoverSafeNeighbourCells(tappedCell)
                    }
                }
            }
        }
    }

    private fun drawActiveCell(canvas: Canvas) {
        activeCell?.let {
            drawBoldCell(canvas, it)
            activeCell = null
        }
    }

    private fun drawCellState(canvas: Canvas, cell: Cell) {
        when {
            cell.state == Cell.State.COVERED ->
                drawText(cell, Cell.ICON_COVERED, textPaint, canvas)

            cell.state == Cell.State.UNCOVERED && cell.hasBomb -> {
                drawText(cell, Cell.ICON_BOMB, textPaint, canvas)
                setGameIsOver()
            }

            cell.state == Cell.State.UNCOVERED && !cell.hasBomb ->
                if (cell.numBombsAround > 0) {
                    drawText(cell, cell.numBombsAround.toString(), textPaint, canvas)
                }

            cell.state == Cell.State.FLAGGED -> {
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
            val numberOfNearestBombs = getNeighbours(cell).stream().filter(Cell::hasBomb).count()
            cell.numBombsAround = numberOfNearestBombs
        }
    }

    private fun getNeighbours(cell: Cell): List<Cell> {
        val neighbours = mutableListOf<Cell>()
        // TODO use streams API here
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
            if (c.state == Cell.State.COVERED) {
                c.uncover()
                if (c.numBombsAround == 0L) {
                    uncoverSafeNeighbourCells(c)
                }
            }
        }
    }

    private fun checkWinState() {
        if (cellsWithBombs.size > 0) {
            val totalBombsFound =
                cellsWithBombs.stream().filter { cell -> cell.state == Cell.State.FLAGGED }.count()
            if (totalBombsFound.toInt() == cellsWithBombs.size) {
                isRunning = false
                // TODO happy music here
                Log.d(TAG, "YOU WON")
            }
        }
    }

    private fun setGameIsOver() {
        // TODO boom sound here. Vibration would be great too
        isRunning = false
        Log.d(TAG, "drawCellState: GAME OVER")
    }

    companion object {
        private const val TAG = "Game.LOG_TAG"
    }
}