package d.spidchenko.canvasgame

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundEngine(context: Context) {
    private val soundPool: SoundPool
    private val explosionId: Int
    private val popId: Int

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
        var descriptor = assetManager.openFd("explosion.ogg")
        explosionId = soundPool.load(descriptor, 0)
        descriptor = assetManager.openFd("bubble-pop.ogg")
        popId = soundPool.load(descriptor, 0)
    }

    fun playExplosion() = playSoundById(explosionId)

    fun playPop() = playSoundById(popId)

    private fun playSoundById(soundId: Int) {
        val leftVolume = 1F
        val rightVolume = 1F
        val priority = 0
        val loopMode = 0
        val rate = 1F
        soundPool.play(soundId, leftVolume, rightVolume, priority, loopMode, rate)
    }
}