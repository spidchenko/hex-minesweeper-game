package d.spidchenko.canvasgame

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Cell internal constructor(val q: Int, val r: Int) {

    enum class State {
        UNCOVERED, COVERED, FLAGGED
    }

    var state: State = State.COVERED
    var hasBomb = false

    var numBombsAround: Long = 0

    val centerPoint: FloatPoint

    fun getNthHexCorner(n: Int, center: FloatPoint = centerPoint): FloatPoint {
        val angleDeg = 60.0 * n - 30.0
        val angleRad = Math.PI / 180 * angleDeg
        return FloatPoint(
            center.x + HEX_SIZE * cos(angleRad), center.y + HEX_SIZE * sin(angleRad)
        )
    }

    fun uncover() {
        state = when (state) {
            State.COVERED -> State.UNCOVERED
            else -> state // Do nothing
        }
    }

    fun flag() {
        state = when (state) {
            State.COVERED -> State.FLAGGED
            State.FLAGGED -> State.COVERED
            else -> state // Do nothing
        }
    }

    init {
        val centerX = GameView.canvasCenter.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r))
        val centerY = GameView.canvasCenter.y + (HEX_SIZE * (3.0 / 2 * r))
        centerPoint = FloatPoint(centerX, centerY)
    }

    companion object {
        const val ICON_COVERED = "⚫"
        const val ICON_BOMB = "\uD83E\uDDE8"
        const val ICON_FLAG = "\uD83D\uDEA9"
        const val HEX_SIZE = 50
        private const val TAG = "Cell.LOG_TAG"
    }
}