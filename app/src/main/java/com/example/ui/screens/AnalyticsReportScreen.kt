package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdLogEntity
import com.example.pangle.PangleSdkState
import com.example.ui.viewmodel.AnalyticsSummary

@Composable
fun AnalyticsReportScreen(
    summary: AnalyticsSummary,
    logs: List<AdLogEntity>,
    pangleState: PangleSdkState = PangleSdkState(),
    onRefreshAd: () -> Unit = {},
    onAdClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            NativeHeaderAd(
                pangleState = pangleState,
                onRefreshAd = onRefreshAd,
                onAdClicked = onAdClicked
            )
        }

        // Revenue KPI Banner Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("revenue_analytics_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00382E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PANGLE TOTAL REVENUE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF80CBC4),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "KSh ${"%,.2f".format(summary.totalRevenueKsh)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "(\$${"%.4f".format(summary.totalRevenueUsd)} USD)",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00695C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFF004D40))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricSmallTile(label = "Impressions", value = "${summary.impressionCount}", color = Color.White)
                        MetricSmallTile(label = "Clicks", value = "${summary.clickCount}", color = Color.White)
                        MetricSmallTile(label = "CTR", value = "${"%.1f".format(summary.ctrPercent)}%", color = Color(0xFF00E676))
                        MetricSmallTile(label = "Avg eCPM", value = "\$${"%.2f".format(summary.avgEcpmUsd)}", color = Color(0xFFFFD54F))
                    }
                }
            }
        }

        // eCPM & Revenue Trend Chart (Compose Canvas)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, tint = Color(0xFF00695C))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Real-Time eCPM Trend (USD)",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE0F2F1))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Pangle Bidding", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Canvas Chart
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("ecpm_trend_canvas_chart")
                    ) {
                        val width = size.width
                        val height = size.height

                        // Grid lines
                        val stepY = height / 3
                        for (i in 0..3) {
                            drawLine(
                                color = Color(0xFFF1F5F9),
                                start = Offset(0f, i * stepY),
                                end = Offset(width, i * stepY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Simulated data points or points based on logs
                        val points = if (logs.isNotEmpty()) {
                            logs.take(7).reversed().mapIndexed { index, log ->
                                val x = (width / 6f) * index
                                val normalizedY = (log.ecpmUsd / 25.0).coerceIn(0.1, 0.9)
                                val y = height - (normalizedY * height).toFloat()
                                Offset(x, y)
                            }
                        } else {
                            listOf(
                                Offset(0f, height * 0.7f),
                                Offset(width * 0.2f, height * 0.5f),
                                Offset(width * 0.4f, height * 0.6f),
                                Offset(width * 0.6f, height * 0.3f),
                                Offset(width * 0.8f, height * 0.4f),
                                Offset(width, height * 0.2f)
                            )
                        }

                        if (points.size > 1) {
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }

                            drawPath(
                                path = path,
                                color = Color(0xFF00897B),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            points.forEach { pt ->
                                drawCircle(
                                    color = Color(0xFF004D40),
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = Color(0xFF00E676),
                                    radius = 3.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mon", fontSize = 10.sp, color = Color.Gray)
                        Text("Tue", fontSize = 10.sp, color = Color.Gray)
                        Text("Wed", fontSize = 10.sp, color = Color.Gray)
                        Text("Thu", fontSize = 10.sp, color = Color.Gray)
                        Text("Fri", fontSize = 10.sp, color = Color.Gray)
                        Text("Today", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00695C))
                    }
                }
            }
        }

        // Ad Format Breakdown & Floor Optimization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Placement Revenue Breakdown",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FormatProgressRow(title = "Banner Ad (Placement: 983456260)", progress = 0.42f, ecpm = "\$2.45 eCPM", color = Color(0xFF00897B))
                    FormatProgressRow(title = "Interstitial (Placement: 983456261)", progress = 0.75f, ecpm = "\$8.80 eCPM", color = Color(0xFF0288D1))
                    FormatProgressRow(title = "Rewarded Video (Placement: 983456262)", progress = 0.95f, ecpm = "\$18.50 eCPM", color = Color(0xFFF57F17))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun MetricSmallTile(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF80CBC4))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun FormatProgressRow(title: String, progress: Float, ecpm: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Text(ecpm, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
