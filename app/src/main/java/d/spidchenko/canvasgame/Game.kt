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

    val cells = mutableListOf<Cell>()
    val tapManager: TapManager = TapManager(this)

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
            for (hexagon in cells) {
                drawSimpleCell(canvas, hexagon)
            }
        }
    }

    fun drawActiveCell(canvas: Canvas, cell: Cell) {
        drawBoldCell(canvas, cell)
    }

    private fun drawCellState(canvas: Canvas, cell: Cell) {
        when {
            //TODO draw covered cell
            cell.state == Cell.State.COVERED ->
                drawText(cell, Cell.ICON_COVERED, textPaint, canvas)

            //TODO game over
            cell.state == Cell.State.UNCOVERED && cell.hasBomb ->
                drawText(cell, Cell.ICON_BOMB, textPaint, canvas)

            //TODO open cells with no bombs around
            cell.state == Cell.State.UNCOVERED && !cell.hasBomb ->
                if (cell.numBombsAround > 0) {
                    drawText(cell, cell.numBombsAround.toString(), textPaint, canvas)
                }

            //TODO draw red flag. On long tap?
            cell.state == Cell.State.FLAGGED ->
                drawText(cell, Cell.ICON_FLAG, textPaint, canvas)
        }
    }

    private fun drawSimpleCell(canvas: Canvas, cell: Cell, paint: Paint = this.thinPaint) {
        val hexPath = Path()
        hexPath.incReserve(6)
        val firstPointInHexagon = cell.getNthHexCorner(0)
        hexPath.moveTo(firstPointInHexagon.floatX, firstPointInHexagon.floatY)
        for (n in 1..5) {
            val nThPoint = cell.getNthHexCorner(n)
            hexPath.lineTo(nThPoint.floatX, nThPoint.floatY)
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
            cell.centerPoint.floatX,
            cell.centerPoint.floatY - dyForTextAlign,
            textPaint
        )

    }

    fun setMines(difficulty: Difficulty) {
        val numberOfMines = difficulty.numberOfMines
        val indexes = IntArray(cells.size) { it } // 0, 1, 2, 3...
        Log.d(TAG, "setMines: Cells - ${cells.size}. Mines - $numberOfMines")
        // using shuffle to get n random cells
        indexes.shuffle()
        for (i in 0..numberOfMines) {
            cells[indexes[i]].hasBomb = true
        }
        calcNumberOfNearestBombs()
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

    companion object {
        private const val TAG = "Game.LOG_TAG"
    }
}