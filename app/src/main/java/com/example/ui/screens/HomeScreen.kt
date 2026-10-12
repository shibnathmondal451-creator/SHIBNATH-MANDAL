package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PandalEntity
import com.example.data.local.UserEntity
import com.example.data.local.WalletLedgerEntity
import com.example.ui.components.PujoChallengeCard
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
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun HomeScreen(
    user: UserEntity?,
    language: AppLanguage,
    todayDistanceKm: Double,
    lifetimeDistanceKm: Double,
    availableBalance: Double,
    pendingBalance: Double,
    totalRedeemed: Double,
    pandals: List<PandalEntity>,
    recentTransactions: List<WalletLedgerEntity>,
    onStartWalkingClick: () -> Unit,
    onExploreClick: () -> Unit,
    onWalletClick: () -> Unit,
    onPandalClick: (PandalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_pujo_banner),
                        contentDescription = "Durga Puja Festive Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay for high text legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        PujoCrimsonDark.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${Strings.get("welcome", language)}, ${user?.name ?: "ভক্ত"}!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = Strings.get("app_tagline", language),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PujoGoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Prominent Start Walking Action Button
        item {
            Button(
                onClick = onStartWalkingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_walking_primary_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PujoGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = PujoCrimsonDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = Strings.get("btn_start_walking", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Live Dashboard Statistics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Today's Distance
                    StatCard(
                        title = Strings.get("today_distance", language),
                        value = "%.2f km".format(todayDistanceKm),
                        subtext = "আজকের অনুমোদন: ₹${todayDistanceKm.toInt()}",
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        iconTint = PujoCrimson,
                        bgColor = Color(0xFFFFECEF),
                        modifier = Modifier.weight(1f)
                    )
                    // Available Balance
                    StatCard(
                        title = Strings.get("available_balance", language),
                        value = "₹%.0f".format(availableBalance),
                        subtext = "অপেক্ষমান: ₹%.0f".format(pendingBalance),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = PujoGoldDark,
                        bgColor = Color(0xFFFEF9C3),
                        modifier = Modifier.weight(1f),
                        onClick = onWalletClick
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Lifetime Distance
                    StatCard(
                        title = Strings.get("lifetime_distance", language),
                        value = "%.1f km".format(lifetimeDistanceKm),
                        subtext = "সর্বমোট পদচারণা",
                        icon = Icons.Default.Stars,
                        iconTint = PujoPurple,
                        bgColor = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )
                    // Total Redeemed
                    StatCard(
                        title = Strings.get("redeemed_total", language),
                        value = "₹%.0f".format(totalRedeemed),
                        subtext = "সফল পেআউট",
                        icon = Icons.Default.CheckCircle,
                        iconTint = SuccessGreen,
                        bgColor = Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f),
                        onClick = onWalletClick
                    )
                }
            }
        }

        // Special Durga Puja 10 KM Challenge Card
        item {
            PujoChallengeCard(
                language = language,
                currentProgressKm = todayDistanceKm,
                onStartWalkClick = onStartWalkingClick
            )
        }

        // Rate Card & Rules
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rate_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PujoCream)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PujoCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("rate_card_title", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PujoCharcoal
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Strings.get("rate_card_desc", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PujoCharcoal.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // Compact Milestone Table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MilestonePill("1 km", "₹1")
                        MilestonePill("3 km", "₹3")
                        MilestonePill("5 km", "₹5")
                        MilestonePill("10 km একনাগাড়ে", "₹15", isBonus = true)
                    }
                }
            }
        }

        // Nearby Pandals Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("nearby_pandals", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PujoCharcoal
                    )
                )
                Text(
                    text = Strings.get("view_all", language),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PujoCrimson,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onExploreClick() }
                        .padding(4.dp)
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pandals.take(6)) { pandal ->
                    MiniPandalCard(
                        pandal = pandal,
                        language = language,
                        onClick = { onPandalClick(pandal) }
                    )
                }
            }
        }

        // Recent Wallet Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("recent_txns", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PujoCharcoal
                    )
                )
                Text(
                    text = Strings.get("view_all", language),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PujoCrimson,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onWalletClick() }
                        .padding(4.dp)
                )
            }
        }

        if (recentTransactions.isEmpty()) {
            item {
                Text(
                    text = "কোনো লেনদেন পাওয়া যায়নি। হাঁটা শুরু করে পুরস্কার অর্জন করুন!",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(recentTransactions.take(3)) { txn ->
                HomeTransactionItem(txn = txn)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PujoCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun MilestonePill(dist: String, reward: String, isBonus: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dist,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = Color.Gray
            )
        )
        Text(
            text = reward,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isBonus) PujoCrimson else SuccessGreen
            )
        )
    }
}

@Composable
private fun MiniPandalCard(
    pandal: PandalEntity,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PujoCrimson,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pandal.zone,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PujoCrimson,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == AppLanguage.BENGALI) pandal.nameBn else pandal.nameEn,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PujoCharcoal
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pandal.locality,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HomeTransactionItem(txn: WalletLedgerEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = txn.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PujoCharcoal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = txn.referenceId,
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val isCredit = txn.type == "CREDIT"
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${"%.0f".format(txn.amountInr)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCredit) SuccessGreen else Color.Red
                    )
                )
                StatusBadge(status = txn.status)
            }
        }
    }
}
