package com.tinygenius.ui.home

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinygenius.data.local.PreferencesManager
import com.tinygenius.data.repository.BillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Screen
 */
class HomeViewModel(
    private val context: Context
) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    private val billingRepository = BillingRepository(context, preferencesManager)
    
    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()
    
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    
    private val _showParentGate = MutableStateFlow(false)
    val showParentGate: StateFlow<Boolean> = _showParentGate.asStateFlow()
    
    val purchaseState = billingRepository.purchaseState
    
    init {
        loadPreferences()
        observeBillingState()
    }
    
    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesManager.isSoundEnabled.collect { enabled ->
                _isSoundEnabled.value = enabled
            }
        }
    }
    
    private fun observeBillingState() {
        viewModelScope.launch {
            billingRepository.isPremium.collect { isPremium ->
                _isPremium.value = isPremium
            }
        }
    }
    
    fun toggleSound() {
        viewModelScope.launch {
            val newValue = !_isSoundEnabled.value
            _isSoundEnabled.value = newValue
            preferencesManager.setSoundEnabled(newValue)
        }
    }
    
    fun showParentGateForPurchase() {
        _showParentGate.value = true
    }
    
    fun hideParentGate() {
        _showParentGate.value = false
    }
    
    fun launchPurchase(activity: Activity) {
        billingRepository.launchPurchaseFlow(activity)
        _showParentGate.value = false
    }
    
    fun resetPurchaseState() {
        billingRepository.resetPurchaseState()
    }
    
    override fun onCleared() {
        super.onCleared()
        billingRepository.cleanup()
    }
}
