package d.spidchenko.canvasgame

import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Cell internal constructor(val q: Byte, val r: Byte) {

    enum class State {
        UNCOVERED, COVERED, FLAGGED
    }

    var state: State = State.COVERED
    var hasBomb = false
    var numBombsAround: Int = 0
    val centerPoint: PointF

    fun getNthHexCorner(n: Int, center: PointF = centerPoint): PointF {
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
        state = when (state) {
            State.COVERED -> State.UNCOVERED
            else -> state // Do nothing
        }
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

    fun isFlagged(): Boolean {
        return state == State.FLAGGED
    }

    init {
        val centerX = GameView.canvasCenter.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r))
        val centerY = GameView.canvasCenter.y + (HEX_SIZE * (3.0 / 2 * r))
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