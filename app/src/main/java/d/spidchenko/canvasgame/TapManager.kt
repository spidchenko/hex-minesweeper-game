package d.spidchenko.canvasgame

import kotlin.math.hypot

class TapManager(private val game: Game) {

    fun getIndexOfTappedCell(): Int? {
        if (MainActivity.tapPosition != null) {
            val tap = MainActivity.tapPosition
            var nearestHexIndex: Int? = null;
            var minDistance = Double.MAX_VALUE
            for (i in game.cells.indices) {
                val distance = hypot(
                    (game.cells[i].centerPoint.x - tap!!.x),
                    (game.cells[i].centerPoint.y - tap.y)
                )
                if (distance < minDistance) {
                    minDistance = distance
                    nearestHexIndex = i
                }
            }
            return if (minDistance < Cell.HEX_SIZE) {
                nearestHexIndex;
            } else {
                null;
            }
        }
        return null
    }

}