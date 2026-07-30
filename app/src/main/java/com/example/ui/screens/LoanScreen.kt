package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.LoanApplicationEntity
import com.example.pangle.PangleSdkState
import com.example.ui.components.PangleBannerAdWidget
import com.example.ui.viewmodel.DocumentType
import com.example.ui.viewmodel.LoanUiState
import com.example.ui.viewmodel.SelectedDocument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    loanUiState: LoanUiState,
    pangleState: PangleSdkState,
    activeLoans: List<LoanApplicationEntity>,
    onAmountChange: (Double) -> Unit,
    onDurationChange: (Int) -> Unit,
    onPurposeChange: (String) -> Unit,
    onApplicantInfoChange: (String, String, String) -> Unit,
    onDocumentSelected: (DocumentType, Uri, String, String) -> Unit,
    onDocumentRemoved: (DocumentType) -> Unit,
    onApplyAdDiscount: () -> Unit,
    onSubmitLoan: () -> Unit,
    onRepayLoan: (Long) -> Unit,
    onRefreshBanner: () -> Unit,
    onBannerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Launcher for National ID file upload
    val idLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "national_id.pdf"
            onDocumentSelected(DocumentType.NATIONAL_ID, it, fileName, "1.4 MB")
        }
    }

    // Launcher for Bank/MPesa statement file upload
    val statementLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "mpesa_statement.pdf"
            onDocumentSelected(DocumentType.BANK_STATEMENT, it, fileName, "2.8 MB")
        }
    }

    val interestRate = 0.05 * (1.0 - (loanUiState.interestDiscountPercent / 100.0))
    val interestFee = loanUiState.selectedAmountKsh * interestRate
    val totalRepayment = loanUiState.selectedAmountKsh + interestFee
    val dailyFee = interestFee / loanUiState.durationDays.toDouble()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Branding Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_brand_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_mkopo),
                        contentDescription = "Mkopo Haraka Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.45f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFB300)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = Color(0xFF00382E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MKOPO HARAKA",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Instant Micro-Loans & Pangle Monetization",
                                        fontSize = 11.sp,
                                        color = Color(0xFF80CBC4)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00796B))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Credit Score: 765 A+",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚡ Instant MPesa Approval",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "🔒 256-Bit Encrypted",
                                fontSize = 11.sp,
                                color = Color(0xFFB2DFDB)
                            )
                        }
                    }
                }
            }
        }

        // Top Pangle Banner Ad Widget
        item {
            PangleBannerAdWidget(
                pangleState = pangleState,
                onRefreshAd = onRefreshBanner,
                onAdClicked = onBannerClicked
            )
        }

        // Loan Calculator Card
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
                        Text(
                            text = "Select Loan Amount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "KSh ${"%,.0f".format(loanUiState.selectedAmountKsh)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF00695C)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = loanUiState.selectedAmountKsh.toFloat(),
                        onValueChange = { onAmountChange(it.toDouble()) },
                        valueRange = 1000f..50000f,
                        steps = 49,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00695C),
                            activeTrackColor = Color(0xFF00897B),
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("loan_amount_slider")
                    )

                    // Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(5000.0, 15000.0, 30000.0, 50000.0).forEach { amount ->
                            FilterChip(
                                selected = loanUiState.selectedAmountKsh == amount,
                                onClick = { onAmountChange(amount) },
                                label = { Text("KSh ${"%.0f".format(amount / 1000)}k", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00695C),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Repayment Duration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(14, 30, 60).forEach { days ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (loanUiState.durationDays == days) Color(0xFF00695C) else Color(
                                            0xFFF1F5F9
                                        )
                                    )
                                    .clickable { onDurationChange(days) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$days Days",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (loanUiState.durationDays == days) Color.White else Color(
                                        0xFF475569
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Repayment Details Summary Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE0F2F1)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Principal Amount", fontSize = 12.sp, color = Color(0xFF004D40))
                                Text("KSh ${"%,.0f".format(loanUiState.selectedAmountKsh)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF004D40))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Interest Fee", fontSize = 12.sp, color = Color(0xFF004D40))
                                    if (loanUiState.interestDiscountPercent > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFD32F2F))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("-50% AD OFF", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text("KSh ${"%,.0f".format(interestFee)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF004D40))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = Color(0xFFB2DFDB))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Repayment Due", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00382E))
                                Text("KSh ${"%,.0f".format(totalRepayment)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00695C))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rewarded Ad Incentive Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onApplyAdDiscount() }
                            .testTag("rewarded_ad_discount_btn"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC107))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Watch Pangle Video Ad",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF5D4037)
                                    )
                                    Text(
                                        text = "Unlock 50% discount on loan interest fee",
                                        fontSize = 10.sp,
                                        color = Color(0xFF795548)
                                    )
                                }
                            }

                            if (pangleState.rewardedState == com.example.pangle.AdState.LOADING) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF57F17))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("CLAIM -50%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Supporting Documents Upload Card (MANDATORY REQUIREMENT)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Required Supporting Documents",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(
                        text = "Attach copy of National ID / Passport and MPesa / Bank statement for instant verification.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // National ID Document Upload Item
                    DocumentUploadItem(
                        title = "1. National ID / Passport",
                        document = loanUiState.nationalIdDoc,
                        onUploadClick = { idLauncher.launch("*/*") },
                        onRemoveClick = { onDocumentRemoved(DocumentType.NATIONAL_ID) },
                        testTag = "upload_national_id_btn"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bank / MPesa Statement Upload Item
                    DocumentUploadItem(
                        title = "2. MPesa / Bank Statement",
                        document = loanUiState.statementDoc,
                        onUploadClick = { statementLauncher.launch("*/*") },
                        onRemoveClick = { onDocumentRemoved(DocumentType.BANK_STATEMENT) },
                        testTag = "upload_statement_btn"
                    )
                }
            }
        }

        // Applicant Personal Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Applicant Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loanUiState.applicantName,
                        onValueChange = { onApplicantInfoChange(it, loanUiState.phone, loanUiState.idNumber) },
                        label = { Text("Full Legal Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("applicant_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = loanUiState.phone,
                            onValueChange = { onApplicantInfoChange(loanUiState.applicantName, it, loanUiState.idNumber) },
                            label = { Text("MPesa Phone") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("applicant_phone_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = loanUiState.idNumber,
                            onValueChange = { onApplicantInfoChange(loanUiState.applicantName, loanUiState.phone, it) },
                            label = { Text("ID Number") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("applicant_id_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Submit Application Action Button
        item {
            Button(
                onClick = onSubmitLoan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_loan_application_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00695C)
                )
            ) {
                if (loanUiState.isSubmitting || pangleState.interstitialState == com.example.pangle.AdState.LOADING) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Triggering Pangle Ad & Processing Loan...", fontSize = 14.sp)
                } else {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APPLY NOW • KSh ${"%,.0f".format(loanUiState.selectedAmountKsh)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active Approved Loans Section
        if (activeLoans.isNotEmpty()) {
            item {
                Text(
                    text = "My Active Loans",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E293B)
                )
            }

            items(activeLoans) { loan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (loan.status == "REPAID") Color(0xFFE8F5E9) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Loan #${loan.id} • ${loan.applicantName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Amount: KSh ${"%,.0f".format(loan.loanAmount)} • Repayment: KSh ${"%,.0f".format(loan.totalRepayment)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (loan.status == "REPAID") Color(0xFF2E7D32) else Color(0xFF0288D1))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = loan.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (loan.status != "REPAID") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onRepayLoan(loan.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("REPAY VIA MPESA NOW", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun DocumentUploadItem(
    title: String,
    document: SelectedDocument?,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (document != null) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (document != null) Color(0xFF81C784) else Color(0xFFCBD5E1)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (document != null) Icons.Default.CheckCircle else Icons.Default.Description,
                    contentDescription = null,
                    tint = if (document != null) Color(0xFF2E7D32) else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = document?.fileName ?: "No file selected",
                        fontSize = 11.sp,
                        color = if (document != null) Color(0xFF2E7D32) else Color(0xFF94A3B8),
                        fontWeight = if (document != null) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }

            if (document != null) {
                IconButton(onClick = onRemoveClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove File",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag(testTag),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00695C))
                ) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = it.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}
