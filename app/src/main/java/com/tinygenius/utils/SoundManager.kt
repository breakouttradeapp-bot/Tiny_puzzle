package com.tinygenius.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manages sound effects for the app
 */
class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var tapSoundId: Int = -1
    private var successSoundId: Int = -1
    private var isSoundEnabled: Boolean = true
    
    init {
        initSoundPool()
    }
    
    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Load sounds if they exist
        // Note: Add tap_sound.mp3 and success_sound.mp3 to res/raw/
        try {
            val tapResId = context.resources.getIdentifier("tap_sound", "raw", context.packageName)
            if (tapResId != 0) {
                tapSoundId = soundPool?.load(context, tapResId, 1) ?: -1
            }
            
            val successResId = context.resources.getIdentifier("success_sound", "raw", context.packageName)
            if (successResId != 0) {
                successSoundId = soundPool?.load(context, successResId, 1) ?: -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }
    
    fun playTapSound() {
        if (isSoundEnabled && tapSoundId != -1) {
            soundPool?.play(tapSoundId, 0.5f, 0.5f, 1, 0, 1f)
        }
    }
    
    fun playSuccessSound() {
        if (isSoundEnabled && successSoundId != -1) {
            soundPool?.play(successSoundId, 1f, 1f, 1, 0, 1f)
        }
    }
    
    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
