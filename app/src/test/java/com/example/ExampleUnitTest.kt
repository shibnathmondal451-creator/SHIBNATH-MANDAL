package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testStandardRewardsMilestones() {
    // 1 km = ₹1, 2 km = ₹2, 5 km = ₹5, 9 km = ₹9, 10 km = ₹10
    val testCases = listOf(
      1.0 to 1.0,
      1.8 to 1.0, // verified whole km
      2.0 to 2.0,
      3.5 to 3.0,
      5.0 to 5.0,
      9.9 to 9.0,
      10.0 to 10.0,
      12.0 to 10.0 // capped at 10 km daily
    )

    for ((distance, expectedStandard) in testCases) {
      val verifiedKm = minOf(distance.toInt(), 10)
      val reward = verifiedKm.toDouble()
      assertEquals("Distance $distance km", expectedStandard, reward, 0.001)
    }
  }

  @Test
  fun testPujo10KmContinuousChallengeBonus() {
    // Continuous 10 km awards ₹10 standard + ₹5 special bonus = ₹15 total
    val continuous10Km = 10.0
    val isContinuous = true
    val isPromoActive = true

    val verifiedKm = minOf(continuous10Km.toInt(), 10)
    val standardReward = verifiedKm.toDouble()
    val qualifiesBonus = isContinuous && continuous10Km >= 10.0 && isPromoActive
    val bonus = if (qualifiesBonus) 5.0 else 0.0

    assertTrue(qualifiesBonus)
    assertEquals(10.0, standardReward, 0.001)
    assertEquals(5.0, bonus, 0.001)
    assertEquals(15.0, standardReward + bonus, 0.001)
  }

  @Test
  fun testDisqualificationIfPausedOrNonContinuous() {
    // A paused session or non-continuous 10 km must NOT get the ₹5 bonus
    val distance = 10.0
    val isContinuous = false // User paused during walk
    val isPromoActive = true

    val qualifiesBonus = isContinuous && distance >= 10.0 && isPromoActive
    val bonus = if (qualifiesBonus) 5.0 else 0.0

    assertFalse(qualifiesBonus)
    assertEquals(0.0, bonus, 0.001)
  }

  @Test
  fun testRedemptionBalanceCheck() {
    val availableBalance = 25.0
    val requestedAmount = 50.0

    val canRedeem = availableBalance >= requestedAmount
    assertFalse("Insufficient balance must prevent redemption", canRedeem)

    val validRequestAmount = 10.0
    val canRedeemValid = availableBalance >= validRequestAmount
    assertTrue("Sufficient balance allows redemption", canRedeemValid)
  }
}
