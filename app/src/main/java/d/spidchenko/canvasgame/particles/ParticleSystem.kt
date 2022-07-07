package d.spidchenko.canvasgame.particles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.random.nextInt

class ParticleSystem(totalParticles: Int) {
    private var duration = 0F
    private val particles = ArrayList<Particle>()
    private var isRunning = false

    init {
        particles.clear()
        for (i in 0 until totalParticles) {
            val angle = Random.nextInt(0..359) * PI / 180.0
            val speed = Random.nextInt(1..20)
            val direction = PointF(
                (cos(angle) * speed).toFloat(),
                (sin(angle) * speed).toFloat()
            )
            particles.add(Particle(direction))
        }
    }

    fun update(fps: Long) {
        duration -= (1F / fps)
        particles.forEach { it.update() }
        if (duration < 0) isRunning = false
    }

    fun emmitParticles(startPosition: PointF) {
        isRunning = true
        duration = 1F
        particles.forEach { it.position = startPosition }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        particles.forEach {
            paint.setARGB(
                255,
                Random.nextInt(0..255),
                Random.nextInt(0..255),
                Random.nextInt(0..255)
            )
            canvas.drawRect(
                it.position.x,
                it.position.y,
                it.position.x + 25,
                it.position.y + 25,
                paint
            )
        }
    }
}