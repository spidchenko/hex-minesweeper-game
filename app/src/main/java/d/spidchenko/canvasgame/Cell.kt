package d.spidchenko.canvasgame

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


class Cell internal constructor(q: Int, r: Int) {

    enum class State {
        UNCOVERED, COVERED, FLAGGED
    }

    var state: State = State.COVERED
    var hasBomb = false

    //TODO replace random logic
    var numBombsAround = Random.nextInt(1, 7)

    val centerPoint: FloatPoint

    fun getNthHexCorner(n: Int, center: FloatPoint = centerPoint): FloatPoint {
        val angleDeg = 60.0 * n - 30.0
        val angleRad = Math.PI / 180 * angleDeg
        return FloatPoint(
            center.x + HEX_SIZE * cos(angleRad), center.y + HEX_SIZE * sin(angleRad)
        )
    }

    fun uncover() {
        this.state = State.UNCOVERED
    }

    companion object {
        const val HEX_SIZE = 50

    }

    init {
        val centerX = GameView.canvasCenter.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r))
        val centerY = GameView.canvasCenter.y + (HEX_SIZE * (3.0 / 2 * r))
        centerPoint = FloatPoint(centerX, centerY)
    }
}