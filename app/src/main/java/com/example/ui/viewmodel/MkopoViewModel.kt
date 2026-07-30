package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdLogEntity
import com.example.data.AppDatabase
import com.example.data.LoanApplicationEntity
import com.example.pangle.PangleManager
import com.example.pangle.PangleSdkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SelectedDocument(
    val uri: Uri,
    val fileName: String,
    val fileSizeFormatted: String,
    val docType: DocumentType
)

enum class DocumentType(val label: String) {
    NATIONAL_ID("National ID / Passport"),
    BANK_STATEMENT("MPesa / Bank Statement")
}

data class LoanUiState(
    val selectedAmountKsh: Double = 15000.0,
    val durationDays: Int = 30,
    val purpose: String = "Business Capital",
    val applicantName: String = "John Kamao",
    val phone: String = "+254 712 345 678",
    val idNumber: String = "32984102",
    val nationalIdDoc: SelectedDocument? = null,
    val statementDoc: SelectedDocument? = null,
    val isSubmitting: Boolean = false,
    val submissionMessage: String? = null,
    val showAdDiscountApplied: Boolean = false,
    val interestDiscountPercent: Int = 0
)

data class AnalyticsSummary(
    val totalRevenueUsd: Double = 0.0,
    val totalRevenueKsh: Double = 0.0,
    val impressionCount: Int = 0,
    val clickCount: Int = 0,
    val ctrPercent: Double = 0.0,
    val avgEcpmUsd: Double = 0.0,
    val fillRatePercent: Double = 98.4
)

class MkopoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val loanDao = db.loanDao()
    private val adLogDao = db.adLogDao()

    val pangleManager = PangleManager(application)
    val pangleState: StateFlow<PangleSdkState> = pangleManager.state

    val loanUiState = MutableStateFlow(LoanUiState())
    val allLoans: StateFlow<List<LoanApplicationEntity>> = loanDao.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adLogs: StateFlow<List<AdLogEntity>> = adLogDao.getAllAdLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        adLogDao.getAllAdLogs(),
        adLogDao.getTotalRevenueUsd(),
        adLogDao.getImpressionCount(),
        adLogDao.getClickCount()
    ) { logs, totalRev, impressions, clicks ->
        val revUsd = totalRev ?: 0.0
        val revKsh = revUsd * 132.5 // Approximate USD to KSh rate
        val imp = impressions
        val clk = clicks
        val ctr = if (imp > 0) (clk.toDouble() / imp.toDouble()) * 100.0 else 0.0
        val avgEcpm = if (imp > 0) (revUsd / imp.toDouble()) * 1000.0 else 2.45

        AnalyticsSummary(
            totalRevenueUsd = revUsd,
            totalRevenueKsh = revKsh,
            impressionCount = imp,
            clickCount = clk,
            ctrPercent = ctr,
            avgEcpmUsd = avgEcpm,
            fillRatePercent = if (logs.isNotEmpty()) 98.6 else 100.0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary())

    init {
        // Seed default sample active loan if database is empty for rich initial UI
        viewModelScope.launch {
            try {
                val currentLoans = loanDao.getAllLoans().first()
                if (currentLoans.isEmpty()) {
                    loanDao.insertLoan(
                        LoanApplicationEntity(
                            applicantName = "John Kamao",
                            phone = "+254 712 345 678",
                            idNumber = "32984102",
                            loanAmount = 15000.0,
                            durationDays = 30,
                            purpose = "Business Capital",
                            idDocumentName = "national_id_copy.pdf",
                            statementName = "mpesa_statement_6m.pdf",
                            status = "APPROVED",
                            creditScore = 765,
                            totalRepayment = 15750.0,
                            dailyFee = 25.0
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAmount(amount: Double) {
        loanUiState.value = loanUiState.value.copy(selectedAmountKsh = amount)
    }

    fun updateDuration(days: Int) {
        loanUiState.value = loanUiState.value.copy(durationDays = days)
    }

    fun updatePurpose(purpose: String) {
        loanUiState.value = loanUiState.value.copy(purpose = purpose)
    }

    fun updateApplicantInfo(name: String, phone: String, idNum: String) {
        loanUiState.value = loanUiState.value.copy(
            applicantName = name,
            phone = phone,
            idNumber = idNum
        )
    }

    fun setDocument(type: DocumentType, uri: Uri, fileName: String, fileSize: String) {
        val doc = SelectedDocument(uri, fileName, fileSize, type)
        if (type == DocumentType.NATIONAL_ID) {
            loanUiState.value = loanUiState.value.copy(nationalIdDoc = doc)
        } else {
            loanUiState.value = loanUiState.value.copy(statementDoc = doc)
        }
    }

    fun removeDocument(type: DocumentType) {
        if (type == DocumentType.NATIONAL_ID) {
            loanUiState.value = loanUiState.value.copy(nationalIdDoc = null)
        } else {
            loanUiState.value = loanUiState.value.copy(statementDoc = null)
        }
    }

    fun applyAdInterestDiscount() {
        pangleManager.loadAndShowRewarded { revenueUsd ->
            loanUiState.value = loanUiState.value.copy(
                interestDiscountPercent = 50,
                showAdDiscountApplied = true,
                submissionMessage = "🎉 Pangle Rewarded Ad watched! 50% Interest Discount applied to your loan!"
            )
        }
    }

    fun submitLoanApplication() {
        val currentState = loanUiState.value
        viewModelScope.launch {
            loanUiState.value = currentState.copy(isSubmitting = true)

            // Trigger Pangle Interstitial Ad on loan application submission!
            pangleManager.loadAndShowInterstitial {
                viewModelScope.launch {
                    val interestRate = 0.05 * (1.0 - (currentState.interestDiscountPercent / 100.0))
                    val totalInterest = currentState.selectedAmountKsh * interestRate
                    val totalRepay = currentState.selectedAmountKsh + totalInterest
                    val dailyFee = totalInterest / currentState.durationDays.toDouble()

                    val newLoan = LoanApplicationEntity(
                        applicantName = currentState.applicantName.ifBlank { "John Kamao" },
                        phone = currentState.phone.ifBlank { "+254 712 345 678" },
                        idNumber = currentState.idNumber.ifBlank { "32984102" },
                        loanAmount = currentState.selectedAmountKsh,
                        durationDays = currentState.durationDays,
                        purpose = currentState.purpose,
                        idDocumentUri = currentState.nationalIdDoc?.uri?.toString(),
                        idDocumentName = currentState.nationalIdDoc?.fileName ?: "national_id_copy.pdf",
                        statementUri = currentState.statementDoc?.uri?.toString(),
                        statementName = currentState.statementDoc?.fileName ?: "mpesa_statement.pdf",
                        status = "APPROVED",
                        creditScore = 765,
                        totalRepayment = totalRepay,
                        dailyFee = dailyFee
                    )

                    loanDao.insertLoan(newLoan)

                    loanUiState.value = currentState.copy(
                        isSubmitting = false,
                        submissionMessage = "✅ Loan Approved Instant! KSh ${"%.0f".format(currentState.selectedAmountKsh)} sent to MPesa ${currentState.phone}"
                    )
                }
            }
        }
    }

    fun clearSubmissionMessage() {
        loanUiState.value = loanUiState.value.copy(submissionMessage = null)
    }

    fun repayLoan(loanId: Long) {
        viewModelScope.launch {
            loanDao.updateLoanStatus(loanId, "REPAID")
        }
    }
}
