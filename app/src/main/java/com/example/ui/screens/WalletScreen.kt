package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WalletLedgerEntity
import com.example.ui.components.StatusBadge
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
import com.example.util.AppLanguage
import com.example.util.Strings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    availableBalance: Double,
    pendingBalance: Double,
    lifetimeEarned: Double,
    totalRedeemed: Double,
    transactions: List<WalletLedgerEntity>,
    language: AppLanguage,
    onRedeemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "CREDIT" -> transactions.filter { it.type == "CREDIT" }
            "DEBIT" -> transactions.filter { it.type == "DEBIT" }
            "BONUS" -> transactions.filter { it.category == "PUJO_10KM_BONUS" }
            else -> transactions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Text(
                text = Strings.get("wallet_title", language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PujoCrimson
                )
            )
        }

        // Available Balance Primary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PujoCrimson),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = Strings.get("available_balance", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PujoGoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹%.2f".format(availableBalance),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )

                        Button(
                            onClick = onRedeemClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PujoGold),
                            modifier = Modifier.testTag("redeem_rewards_action_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Redeem,
                                contentDescription = null,
                                tint = PujoCrimsonDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.get("btn_redeem", language),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PujoCrimsonDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Balances Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PujoCrimsonDark.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = Strings.get("pending_balance", language),
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "₹%.0f".format(pendingBalance),
                                style = MaterialTheme.typography.titleSmall.copy(color = PujoGold, fontWeight = FontWeight.Bold)
                            )
                        }
                        Column {
                            Text(
                                text = "মোট অর্জিত",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "₹%.0f".format(lifetimeEarned),
                                style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                        Column {
                            Text(
                                text = Strings.get("redeemed_total", language),
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "₹%.0f".format(totalRedeemed),
                                style = MaterialTheme.typography.titleSmall.copy(color = SuccessGreenLight, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Ledger Audit Guarantee Notice
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PujoCream)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সুরক্ষিত অডিটেবল লেজার: ব্যালেন্স রিয়েল-টাইমে ডাটাবেস এন্ট্রির মাধ্যমে হিসাব করা হয়।",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PujoCharcoal,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // Transaction History Header & Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বিস্তারিত লেনদেনের ইতিহাস",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PujoCharcoal
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("সব", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PujoCrimson, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = selectedFilter == "CREDIT",
                        onClick = { selectedFilter = "CREDIT" },
                        label = { Text("ক্রেডিট", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PujoCrimson, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = selectedFilter == "BONUS",
                        onClick = { selectedFilter = "BONUS" },
                        label = { Text("১০ কিমি বোনাস", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PujoCrimson, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        // Ledger List
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো লেনদেন রেকর্ড নেই।",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
            }
        } else {
            items(filteredTransactions) { txn ->
                LedgerDetailCard(txn = txn)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LedgerDetailCard(txn: WalletLedgerEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(txn.createdAt))
    val isCredit = txn.type == "CREDIT"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ledger_txn_${txn.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = txn.description,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "রেফারেন্স: ${txn.referenceId} • $formattedDate",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isCredit) "+" else "-"}₹${"%.2f".format(txn.amountInr)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCredit) SuccessGreen else Color.Red
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(status = txn.status)
                }
            }
        }
    }
}
