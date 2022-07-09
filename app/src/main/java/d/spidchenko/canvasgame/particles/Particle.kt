package d.spidchenko.canvasgame.particles

import android.graphics.PointF

class Particle(velX: Double, velY: Double) {

    private val velocity = Vector2d(true).apply { set(velX, velY) }
    val position = Vector2d(true)

    private val deltaVelocity = Vector2d(true)
    private val deltaPosition = Vector2d(true)

    fun setStartPosition(startCoordinates: PointF){
        position.set(startCoordinates.x.toDouble() ,startCoordinates.y.toDouble())
    }

    fun update(fps: Long){
        deltaPosition.apply {
            cloneFrom(velocity)
            divide(fps)
        }
        position.add(deltaPosition)

        deltaVelocity.apply {
            cloneFrom(G)
            divide(fps)
        }
        velocity.add(deltaVelocity)
    }

    companion object{
        // Gravity const
        private val G = Vector2d(0.0, 200.0)
    }
}