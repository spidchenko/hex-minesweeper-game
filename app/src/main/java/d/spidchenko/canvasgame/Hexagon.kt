package d.spidchenko.canvasgame

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import d.spidchenko.canvasgame.Hexagon
import java.util.ArrayList
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class Hexagon internal constructor(var q: Int, //    private final int circumradius;
                                   var r: Int) {
    private val points = ArrayList<Point>()
    val centerPoint: Point
    fun drawCorners(canvas: Canvas, paint: Paint?) {
        for (point in points) {
            canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), paint!!)
        }
    }

    fun draw(canvas: Canvas, paint: Paint?) {
        for (i in 0..4) {
            val currentPoint = points[i]
            val nextPoint = points[i + 1]
            canvas.drawLine(currentPoint.x.toFloat(), currentPoint.y.toFloat(), nextPoint.x.toFloat(), nextPoint.y.toFloat(), paint!!)
        }
        canvas.drawLine(
                points[points.size - 1].x.toFloat(),
                points[points.size - 1].y.toFloat(),
                points[0].x.toFloat(),
                points[0].y.toFloat(),
                paint!!)
    }

    private fun getNthHexCorner(center: Point, i: Int): Point {
        val angleDeg = (60 * i - 30).toDouble()
        val angleRad = Math.PI / 180 * angleDeg
        return Point(
                (center.x + HEX_SIZE * cos(angleRad)).roundToInt(),
                (center.y + HEX_SIZE * sin(angleRad)).roundToInt()
        )
    }

    companion object {
        const val HEX_SIZE = 50
        var zeroCenter: Point? = null
    }

    init {
        val centerX = zeroCenter!!.x + (HEX_SIZE * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)).roundToInt()
        val centerY = zeroCenter!!.y + (HEX_SIZE * (3.0 / 2 * r)).roundToInt()
        centerPoint = Point(centerX, centerY)
        for (i in 0..5) {
            points.add(getNthHexCorner(centerPoint, i))
        }
    }
}