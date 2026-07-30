package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loan_applications ORDER BY timestamp DESC")
    fun getAllLoans(): Flow<List<LoanApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanApplicationEntity): Long

    @Query("UPDATE loan_applications SET status = :newStatus WHERE id = :loanId")
    suspend fun updateLoanStatus(loanId: Long, newStatus: String)

    @Query("DELETE FROM loan_applications")
    suspend fun clearLoans()
}

@Dao
interface AdLogDao {
    @Query("SELECT * FROM ad_logs ORDER BY timestamp DESC")
    fun getAllAdLogs(): Flow<List<AdLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdLog(log: AdLogEntity): Long

    @Query("SELECT COUNT(*) FROM ad_logs WHERE eventType = 'IMPRESSION'")
    fun getImpressionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ad_logs WHERE eventType = 'CLICK'")
    fun getClickCount(): Flow<Int>

    @Query("SELECT SUM(revenueUsd) FROM ad_logs")
    fun getTotalRevenueUsd(): Flow<Double?>

    @Query("DELETE FROM ad_logs")
    suspend fun clearLogs()
}
