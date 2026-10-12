package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RedemptionEntity
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PujoCharcoal
import com.example.ui.theme.PujoCream
import com.example.ui.theme.PujoCrimson
import com.example.ui.theme.PujoCrimsonDark
import com.example.ui.theme.PujoGold
import com.example.ui.theme.PujoGoldDark
import com.example.ui.theme.PujoGoldLight
import com.example.ui.theme.PujoPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.WarningOrange
import com.example.util.AppLanguage
import com.example.util.Strings
import kotlinx.coroutines.launch

data class RedemptionOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val minAmount: Double,
    val fee: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val statusLabel: String
)

@Composable
fun RedeemScreen(
    availableBalance: Double,
    language: AppLanguage,
    onBackClick: () -> Unit,
    onSubmitRedemption: suspend (method: String, amount: Double, target: String, extra: String) -> Result<RedemptionEntity>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var selectedMethodId by remember { mutableStateOf("UPI") }
    var amountInput by remember { mutableStateOf("10") }
    var targetAccountInput by remember { mutableStateOf("") }
    var extraDetailsInput by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val options = listOf(
        RedemptionOption("UPI", "UPI ইন্সট্যান্ট পেআউট", "যেকোনো UPI ID (Google Pay, PhonePe, Paytm)", 10.0, "বিনামূল্যে (₹0)", Icons.Default.QrCode, PujoCrimson, "সক্রিয় (RazorpayX Sandbox)"),
        RedemptionOption("BANK", "ব্যাংক অ্যাকাউন্ট ট্রান্সফার", "সরাসরি ব্যাংক অ্যাকাউন্ট (IMPS/NEFT)", 50.0, "বিনামূল্যে (₹0)", Icons.Default.AccountBalance, PujoGoldDark, "সক্রিয় (Payout Gateway)"),
        RedemptionOption("RECHARGE", "মোবাইল রিচার্জ", "Airtel, Jio, Vi, BSNL প্রিপেইড রিচার্জ", 10.0, "বিনামূল্যে (₹0)", Icons.Default.PhoneAndroid, SuccessGreen, "সক্রিয় (BBPS Partner)"),
        RedemptionOption("PLAY_CARD", "Google Play গিফট কার্ড", "ইন-অ্যাপ ও গেমসের জন্য প্লে স্টোর কোড", 10.0, "বিনামূল্যে (₹0)", Icons.Default.CardGiftcard, PujoPurple, "ইমেল ভাউচার ডেলিভারি"),
        RedemptionOption("FLIPKART", "Flipkart গিফট ভাউচার", "অনলাইন শপিংয়ের জন্য ডিজিটাল ভাউচার", 25.0, "বিনামূল্যে (₹0)", Icons.Default.ShoppingBag, Color(0xFF2563EB), "ইমেল ভাউচার ডেলিভারি"),
        RedemptionOption("AMAZON", "Amazon Pay গিফট কার্ড", "Amazon শপিং ও বিল পেমেন্টের জন্য ব্যালেন্স", 25.0, "বিনামূল্যে (₹0)", Icons.Default.CardGiftcard, WarningOrange, "ইমেল ভাউচার ডেলিভারি")
    )

    val currentOption = options.first { it.id == selectedMethodId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PujoCrimson)
            }
            Text(
                text = Strings.get("btn_redeem", language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PujoCrimson
                )
            )
        }

        // Available Balance Pill Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PujoCream)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Strings.get("available_balance", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "₹%.2f".format(availableBalance),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PujoCrimson
                        )
                    )
                }

                Surface(
                    color = PujoGold,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "সর্বনিম্ন ₹${currentOption.minAmount.toInt()}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PujoCrimsonDark)
                    )
                }
            }
        }

        // Choose Redemption Option Cards
        Text(
            text = Strings.get("select_payout_method", language),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
        )

        options.forEach { opt ->
            val isSelected = opt.id == selectedMethodId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        selectedMethodId = opt.id
                        resultMessage = null
                        if (amountInput.toDoubleOrNull() ?: 0.0 < opt.minAmount) {
                            amountInput = opt.minAmount.toInt().toString()
                        }
                    }
                    .testTag("redeem_option_${opt.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFFFECEF) else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) PujoCrimson else Color(0xFFE5E7EB)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(opt.iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = opt.icon, contentDescription = null, tint = opt.iconColor, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opt.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                        )
                        Text(
                            text = opt.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Min ₹${opt.minAmount.toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PujoCrimson)
                        )
                        Text(
                            text = opt.statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SuccessGreen)
                        )
                    }
                }
            }
        }

        // Payout Details Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "রিডেম্পশন অনুরোধ বিবরণ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                )

                // Amount input
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("রিডিম পরিমাণ (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("redeem_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PujoCrimson),
                    singleLine = true
                )

                // Target account input
                val targetLabel = when (selectedMethodId) {
                    "UPI" -> "UPI আইডি (যেমন: 9830012345@paytm বা user@okhdfcbank)"
                    "BANK" -> "ব্যাংক অ্যাকাউন্ট নম্বর"
                    "RECHARGE" -> "১০ ডিজিট মোবাইল নম্বর"
                    else -> "ইমেল ঠিকানা (ভাউচার কোড প্রাপ্তির জন্য)"
                }
                OutlinedTextField(
                    value = targetAccountInput,
                    onValueChange = { targetAccountInput = it },
                    label = { Text(targetLabel) },
                    modifier = Modifier.fillMaxWidth().testTag("redeem_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PujoCrimson),
                    singleLine = true
                )

                // Extra details if Bank or Recharge
                if (selectedMethodId == "BANK") {
                    OutlinedTextField(
                        value = extraDetailsInput,
                        onValueChange = { extraDetailsInput = it },
                        label = { Text("IFSC কোড ও অ্যাকাউন্ট হোল্ডারের নাম") },
                        modifier = Modifier.fillMaxWidth().testTag("redeem_extra_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PujoCrimson),
                        singleLine = true
                    )
                } else if (selectedMethodId == "RECHARGE") {
                    OutlinedTextField(
                        value = extraDetailsInput,
                        onValueChange = { extraDetailsInput = it },
                        label = { Text("টেলিকম অপারেটর (Airtel, Jio, Vi, BSNL) ও সার্কেল") },
                        modifier = Modifier.fillMaxWidth().testTag("redeem_extra_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PujoCrimson),
                        singleLine = true
                    )
                }

                // Security & Privacy Note
                Surface(
                    color = PujoCream,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ব্যাংক পিন, ওটিপি বা পাসওয়ার্ড কখনোই চাওয়া হয় না। ব্যালেন্স তাৎক্ষণিকভাবে লেজারে সংরক্ষিত হয়।",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = PujoCharcoal)
                        )
                    }
                }

                // Status / Error message banner
                resultMessage?.let { msg ->
                    Surface(
                        color = if (isSuccess) SuccessGreenLight else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isSuccess) SuccessGreen else ErrorRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSuccess) SuccessGreen else ErrorRed
                                )
                            )
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        if (amount < currentOption.minAmount) {
                            resultMessage = "সর্বনিম্ন রিডিম পরিমাণ ₹${currentOption.minAmount.toInt()}"
                            isSuccess = false
                            return@Button
                        }
                        if (amount > availableBalance) {
                            resultMessage = "অপর্যাপ্ত ব্যালেন্স! আপনার ব্যালেন্স ₹%.2f".format(availableBalance)
                            isSuccess = false
                            return@Button
                        }
                        if (targetAccountInput.isBlank()) {
                            resultMessage = "অনুগ্রহ করে বৈধ অ্যাকাউন্ট বা মোবাইল/ইমেল তথ্য প্রদান করুন।"
                            isSuccess = false
                            return@Button
                        }

                        isSubmitting = true
                        resultMessage = null

                        coroutineScope.launch {
                            val res = onSubmitRedemption(
                                currentOption.id,
                                amount,
                                targetAccountInput,
                                extraDetailsInput
                            )
                            isSubmitting = false
                            res.onSuccess { red ->
                                isSuccess = true
                                resultMessage = "সফলভাবে রিডিম অনুরোধ জমা হয়েছে! রেফারেন্স আইডি: ${red.providerReference}। পেআউট প্রক্রিয়াধীন।"
                            }.onFailure { err ->
                                isSuccess = false
                                resultMessage = err.message ?: "অনুরোধ প্রক্রিয়া করতে ব্যর্থ হয়েছে।"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_redemption_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = Strings.get("confirm_redeem", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
