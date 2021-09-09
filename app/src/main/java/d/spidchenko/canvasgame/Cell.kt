package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Cell internal constructor(q: Int, r: Int) {

    enum class CellState {
        UNCOVERED, COVERED, FLAGGED
    }

    var state: CellState = CellState.COVERED
    var hasBomb = false

    val centerPoint: FloatPoint

    fun draw(canvas: Canvas, paint: Paint, textPaint: Paint) {
        val hexPath = Path()
        hexPath.incReserve(6)
        val firstPointInHexagon = getNthHexCorner(0)
        hexPath.moveTo(firstPointInHexagon.x, firstPointInHexagon.y)
        for (n in 1..5) {
            val nThPoint = getNthHexCorner(n)
            hexPath.lineTo(nThPoint.x, nThPoint.y)
        }
        hexPath.close()
        canvas.drawPath(hexPath, paint)

        when (state) {
            CellState.COVERED -> drawText("\u2622", textPaint, canvas)
            CellState.UNCOVERED -> drawText("\u2620", textPaint, canvas)
            CellState.FLAGGED -> drawText("F", textPaint, canvas)
        }

    }

    private fun drawText(text: String, textPaint: Paint, canvas: Canvas) {
        val dyForTextAlign = (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, centerPoint.x, centerPoint.y - dyForTextAlign, textPaint)

    }

    private fun getNthHexCorner(n: Int, center: FloatPoint = centerPoint): FloatPoint {
        val angleDeg = (60 * n - 30).toDouble()
        val angleRad = Math.PI / 180 * angleDeg
        return FloatPoint(
                (center.x + HEX_SIZE * cos(angleRad)).toFloat(),
                (center.y + HEX_SIZE * sin(angleRad)).toFloat()
        )
    }


    companion object {
        const val HEX_SIZE = 50
        var gameFieldCenter = FloatPoint()
    }

    init {
        val centerX = gameFieldCenter.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r))
        val centerY = gameFieldCenter.y + (HEX_SIZE * (3.0 / 2 * r))
        centerPoint = FloatPoint(centerX.toFloat(), centerY.toFloat())
    }
}