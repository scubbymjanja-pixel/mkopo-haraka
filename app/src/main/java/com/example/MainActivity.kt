package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyticsReportScreen
import com.example.ui.screens.LoanScreen
import com.example.ui.screens.PangleSandboxScreen
import com.example.ui.theme.MkopoHarakaTheme
import com.example.ui.viewmodel.MkopoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MkopoViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MkopoHarakaTheme {
                val loanState by viewModel.loanUiState.collectAsStateWithLifecycle()
                val pangleState by viewModel.pangleState.collectAsStateWithLifecycle()
                val activeLoans by viewModel.allLoans.collectAsStateWithLifecycle()
                val adLogs by viewModel.adLogs.collectAsStateWithLifecycle()
                val analyticsSummary by viewModel.analyticsSummary.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                LaunchedEffect(loanState.submissionMessage) {
                    loanState.submissionMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearSubmissionMessage()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mkopo Haraka",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.pangleManager.loadBannerAd() },
                                    modifier = Modifier.testTag("topbar_refresh_ad_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Pangle Ad",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF004D40)
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                icon = { Icon(Icons.Default.CreditCard, contentDescription = "Loans") },
                                label = { Text("Loans & Files", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00695C),
                                    selectedTextColor = Color(0xFF00695C),
                                    indicatorColor = Color(0xFFE0F2F1)
                                ),
                                modifier = Modifier.testTag("tab_loans_files")
                            )

                            NavigationBarItem(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                icon = { Icon(Icons.Default.BugReport, contentDescription = "Pangle Sandbox") },
                                label = { Text("Pangle Sandbox", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00695C),
                                    selectedTextColor = Color(0xFF00695C),
                                    indicatorColor = Color(0xFFE0F2F1)
                                ),
                                modifier = Modifier.testTag("tab_pangle_sandbox")
                            )

                            NavigationBarItem(
                                selected = selectedTabIndex == 2,
                                onClick = { selectedTabIndex = 2 },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                                label = { Text("Ad Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00695C),
                                    selectedTextColor = Color(0xFF00695C),
                                    indicatorColor = Color(0xFFE0F2F1)
                                ),
                                modifier = Modifier.testTag("tab_ad_analytics")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTabIndex) {
                            0 -> LoanScreen(
                                loanUiState = loanState,
                                pangleState = pangleState,
                                activeLoans = activeLoans,
                                onAmountChange = viewModel::updateAmount,
                                onDurationChange = viewModel::updateDuration,
                                onPurposeChange = viewModel::updatePurpose,
                                onApplicantInfoChange = viewModel::updateApplicantInfo,
                                onDocumentSelected = viewModel::setDocument,
                                onDocumentRemoved = viewModel::removeDocument,
                                onApplyAdDiscount = viewModel::applyAdInterestDiscount,
                                onSubmitLoan = viewModel::submitLoanApplication,
                                onRepayLoan = viewModel::repayLoan,
                                onRefreshBanner = { viewModel.pangleManager.loadBannerAd() },
                                onBannerClicked = { viewModel.pangleManager.onBannerClicked() }
                            )
                            1 -> PangleSandboxScreen(
                                pangleState = pangleState,
                                adLogs = adLogs,
                                onToggleTestMode = { viewModel.pangleManager.setTestMode(it) },
                                onUpdateFloorPrice = { viewModel.pangleManager.updateFloorPrice(it) },
                                onLoadBanner = { viewModel.pangleManager.loadBannerAd() },
                                onBannerClick = { viewModel.pangleManager.onBannerClicked() },
                                onShowInterstitial = { viewModel.pangleManager.loadAndShowInterstitial() },
                                onShowRewarded = { viewModel.pangleManager.loadAndShowRewarded() },
                                onClearLogs = { viewModel.pangleManager.clearAdLogs() }
                            )
                            2 -> AnalyticsReportScreen(
                                summary = analyticsSummary,
                                logs = adLogs,
                                pangleState = pangleState,
                                onRefreshAd = { viewModel.pangleManager.loadBannerAd() },
                                onAdClicked = { viewModel.pangleManager.onBannerClicked() }
                            )
                        }
                    }
                }
            }
        }
    }
}
