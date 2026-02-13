package com.tinygenius

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Application class for initialization
 */
class TinyGeniusApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        initializeAdMob()
    }
    
    private fun initializeAdMob() {
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) { initializationStatus ->
            // Initialization complete
            val statusMap = initializationStatus.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                android.util.Log.d(
                    "AdMob",
                    "Adapter: $adapterClass, Status: ${status?.initializationState}, " +
                            "Description: ${status?.description}"
                )
            }
        }
        
        // Configure for kids apps - use only non-personalized ads
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        
        MobileAds.setRequestConfiguration(requestConfiguration)
    }
}
