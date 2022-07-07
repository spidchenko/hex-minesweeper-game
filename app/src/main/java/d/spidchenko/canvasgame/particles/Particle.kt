package d.spidchenko.canvasgame.particles

import android.graphics.PointF

class Particle(direction: PointF) {
    private val velocity = PointF().apply { set(direction) }
    var position = PointF()
        set(value) = field.set(value)

    fun update() = position.offset(velocity.x, velocity.y)
}