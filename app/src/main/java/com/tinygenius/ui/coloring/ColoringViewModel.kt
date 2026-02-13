package com.tinygenius.ui.coloring

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinygenius.data.local.PreferencesManager
import com.tinygenius.data.model.ColoringAction
import com.tinygenius.data.repository.ContentRepository
import com.tinygenius.utils.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel for Coloring functionality
 */
class ColoringViewModel(
    private val context: Context,
    private val pageId: Int
) : ViewModel() {
    
    private val contentRepository = ContentRepository()
    private val preferencesManager = PreferencesManager(context)
    private val soundManager = SoundManager(context)
    
    private val _selectedColor = MutableStateFlow(0xFFFF0000L) // Default red
    val selectedColor: StateFlow<Long> = _selectedColor.asStateFlow()
    
    private val _coloredRegions = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val coloredRegions: StateFlow<Map<Int, Long>> = _coloredRegions.asStateFlow()
    
    private val _undoStack = MutableStateFlow<List<ColoringAction>>(emptyList())
    val undoStack: StateFlow<List<ColoringAction>> = _undoStack.asStateFlow()
    
    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()
    
    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()
    
    init {
        loadSoundPreference()
    }
    
    private fun loadSoundPreference() {
        viewModelScope.launch {
            preferencesManager.isSoundEnabled.collect { enabled ->
                _isSoundEnabled.value = enabled
                soundManager.setSoundEnabled(enabled)
            }
        }
    }
    
    fun selectColor(color: Long) {
        _selectedColor.value = color
    }
    
    fun colorRegion(regionId: Int) {
        val currentColor = _coloredRegions.value[regionId] ?: 0xFFFFFFFF
        val newColor = _selectedColor.value
        
        if (currentColor != newColor) {
            // Add to undo stack
            val action = ColoringAction(
                regionId = regionId,
                previousColor = currentColor,
                newColor = newColor
            )
            _undoStack.value = _undoStack.value + action
            
            // Update colored regions
            _coloredRegions.value = _coloredRegions.value.toMutableMap().apply {
                put(regionId, newColor)
            }
            
            // Play sound
            soundManager.playTapSound()
            
            // Check if page is complete
            checkCompletion()
        }
    }
    
    fun undo() {
        val stack = _undoStack.value
        if (stack.isNotEmpty()) {
            val lastAction = stack.last()
            
            _coloredRegions.value = _coloredRegions.value.toMutableMap().apply {
                put(lastAction.regionId, lastAction.previousColor)
            }
            
            _undoStack.value = stack.dropLast(1)
            soundManager.playTapSound()
        }
    }
    
    fun clear() {
        _coloredRegions.value = emptyMap()
        _undoStack.value = emptyList()
        _showCelebration.value = false
        soundManager.playTapSound()
    }
    
    private fun checkCompletion() {
        // Simple completion check: if most regions are colored
        // In production, you'd define specific regions for each image
        val totalRegions = 20 // Approximate regions in an image
        val coloredCount = _coloredRegions.value.size
        
        if (coloredCount >= totalRegions * 0.8) { // 80% completion
            _showCelebration.value = true
            soundManager.playSuccessSound()
        }
    }
    
    fun hideCelebration() {
        _showCelebration.value = false
    }
    
    fun saveImage(bitmap: Bitmap): Boolean {
        return try {
            val fileName = "coloring_${pageId}_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
