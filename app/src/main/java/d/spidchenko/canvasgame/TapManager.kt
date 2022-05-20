package d.spidchenko.canvasgame

import android.util.Log
import kotlin.math.hypot

enum class ClickDuration { SHORT, LONG }

class TapManager(private val game: Game) {

    fun getIndexOfTappedCell(): Int? {
        val tap = MainActivity.lastClickCoordinates
        if (tap != null) {
            Log.d(TAG, "getIndexOfTappedCell: x=${tap.x} y=${tap.y} ${MainActivity.clickDuration}")
            var nearestHexIndex: Int? = null
            var minDistance = Double.MAX_VALUE
            for (i in game.cells.indices) {
                val distance = hypot(
                    (game.cells[i].centerPoint.x - tap.x),
                    (game.cells[i].centerPoint.y - tap.y)
                )
                if (distance < minDistance) {
                    minDistance = distance
                    nearestHexIndex = i
                }
            }
            MainActivity.lastClickCoordinates = null
            return if (minDistance < Cell.HEX_SIZE) {
                nearestHexIndex
            } else {
                null
            }
        }
        return null
    }

    companion object {
        private const val TAG = "TapManager.LOG_TAG"
    }
}