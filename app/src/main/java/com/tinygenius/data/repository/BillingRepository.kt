package com.tinygenius.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.tinygenius.data.local.PreferencesManager
import com.tinygenius.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository for handling Google Play Billing
 */
class BillingRepository(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private var billingClient: BillingClient? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()
    
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    
    sealed class PurchaseState {
        object Idle : PurchaseState()
        object Loading : PurchaseState()
        object Success : PurchaseState()
        data class Error(val message: String) : PurchaseState()
    }
    
    init {
        initializeBillingClient()
        
        // Load premium status from preferences
        coroutineScope.launch {
            preferencesManager.isPremiumPurchased.collect { isPurchased ->
                _isPremium.value = isPurchased
            }
        }
    }
    
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchases(purchases)
                }
            }
            .enablePendingPurchases()
            .build()
        
        connectToBillingService()
    }
    
    private fun connectToBillingService() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Query purchases
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to reconnect
                connectToBillingService()
            }
        })
    }
    
    /**
     * Query existing purchases
     */
    private fun queryPurchases() {
        billingClient?.let { client ->
            if (!client.isReady) return
            
            coroutineScope.launch {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
                
                val purchasesResult = withContext(Dispatchers.IO) {
                    client.queryPurchasesAsync(params)
                }
                
                if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    handlePurchases(purchasesResult.purchasesList)
                }
            }
        }
    }
    
    /**
     * Handle purchases
     */
    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.products.contains(Constants.PREMIUM_PRODUCT_ID)) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Verify and acknowledge purchase
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                    
                    // Grant premium access
                    coroutineScope.launch {
                        preferencesManager.setPremiumPurchased(true)
                        _isPremium.value = true
                        _purchaseState.value = PurchaseState.Success
                    }
                }
            }
        }
    }
    
    /**
     * Acknowledge purchase
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        billingClient?.let { client ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    client.acknowledgePurchase(params)
                }
            }
        }
    }
    
    /**
     * Launch purchase flow
     */
    fun launchPurchaseFlow(activity: Activity) {
        billingClient?.let { client ->
            if (!client.isReady) {
                _purchaseState.value = PurchaseState.Error("Billing not ready")
                return
            }
            
            _purchaseState.value = PurchaseState.Loading
            
            coroutineScope.launch {
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(Constants.PREMIUM_PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
                
                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()
                
                val productDetailsResult = withContext(Dispatchers.IO) {
                    client.queryProductDetails(params)
                }
                
                if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetails = productDetailsResult.productDetailsList?.firstOrNull()
                    
                    if (productDetails != null) {
                        val productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                        )
                        
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()
                        
                        client.launchBillingFlow(activity, billingFlowParams)
                    } else {
                        _purchaseState.value = PurchaseState.Error("Product not found")
                    }
                } else {
                    _purchaseState.value = PurchaseState.Error("Failed to load product")
                }
            }
        }
    }
    
    /**
     * Reset purchase state
     */
    fun resetPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        billingClient?.endConnection()
    }
}
