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
    private val currentAlphaValue: Int
        get() = ((duration * MAX_COLOR_COMPONENT_VALUE) / PARTICLES_LIFESPAN).roundToInt()
    private val particles = ArrayList<Particle>()
    var isRunning = false

    init {
        particles.clear()
        for (i in 0 until totalParticles) {
            val angle = Random.nextDouble(FULL_CIRCLE)
            val speed = Random.nextInt(1..PARTICLE_MAX_SPEED)
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
        particles.forEach {
            paint.setARGB(
                currentAlphaValue,
                Random.nextInt(0..MAX_COLOR_COMPONENT_VALUE),
                Random.nextInt(0..MAX_COLOR_COMPONENT_VALUE),
                Random.nextInt(0..MAX_COLOR_COMPONENT_VALUE)
            )
            canvas.drawRect(
                it.position.x.toFloat(),
                it.position.y.toFloat(),
                (it.position.x + PARTICLE_SIZE).toFloat(),
                (it.position.y + PARTICLE_SIZE).toFloat(),
                paint
            )
        }
    }

    companion object {
        private const val TAG = "ParticleSystem.LOG_TAG"
        const val FULL_CIRCLE = PI * 2
        const val PARTICLES_LIFESPAN = 2F // In seconds
        const val MAX_COLOR_COMPONENT_VALUE = 255
        const val PARTICLE_SIZE = 25
        const val PARTICLE_MAX_SPEED = 400
    }
}