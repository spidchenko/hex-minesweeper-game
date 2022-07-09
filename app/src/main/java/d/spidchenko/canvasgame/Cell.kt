package d.spidchenko.canvasgame

import android.graphics.*
import d.spidchenko.canvasgame.particles.ParticleSystem
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Cell constructor(val q: Byte, val r: Byte) {

    enum class CellOutline { NORMAL, BOLD }

    private enum class State {
        UNCOVERED, COVERED, FLAGGED
    }

    private var state: State = State.COVERED
    val isCovered: Boolean
        get() = state == State.COVERED
    val isFlagged: Boolean
        get() = state == State.FLAGGED
    val isUncovered: Boolean
        get() = state == State.UNCOVERED
    var hasBomb = false
    var numBombsAround: Int = 0
    val hasNoBombsAround: Boolean
        get() = numBombsAround == 0
    val centerPoint: PointF

    fun draw(
        canvas: Canvas,
        paint: Paint,
        textSize: Float,
        cellOutline: CellOutline = CellOutline.NORMAL
    ) {
        when (cellOutline) {
            CellOutline.NORMAL -> {
                paint.apply {
                    color = Color.RED
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
            }
            CellOutline.BOLD -> {
                paint.apply {
                    isAntiAlias = true
                    color = Color.YELLOW
                    style = Paint.Style.STROKE
                    strokeWidth = 10f
                }
            }
        }

        canvas.drawPath(getCellPath(), paint)
        drawCellState(canvas, paint, textSize)
    }

    private fun drawCellState(
        canvas: Canvas,
        paint: Paint,
        textSize: Float
    ) {
        paint.apply {
            color = Color.GRAY
            style = Paint.Style.FILL
            this.textSize = textSize
            textAlign = Paint.Align.CENTER
        }

        when {
            isCovered -> drawText(ICON_COVERED, paint, canvas)

            isUncovered && hasBomb -> drawText(ICON_BOMB, paint, canvas)

            isUncovered && !hasBomb ->
                if (numBombsAround > 0) drawText(numBombsAround.toString(), paint, canvas)

            isFlagged -> drawText(ICON_FLAG, paint, canvas)
        }
    }


    private fun drawText(text: String, textPaint: Paint, canvas: Canvas) {
        val dyForTextAlign = (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, centerPoint.x, centerPoint.y - dyForTextAlign, textPaint)
    }


    private fun getCellPath(): Path {
        val hexPath = Path()
        hexPath.incReserve(6)
        val firstPointInHexagon = getNthHexCorner(0)
        hexPath.moveTo(firstPointInHexagon.x, firstPointInHexagon.y)
        for (n in 1..5) {
            val nThPoint = getNthHexCorner(n)
            hexPath.lineTo(nThPoint.x, nThPoint.y)
        }
        hexPath.close()
        return hexPath
    }

    private fun getNthHexCorner(n: Int, center: PointF = centerPoint): PointF {
        val angleDeg = 60.0 * n - 30.0
        val angleRad = Math.PI / 180 * angleDeg
        val dx = (HEX_SIZE * cos(angleRad)).toFloat()
        val dy = (HEX_SIZE * sin(angleRad)).toFloat()

        return PointF().apply {
            set(center)
            offset(dx, dy)
        }
    }

    fun uncover() {
        // TODO need sound here
        if (state == State.COVERED) state = State.UNCOVERED
    }

    override fun toString(): String {
        return "r=$r q=$q bombs_around=$numBombsAround has_bomb=$hasBomb"
    }

    fun flag() {
        // TODO need some sound here too
        state = when (state) {
            State.COVERED -> State.FLAGGED
            State.FLAGGED -> State.COVERED
            else -> state // Do nothing
        }
    }

    init {
        val centerX = GameEngine.canvasCenter.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r))
        val centerY = GameEngine.canvasCenter.y + (HEX_SIZE * (3.0 / 2 * r))
        centerPoint = PointF(centerX.toFloat(), centerY.toFloat())
    }

    companion object {
        const val ICON_COVERED = "⚫"
        const val ICON_BOMB = "\uD83E\uDDE8"
        const val ICON_FLAG = "\uD83D\uDEA9"
        const val HEX_SIZE = 50
        private const val TAG = "Cell.LOG_TAG"
    }
}