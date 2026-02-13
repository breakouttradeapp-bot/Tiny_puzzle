package com.tinygenius.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.tinygenius.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages app preferences using DataStore
 */
class PreferencesManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tiny_genius_prefs")
        
        private val SOUND_ENABLED = booleanPreferencesKey(Constants.SOUND_ENABLED_KEY)
        private val PREMIUM_PURCHASED = booleanPreferencesKey(Constants.PREMIUM_PURCHASED_KEY)
    }
    
    /**
     * Sound enabled state
     */
    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED] ?: true
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }
    
    /**
     * Premium purchase state
     */
    val isPremiumPurchased: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PREMIUM_PURCHASED] ?: false
    }
    
    suspend fun setPremiumPurchased(purchased: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREMIUM_PURCHASED] = purchased
        }
    }
}
