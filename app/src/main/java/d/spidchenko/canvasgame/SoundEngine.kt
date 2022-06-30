package d.spidchenko.canvasgame

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundEngine(context: Context) {
    private val soundPool: SoundPool
    private val explosionId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        val assetManager = context.assets
        val descriptor = assetManager.openFd("explosion.ogg")
        explosionId = soundPool.load(descriptor, 0)
    }

    fun playExplosion() {
        val leftVolume = 1F
        val rightVolume = 1F
        val priority = 0
        val loopMode = 0
        val rate = 1F
        soundPool.play(explosionId, leftVolume, rightVolume, priority, loopMode, rate)
    }
}