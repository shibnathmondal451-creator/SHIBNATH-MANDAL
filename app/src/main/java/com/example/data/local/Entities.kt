package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1L,
    val name: String = "সৌমিক চ্যাটার্জী",
    val phone: String = "+91 98300 12345",
    val email: String = "soumik.kolkata@example.com",
    val isPhoneVerified: Boolean = true,
    val isTermsAccepted: Boolean = true,
    val selectedLanguage: String = "bn",
    val role: String = "USER", // "USER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "walking_sessions")
data class WalkingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val startTime: Long,
    val endTime: Long = 0L,
    val distanceKm: Double = 0.0,
    val steps: Int = 0,
    val durationSeconds: Long = 0L,
    val isContinuous: Boolean = true,
    val status: String = "COMPLETED", // "RECORDING", "PAUSED", "COMPLETED", "VERIFIED", "REJECTED"
    val fraudFlagReason: String? = null,
    val isSpecialBonusEligible: Boolean = false,
    val specialBonusAwarded: Boolean = false,
    val standardRewardInr: Double = 0.0,
    val bonusRewardInr: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "location_breadcrumbs")
data class LocationBreadcrumbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speedKmh: Double,
    val accuracyMeters: Float
)

@Entity(tableName = "wallet_ledger")
data class WalletLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val type: String, // "CREDIT", "DEBIT"
    val category: String, // "WALK_STANDARD", "PUJO_10KM_BONUS", "REDEMPTION_HOLD", "REDEMPTION_SETTLED", "REDEMPTION_REFUND", "ADMIN_ADJUSTMENT"
    val amountInr: Double,
    val description: String,
    val referenceId: String,
    val status: String, // "PENDING", "APPROVED", "COMPLETED", "REJECTED", "REVERSED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pandals")
data class PandalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nameBn: String,
    val nameEn: String,
    val addressBn: String,
    val addressEn: String,
    val locality: String,
    val zone: String, // "North Kolkata", "South Kolkata", "Central Kolkata", "Salt Lake & East"
    val latitude: Double,
    val longitude: Double,
    val descriptionBn: String,
    val descriptionEn: String,
    val highlights: String,
    val visitingHours: String,
    val isDemonstrationData: Boolean = false,
    val isApproved: Boolean = true,
    val isActive: Boolean = true
)

@Entity(tableName = "redemptions")
data class RedemptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val method: String, // "UPI", "BANK_TRANSFER", "MOBILE_RECHARGE", "PLAY_GIFT_CARD", "FLIPKART_GIFT_CARD", "AMAZON_GIFT_CARD"
    val amountInr: Double,
    val targetAccount: String,
    val accountDetailsExtra: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "PROCESSING", "COMPLETED", "FAILED", "REVERSED"
    val providerReference: String = "",
    val failureReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_configs")
data class AdminConfigEntity(
    @PrimaryKey val configKey: String,
    val configValue: String,
    val description: String
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val adminName: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
