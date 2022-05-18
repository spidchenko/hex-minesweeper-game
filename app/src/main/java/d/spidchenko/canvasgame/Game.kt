package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log

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

    fun drawField(canvas: Canvas) {
        for (hexagon in cells) {
            drawSimpleCell(canvas, hexagon)
        }
    }

    fun drawActiveCell(canvas: Canvas, cell: Cell) {
        drawBoldCell(canvas, cell);
    }

    private fun drawCellState(canvas: Canvas, cell: Cell) {
        when {
            //TODO draw covered cell
            cell.state == Cell.State.COVERED ->
                drawText(cell, "?", textPaint, canvas)

            //TODO game over
            cell.state == Cell.State.UNCOVERED && cell.hasBomb ->
                drawText(cell, "B", textPaint, canvas)

            //TODO draw number
            cell.state == Cell.State.UNCOVERED && !cell.hasBomb ->
                drawText(cell, cell.numBombsAround.toString(), textPaint, canvas)

            //TODO draw red flag
            cell.state == Cell.State.FLAGGED ->
                drawText(cell, "F", textPaint, canvas)
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
    }

    companion object {
        private const val TAG = "Game.LOG_TAG"
    }
}