package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdLogEntity
import com.example.pangle.PangleManager
import com.example.pangle.PangleSdkState
import com.example.ui.components.PangleBannerAdWidget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PangleSandboxScreen(
    pangleState: PangleSdkState,
    adLogs: List<AdLogEntity>,
    onToggleTestMode: (Boolean) -> Unit,
    onUpdateFloorPrice: (Double) -> Unit,
    onLoadBanner: () -> Unit,
    onBannerClick: () -> Unit,
    onShowInterstitial: () -> Unit,
    onShowRewarded: () -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Pangle Credentials Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00251A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF00251A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PANGLE SDK MONETIZATION",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Publisher Portal Integration • App ID: ${pangleState.appId}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF80CBC4)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (pangleState.isTestMode) Color(0xFFFFC107) else Color(0xFF00E676))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (pangleState.isTestMode) "TESTING" else "LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00251A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFF004D40))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Registered Test Device (GAID):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pangleState.testDeviceId,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Placements grid
                    Text(
                        text = "Configured Ad Placements:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF80CBC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    PlacementInfoRow(name = "My Banner (Default)", placementId = "983456260", type = "In-App Bidding", status = if (pangleState.isTestMode) "Testing" else "Live")
                    PlacementInfoRow(name = "Interstitial", placementId = "983456261", type = "In-App Bidding", status = "Testing")
                    PlacementInfoRow(name = "Rewarded Video", placementId = "983456262", type = "In-App Bidding", status = "Testing")
                }
            }
        }

        // Mode Switch & Floor Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pangle Network Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = if (pangleState.isTestMode) "Using Test Sandbox Ad Fill" else "Using Live Pangle Bidding Auction",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = pangleState.isTestMode,
                            onCheckedChange = onToggleTestMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00695C),
                                checkedTrackColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("toggle_pangle_test_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target Floor Price (eCPM)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "\$${"%.2f".format(pangleState.adFloorPriceUsd)} USD",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF00695C)
                        )
                    }

                    Slider(
                        value = pangleState.adFloorPriceUsd.toFloat(),
                        onValueChange = { onUpdateFloorPrice(it.toDouble()) },
                        valueRange = 0.5f..10.0f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00695C),
                            activeTrackColor = Color(0xFF00897B)
                        ),
                        modifier = Modifier.testTag("floor_price_slider")
                    )
                }
            }
        }

        // Interactive Ad Triggers Sandbox
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Ad Unit Triggers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Trigger test ad requests for placement ID 983456260 and verify eCPM response.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onLoadBanner,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("trigger_banner_ad_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                        ) {
                            Text("Request Banner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onShowInterstitial,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("trigger_interstitial_ad_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Text("Show Interstitial", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onShowRewarded,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("trigger_rewarded_ad_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17))
                        ) {
                            Text("Show Rewarded", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Banner Preview
        item {
            Text(
                text = "Live Banner Preview (Placement: 983456260)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            PangleBannerAdWidget(
                pangleState = pangleState,
                onRefreshAd = onLoadBanner,
                onAdClicked = onBannerClick
            )
        }

        // Live SDK Event Logs Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pangle SDK Event Log Stream",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )

                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.testTag("clear_ad_logs_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Logs",
                        tint = Color(0xFFC62828)
                    )
                }
            }
        }

        if (adLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No Pangle SDK events recorded yet. Tap buttons above to trigger ads.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(adLogs.take(20)) { log ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (log.eventType) {
                                                "IMPRESSION" -> Color(0xFF22C55E)
                                                "REVENUE_EARNED" -> Color(0xFFEAB308)
                                                "CLICK" -> Color(0xFF3B82F6)
                                                else -> Color(0xFF94A3B8)
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${log.adType} • ${log.eventType}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = timeFormat.format(Date(log.timestamp)),
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (log.ecpmUsd > 0.0) {
                            Text(
                                text = "eCPM: \$${"%.2f".format(log.ecpmUsd)} | Rev: +\$${"%.5f".format(log.revenueUsd)} | ID: ${log.placementId}",
                                fontSize = 10.sp,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        log.errorMessage?.let {
                            Text(text = "> $it", fontSize = 10.sp, color = Color(0xFFCBD5E1), fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun PlacementInfoRow(name: String, placementId: String, type: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            Text("ID: $placementId • $type", fontSize = 10.sp, color = Color(0xFF80CBC4))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF00796B))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(status, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
