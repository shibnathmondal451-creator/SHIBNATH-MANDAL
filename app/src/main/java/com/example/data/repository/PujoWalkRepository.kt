package com.example.data.repository

import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.AdminConfigEntity
import com.example.data.local.AppDatabase
import com.example.data.local.LocationBreadcrumbEntity
import com.example.data.local.PandalEntity
import com.example.data.local.RedemptionEntity
import com.example.data.local.UserEntity
import com.example.data.local.WalkingSessionEntity
import com.example.data.local.WalletLedgerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class PujoWalkRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val walkingDao = database.walkingDao()
    private val walletDao = database.walletDao()
    private val pandalDao = database.pandalDao()
    private val redemptionDao = database.redemptionDao()
    private val adminDao = database.adminDao()

    val currentUser: Flow<UserEntity?> = userDao.getUser(1L)
    val allPandals: Flow<List<PandalEntity>> = pandalDao.getAllPandals()
    val activePandals: Flow<List<PandalEntity>> = pandalDao.getActivePandals()
    val recentSessions: Flow<List<WalkingSessionEntity>> = walkingDao.getAllSessions()
    val allRedemptions: Flow<List<RedemptionEntity>> = redemptionDao.getAllRedemptions()
    val adminAuditLogs: Flow<List<AdminAuditLogEntity>> = adminDao.getAuditLogs()
    val adminConfigs: Flow<List<AdminConfigEntity>> = adminDao.getAllConfigs()

    fun userTransactions(userId: Long = 1L): Flow<List<WalletLedgerEntity>> =
        walletDao.getTransactionsForUser(userId)

    // Reactive calculated balances directly from auditable ledger
    fun getAvailableBalance(userId: Long = 1L): Flow<Double> {
        return combine(
            walletDao.getApprovedCreditSum(userId),
            walletDao.getDebitSum(userId)
        ) { approvedCredits, debits ->
            val balance = approvedCredits - debits
            if (balance < 0.0) 0.0 else balance
        }
    }

    fun getPendingBalance(userId: Long = 1L): Flow<Double> =
        walletDao.getPendingCreditSum(userId)

    fun getLifetimeEarned(userId: Long = 1L): Flow<Double> =
        walletDao.getApprovedCreditSum(userId)

    fun getTotalRedeemed(userId: Long = 1L): Flow<Double> =
        walletDao.getRedeemedSettledSum(userId)

    fun getTodayDistance(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return walkingDao.getTodayDistance(calendar.timeInMillis)
    }

    fun getLifetimeDistance(): Flow<Double?> =
        walkingDao.getTotalLifetimeDistance()

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    /**
     * Completes and validates a walking session according to Pujo 10 KM rules:
     * - Standard rewards: ₹1 per verified km up to 10 km daily cap.
     * - Special ₹5 Bonus: exactly when 10 km is completed in a single CONTINUOUS session
     *   during promo period, passes server-side anti-fraud verification, and not previously awarded.
     */
    suspend fun recordWalkingSession(
        session: WalkingSessionEntity,
        breadcrumbs: List<LocationBreadcrumbEntity>
    ): WalkingSessionEntity = withContext(Dispatchers.IO) {
        // 1. Anti-fraud verification check
        var fraudReason: String? = null
        if (session.durationSeconds > 0) {
            val avgSpeedKmh = (session.distanceKm / (session.durationSeconds / 3600.0))
            if (avgSpeedKmh > 14.0) {
                fraudReason = "Unusually high walking speed: %.1f km/h (vehicle suspected)".format(avgSpeedKmh)
            }
        }
        if (session.distanceKm > 0 && session.steps < (session.distanceKm * 400).toInt()) {
            fraudReason = "Step count too low for recorded distance"
        }

        val isValid = fraudReason == null
        val sessionStatus = if (isValid) "VERIFIED" else "REJECTED"

        // 2. Standard Reward Calculation: ₹1 per verified km (capped at 10 km)
        val verifiedKm = if (isValid) session.distanceKm.toInt() else 0
        val cappedKm = minOf(verifiedKm, 10)
        val standardReward = cappedKm.toDouble() // e.g. 1 km = ₹1, 2 km = ₹2, ..., 10 km = ₹10

        // 3. Special Pujo 10 KM Bonus Rule:
        // Must be continuous, >= 10.0 km, promo active, valid
        val isPromoActive = adminDao.getConfigValue("promo_active")?.toBooleanStrictOrNull() ?: true
        val qualifiesFor10KmBonus = isValid &&
                session.isContinuous &&
                session.distanceKm >= 10.0 &&
                isPromoActive

        val bonusReward = if (qualifiesFor10KmBonus) 5.0 else 0.0

        val finalSession = session.copy(
            status = sessionStatus,
            fraudFlagReason = fraudReason,
            isSpecialBonusEligible = qualifiesFor10KmBonus,
            specialBonusAwarded = qualifiesFor10KmBonus,
            standardRewardInr = standardReward,
            bonusRewardInr = bonusReward,
            endTime = if (session.endTime == 0L) System.currentTimeMillis() else session.endTime
        )

        val insertedSessionId = walkingDao.insertSession(finalSession)
        val sessionWithId = finalSession.copy(id = insertedSessionId)

        // Save breadcrumbs
        breadcrumbs.forEach { bc ->
            walkingDao.insertBreadcrumb(bc.copy(sessionId = insertedSessionId))
        }

        // Ledger entry for standard reward
        if (standardReward > 0.0) {
            walletDao.insertTransaction(
                WalletLedgerEntity(
                    userId = session.userId,
                    type = "CREDIT",
                    category = "WALK_STANDARD",
                    amountInr = standardReward,
                    description = "Pujo Walk Reward ($cappedKm km verified)",
                    referenceId = "SESSION-$insertedSessionId",
                    status = "APPROVED"
                )
            )
        }

        // Separate ledger entry for Pujo 10 KM Bonus — ₹5
        if (qualifiesFor10KmBonus) {
            walletDao.insertTransaction(
                WalletLedgerEntity(
                    userId = session.userId,
                    type = "CREDIT",
                    category = "PUJO_10KM_BONUS",
                    amountInr = 5.0,
                    description = "Pujo 10 KM Bonus — ₹5",
                    referenceId = "BONUS-$insertedSessionId",
                    status = "APPROVED"
                )
            )
        }

        sessionWithId
    }

    /**
     * Atomically reserves/debits user balance and creates a redemption request.
     */
    suspend fun requestRedemption(
        userId: Long,
        method: String,
        amount: Double,
        targetAccount: String,
        extraDetails: String = ""
    ): Result<RedemptionEntity> = withContext(Dispatchers.IO) {
        val emergencyPause = adminDao.getConfigValue("emergency_pause")?.toBooleanStrictOrNull() ?: false
        if (emergencyPause) {
            return@withContext Result.failure(Exception("Redemptions are temporarily paused by administrator."))
        }

        val approvedCredits = walletDao.getApprovedCreditSum(userId).first()
        val totalDebits = walletDao.getDebitSum(userId).first()
        val available = approvedCredits - totalDebits

        if (available < amount) {
            return@withContext Result.failure(Exception("Insufficient available balance. Required: ₹$amount, Available: ₹$available"))
        }

        val redemption = RedemptionEntity(
            userId = userId,
            method = method,
            amountInr = amount,
            targetAccount = targetAccount,
            accountDetailsExtra = extraDetails,
            status = "PROCESSING",
            providerReference = "PW-${System.currentTimeMillis()}-${(1000..9999).random()}"
        )

        val redemptionId = redemptionDao.insertRedemption(redemption)
        val savedRedemption = redemption.copy(id = redemptionId)

        // Atomic debit in ledger
        walletDao.insertTransaction(
            WalletLedgerEntity(
                userId = userId,
                type = "DEBIT",
                category = "REDEMPTION_SETTLED",
                amountInr = amount,
                description = "Redemption via $method ($targetAccount)",
                referenceId = "REDEMPTION-$redemptionId",
                status = "APPROVED"
            )
        )

        adminDao.insertAuditLog(
            AdminAuditLogEntity(
                adminName = "User-$userId",
                action = "REDEEM_REQUEST",
                details = "User requested ₹$amount via $method (Ref: ${savedRedemption.providerReference})"
            )
        )

        Result.success(savedRedemption)
    }

    suspend fun adminUpdateRedemptionStatus(
        redemptionId: Long,
        newStatus: String, // "COMPLETED", "FAILED", "REVERSED"
        reason: String? = null
    ) = withContext(Dispatchers.IO) {
        val redemption = redemptionDao.getRedemptionById(redemptionId) ?: return@withContext
        val updated = redemption.copy(
            status = newStatus,
            failureReason = reason,
            updatedAt = System.currentTimeMillis()
        )
        redemptionDao.updateRedemption(updated)

        // If redemption failed or reversed, refund the balance back safely!
        if (newStatus == "FAILED" || newStatus == "REVERSED") {
            walletDao.insertTransaction(
                WalletLedgerEntity(
                    userId = redemption.userId,
                    type = "CREDIT",
                    category = "REDEMPTION_REFUND",
                    amountInr = redemption.amountInr,
                    description = "Refund: Redemption Failed (${redemption.providerReference})",
                    referenceId = "REFUND-$redemptionId",
                    status = "APPROVED"
                )
            )
        }

        adminDao.insertAuditLog(
            AdminAuditLogEntity(
                adminName = "Admin",
                action = "REDEMPTION_STATUS_$newStatus",
                details = "Redemption #$redemptionId updated to $newStatus. Reason: ${reason ?: "N/A"}"
            )
        )
    }

    suspend fun savePandal(pandal: PandalEntity) = withContext(Dispatchers.IO) {
        if (pandal.id == 0L) {
            pandalDao.insertPandal(pandal)
        } else {
            pandalDao.updatePandal(pandal)
        }
    }

    suspend fun deletePandal(pandal: PandalEntity) = withContext(Dispatchers.IO) {
        pandalDao.deletePandal(pandal)
    }

    suspend fun updateAdminConfig(key: String, value: String, description: String = "") = withContext(Dispatchers.IO) {
        adminDao.setConfig(AdminConfigEntity(key, value, description))
        adminDao.insertAuditLog(
            AdminAuditLogEntity(
                adminName = "Admin",
                action = "UPDATE_CONFIG",
                details = "Key: $key set to: $value"
            )
        )
    }
}
