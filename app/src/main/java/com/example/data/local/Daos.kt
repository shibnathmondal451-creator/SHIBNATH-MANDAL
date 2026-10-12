package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUser(id: Long = 1L): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserSync(id: Long = 1L): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    fun getTotalUsersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface WalkingDao {
    @Query("SELECT * FROM walking_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WalkingSessionEntity>>

    @Query("SELECT * FROM walking_sessions WHERE startTime >= :startOfDayTimestamp ORDER BY startTime DESC")
    fun getTodaySessions(startOfDayTimestamp: Long): Flow<List<WalkingSessionEntity>>

    @Query("SELECT * FROM walking_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WalkingSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WalkingSessionEntity): Long

    @Update
    suspend fun updateSession(session: WalkingSessionEntity)

    @Query("SELECT SUM(distanceKm) FROM walking_sessions WHERE status IN ('COMPLETED', 'VERIFIED')")
    fun getTotalLifetimeDistance(): Flow<Double?>

    @Query("SELECT SUM(distanceKm) FROM walking_sessions WHERE startTime >= :startOfDayTimestamp AND status IN ('COMPLETED', 'VERIFIED')")
    fun getTodayDistance(startOfDayTimestamp: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreadcrumb(breadcrumb: LocationBreadcrumbEntity): Long

    @Query("SELECT * FROM location_breadcrumbs WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getBreadcrumbsForSession(sessionId: Long): Flow<List<LocationBreadcrumbEntity>>
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_ledger ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<WalletLedgerEntity>>

    @Query("SELECT * FROM wallet_ledger WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<WalletLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletLedgerEntity): Long

    @Update
    suspend fun updateTransaction(transaction: WalletLedgerEntity)

    // Calculated balances based on auditable ledger
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM wallet_ledger WHERE userId = :userId AND type = 'CREDIT' AND status IN ('APPROVED', 'COMPLETED')")
    fun getApprovedCreditSum(userId: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM wallet_ledger WHERE userId = :userId AND type = 'CREDIT' AND status = 'PENDING'")
    fun getPendingCreditSum(userId: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM wallet_ledger WHERE userId = :userId AND type = 'DEBIT' AND status IN ('APPROVED', 'PROCESSING', 'COMPLETED')")
    fun getDebitSum(userId: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM wallet_ledger WHERE userId = :userId AND type = 'DEBIT' AND status = 'COMPLETED'")
    fun getRedeemedSettledSum(userId: Long): Flow<Double>
}

@Dao
interface PandalDao {
    @Query("SELECT * FROM pandals ORDER BY id ASC")
    fun getAllPandals(): Flow<List<PandalEntity>>

    @Query("SELECT * FROM pandals WHERE isActive = 1 ORDER BY id ASC")
    fun getActivePandals(): Flow<List<PandalEntity>>

    @Query("SELECT * FROM pandals WHERE id = :id LIMIT 1")
    suspend fun getPandalById(id: Long): PandalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPandal(pandal: PandalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPandals(pandals: List<PandalEntity>)

    @Update
    suspend fun updatePandal(pandal: PandalEntity)

    @Delete
    suspend fun deletePandal(pandal: PandalEntity)
}

@Dao
interface RedemptionDao {
    @Query("SELECT * FROM redemptions ORDER BY createdAt DESC")
    fun getAllRedemptions(): Flow<List<RedemptionEntity>>

    @Query("SELECT * FROM redemptions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getRedemptionsForUser(userId: Long): Flow<List<RedemptionEntity>>

    @Query("SELECT * FROM redemptions WHERE id = :id LIMIT 1")
    suspend fun getRedemptionById(id: Long): RedemptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemption(redemption: RedemptionEntity): Long

    @Update
    suspend fun updateRedemption(redemption: RedemptionEntity)
}

@Dao
interface AdminDao {
    @Query("SELECT * FROM admin_configs")
    fun getAllConfigs(): Flow<List<AdminConfigEntity>>

    @Query("SELECT configValue FROM admin_configs WHERE configKey = :key LIMIT 1")
    suspend fun getConfigValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AdminConfigEntity)

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity): Long
}
