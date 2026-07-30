package com.example.pangle

data class PangleAdConfig(
    val appId: String = "8868429",
    val appName: String = "Mkopo Haraka",
    val bannerPlacementId: String = "983456260", // "My banner" from screenshot
    val interstitialPlacementId: String = "983456261",
    val rewardedPlacementId: String = "983456262",
    val nativePlacementId: String = "983456263",
    val appOpenPlacementId: String = "983456264",
    val testDeviceId: String = "108dee8d-52c8-4e90-b1c9-7176f795aad0",
    val isTestMode: Boolean = true,
    val adFloorPriceUsd: Double = 1.50
)

enum class AdType(val displayName: String, val placementId: String, val defaultEcpm: Double) {
    BANNER("Banner Ad (983456260)", "983456260", 2.45),
    INTERSTITIAL("Interstitial Ad (983456261)", "983456261", 8.80),
    REWARDED("Rewarded Video (983456262)", "983456262", 18.50),
    NATIVE("Native Express (983456263)", "983456263", 4.10),
    APP_OPEN("App Open Ad (983456264)", "983456264", 12.00)
}

enum class AdState {
    IDLE, LOADING, LOADED, ERROR
}
