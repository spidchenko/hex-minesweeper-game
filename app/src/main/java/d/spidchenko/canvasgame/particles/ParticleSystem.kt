package d.spidchenko.canvasgame.particles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlin.random.nextInt

class ParticleSystem(totalParticles: Int) {
    private var duration = 0F
    private val particles = ArrayList<Particle>()
    var isRunning = false

    init {
        particles.clear()
        for (i in 0 until totalParticles) {
            val angle = Random.nextInt(0..359) * PI / 180.0
            val speed = Random.nextInt(1..400)

            particles.add(Particle((cos(angle) * speed), (sin(angle) * speed)))
        }
    }

    fun update(fps: Long) {
//        Log.d(TAG, "updating particles: d=$duration fps=$fps")
        duration -= (1F / fps)
        particles.forEach { it.update(fps) }
        if (duration < 0) isRunning = false
    }

    fun emmitParticles(startCoordinates: PointF) {
        isRunning = true
        duration = PARTICLES_LIFESPAN
        particles.forEach { it.setStartPosition(startCoordinates) }
    }

    fun draw(canvas: Canvas, paint: Paint) {
//        Log.d(TAG, "drawing particles: ")
        particles.forEach {
            paint.setARGB(
                ((duration * 255)/ PARTICLES_LIFESPAN).roundToInt(),
                255,
                Random.nextInt(0..255),
                Random.nextInt(0..255)
            )
            canvas.drawRect(
                it.position.x.toFloat(),
                it.position.y.toFloat(),
                (it.position.x + 25).toFloat(),
                (it.position.y + 25).toFloat(),
                paint
            )
        }
    }

    companion object {
        private const val TAG = "ParticleSystem.LOG_TAG"
        const val PARTICLES_LIFESPAN = 2F // In seconds
    }
}