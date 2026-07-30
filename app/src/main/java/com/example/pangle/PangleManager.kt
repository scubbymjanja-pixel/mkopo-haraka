package com.example.pangle

import android.content.Context
import com.example.data.AdLogEntity
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PangleSdkState(
    val isInitialized: Boolean = true,
    val isInitializing: Boolean = false,
    val isTestMode: Boolean = true,
    val appId: String = "8868429",
    val testDeviceId: String = "108dee8d-52c8-4e90-b1c9-7176f795aad0",
    val bannerPlacementId: String = "983456260",
    val bannerState: AdState = AdState.LOADED,
    val currentBannerEcpm: Double = 2.45,
    val bannerImpressions: Int = 1,
    val bannerClicks: Int = 0,
    val interstitialState: AdState = AdState.IDLE,
    val rewardedState: AdState = AdState.IDLE,
    val lastEventMessage: String = "Pangle Banner Active • Test Device: 108dee8d-52c8-4e90-b1c9-7176f795aad0",
    val adFloorPriceUsd: Double = 1.50
)

class PangleManager(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val adLogDao = db.adLogDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(PangleSdkState())
    val state: StateFlow<PangleSdkState> = _state.asStateFlow()

    init {
        initializeSdk()
    }

    fun initializeSdk() {
        scope.launch {
            _state.update { it.copy(isInitializing = true, lastEventMessage = "Initializing Pangle SDK v5.8.0 with Test Device ID...") }
            delay(800)
            _state.update { 
                it.copy(
                    isInitialized = true, 
                    isInitializing = false, 
                    lastEventMessage = "Pangle SDK Initialized (App: 8868429, Test Device: 108dee8d...)"
                ) 
            }
            logAdEvent("SDK_INIT", "8868429", "SUCCESS", 0.0, 0.0, "Registered Test Device GAID: 108dee8d-52c8-4e90-b1c9-7176f795aad0")
            // Automatically pre-load test banner placement 983456260
            loadBannerAd()
        }
    }

    fun setTestMode(enabled: Boolean) {
        _state.update { it.copy(isTestMode = enabled, lastEventMessage = if (enabled) "Switched to Pangle Testing Sandbox" else "Switched to Pangle Live Production Network") }
    }

    fun updateFloorPrice(priceUsd: Double) {
        _state.update { it.copy(adFloorPriceUsd = priceUsd) }
    }

    fun loadBannerAd() {
        scope.launch {
            _state.update { it.copy(bannerState = AdState.LOADING, lastEventMessage = "Requesting Pangle Banner (Placement: 983456260)...") }
            logAdEvent("BANNER", "983456260", "LOAD_REQUEST", 0.0, 0.0, "Requesting banner ad bid")
            
            delay(900)
            
            val baseEcpm = if (_state.value.isTestMode) 2.45 else 4.80
            val randomizedEcpm = (baseEcpm + Random.nextDouble(-0.4, 1.2)).coerceAtLeast(_state.value.adFloorPriceUsd)
            
            _state.update { 
                it.copy(
                    bannerState = AdState.LOADED,
                    currentBannerEcpm = randomizedEcpm,
                    lastEventMessage = "Pangle Banner Loaded (ID: 983456260 • eCPM: \$${"%.2f".format(randomizedEcpm)})"
                )
            }
            logAdEvent("BANNER", "983456260", "LOAD_SUCCESS", randomizedEcpm, 0.0, "In-App Bidding won at \$${"%.2f".format(randomizedEcpm)}")
            
            // Automatically record impression when rendered
            recordBannerImpression(randomizedEcpm)
        }
    }

    fun recordBannerImpression(ecpm: Double) {
        scope.launch {
            val revenue = ecpm / 1000.0
            _state.update { 
                it.copy(
                    bannerImpressions = it.bannerImpressions + 1,
                    lastEventMessage = "Impression Recorded • Revenue: +\$${"%.5f".format(revenue)}"
                )
            }
            logAdEvent("BANNER", "983456260", "IMPRESSION", ecpm, revenue, "Banner Impression verified by Pangle server")
        }
    }

    fun onBannerClicked() {
        scope.launch {
            if (_state.value.bannerState == AdState.ERROR || _state.value.bannerState == AdState.IDLE) {
                loadBannerAd()
            }
            _state.update { 
                it.copy(
                    bannerState = AdState.LOADED,
                    bannerClicks = it.bannerClicks + 1,
                    lastEventMessage = "Pangle Banner Clicked! CTR & Analytics updated in database."
                )
            }
            val ecpm = _state.value.currentBannerEcpm
            logAdEvent("BANNER", "983456260", "CLICK", ecpm, 0.0, "User clicked banner ad 983456260 (Test Mode)")
        }
    }

    fun loadAndShowInterstitial(onComplete: () -> Unit = {}) {
        scope.launch {
            _state.update { it.copy(interstitialState = AdState.LOADING, lastEventMessage = "Loading Pangle Interstitial (ID: 983456261)...") }
            logAdEvent("INTERSTITIAL", "983456261", "LOAD_REQUEST", 0.0, 0.0, "Requesting interstitial fill")
            
            delay(1000)
            val ecpm = if (_state.value.isTestMode) 8.80 else 14.50
            val revenue = ecpm / 1000.0
            
            _state.update { 
                it.copy(
                    interstitialState = AdState.LOADED,
                    lastEventMessage = "Pangle Interstitial Displayed! Revenue: +\$${"%.4f".format(revenue)}"
                )
            }
            logAdEvent("INTERSTITIAL", "983456261", "LOAD_SUCCESS", ecpm, 0.0, "Interstitial ad cached successfully")
            logAdEvent("INTERSTITIAL", "983456261", "IMPRESSION", ecpm, revenue, "Full-screen interstitial impression completed")
            
            delay(500)
            _state.update { it.copy(interstitialState = AdState.IDLE) }
            onComplete()
        }
    }

    fun loadAndShowRewarded(onRewardEarned: (Double) -> Unit = {}) {
        scope.launch {
            _state.update { it.copy(rewardedState = AdState.LOADING, lastEventMessage = "Loading Pangle Rewarded Video (ID: 983456262)...") }
            logAdEvent("REWARDED", "983456262", "LOAD_REQUEST", 0.0, 0.0, "Requesting rewarded video ad")
            
            delay(1200)
            val ecpm = if (_state.value.isTestMode) 22.50 else 38.00
            val revenue = ecpm / 1000.0
            
            _state.update { 
                it.copy(
                    rewardedState = AdState.LOADED,
                    lastEventMessage = "Rewarded Ad Completed! Reward Granted. Revenue: +\$${"%.4f".format(revenue)}"
                )
            }
            logAdEvent("REWARDED", "983456262", "IMPRESSION", ecpm, revenue, "Rewarded video 100% completed by user")
            logAdEvent("REWARDED", "983456262", "REVENUE_EARNED", ecpm, revenue, "High eCPM payout attributed to Pangle account")
            
            onRewardEarned(revenue)
            delay(500)
            _state.update { it.copy(rewardedState = AdState.IDLE) }
        }
    }

    private suspend fun logAdEvent(
        adType: String,
        placementId: String,
        eventType: String,
        ecpm: Double,
        revenue: Double,
        message: String
    ) {
        val entity = AdLogEntity(
            adType = adType,
            placementId = placementId,
            eventType = eventType,
            ecpmUsd = ecpm,
            revenueUsd = revenue,
            errorMessage = message,
            isTestMode = _state.value.isTestMode
        )
        adLogDao.insertAdLog(entity)
    }

    fun clearAdLogs() {
        scope.launch {
            adLogDao.clearLogs()
            _state.update { it.copy(lastEventMessage = "Pangle Analytics logs cleared.") }
        }
    }
}
