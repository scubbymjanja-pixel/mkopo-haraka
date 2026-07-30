package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan_applications")
data class LoanApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val applicantName: String,
    val phone: String,
    val idNumber: String,
    val loanAmount: Double,
    val durationDays: Int,
    val purpose: String,
    val idDocumentUri: String? = null,
    val idDocumentName: String? = null,
    val statementUri: String? = null,
    val statementName: String? = null,
    val status: String = "APPROVED", // APPROVED, DISBURSED, REPAID
    val creditScore: Int = 740,
    val totalRepayment: Double,
    val dailyFee: Double
)

@Entity(tableName = "ad_logs")
data class AdLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val adType: String, // BANNER, INTERSTITIAL, REWARDED, NATIVE
    val placementId: String,
    val eventType: String, // LOAD_REQUEST, LOAD_SUCCESS, IMPRESSION, CLICK, REVENUE_EARNED, ERROR
    val ecpmUsd: Double,
    val revenueUsd: Double,
    val errorMessage: String? = null,
    val isTestMode: Boolean = true
)
