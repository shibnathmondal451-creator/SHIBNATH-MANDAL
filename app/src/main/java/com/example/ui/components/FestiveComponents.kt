package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedLight
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
import com.example.ui.theme.WarningOrangeLight
import com.example.util.AppLanguage
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestiveTopBar(
    language: AppLanguage,
    availableBalance: Double,
    onLanguageToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("festive_top_bar"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PujoGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Pujo Icon",
                        tint = PujoCrimsonDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = Strings.get("app_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "₹${"%.0f".format(availableBalance)} ব্যালেন্স",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PujoGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        actions = {
            // Login / Switch Account Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onLoginClick() }
                    .testTag("top_bar_login_btn"),
                color = PujoGold,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = "Login",
                        tint = PujoCrimsonDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = Strings.get("login_title", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCrimsonDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Language Switch Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onLanguageToggle() }
                    .testTag("language_toggle_btn"),
                color = PujoCrimsonDark.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = PujoGoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "EN" else "বাংলা",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoGoldLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Admin Portal Shortcut
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("admin_portal_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Portal",
                    tint = PujoGoldLight
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PujoCrimson,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun PujoChallengeCard(
    language: AppLanguage,
    currentProgressKm: Double,
    onStartWalkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentProgressKm / 10.0).coerceIn(0.0, 1.0).toFloat()
    val isComplete = currentProgressKm >= 10.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pujo_challenge_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PujoCrimson, PujoPurple, PujoCrimsonDark)
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = PujoGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get("challenge_title", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Surface(
                        color = PujoGold,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "পুরস্কার ₹১৫",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PujoCrimsonDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = Strings.get("challenge_rule", language),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "%.1f / 10.0 km".format(currentProgressKm),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoGoldLight
                        )
                    )
                    Text(
                        text = if (isComplete) "সম্পূর্ণ! +₹৫ বোনাস আনলকড" else "${(progress * 100).toInt()}% সম্পূর্ণ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isComplete) SuccessGreenLight else Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PujoGold,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("continuous_session_only", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onStartWalkClick() }
                            .testTag("challenge_walk_action_btn"),
                        color = PujoGold,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = Strings.get("btn_start_walking", language),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PujoCrimsonDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "VERIFIED", "APPROVED", "COMPLETED" -> Triple(SuccessGreenLight, SuccessGreen, "অনুমোদিত")
        "PENDING", "PROCESSING", "RECORDING" -> Triple(WarningOrangeLight, WarningOrange, "অপেক্ষমান")
        "REJECTED", "FAILED" -> Triple(ErrorRedLight, ErrorRed, "বাতিল")
        "REVERSED" -> Triple(PujoCream, PujoCharcoal, "রিফান্ডেড")
        else -> Triple(PujoCream, PujoCharcoal, status)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun AppBottomNavigation(
    currentRoute: String,
    language: AppLanguage,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(Screen.Home.route, Icons.Default.Home, Strings.get("nav_home", language)),
            Triple(Screen.Walk.route, Icons.AutoMirrored.Filled.DirectionsWalk, Strings.get("nav_walk", language)),
            Triple(Screen.Explore.route, Icons.Default.LocationOn, Strings.get("nav_explore", language)),
            Triple(Screen.Wallet.route, Icons.Default.AccountBalanceWallet, Strings.get("nav_wallet", language)),
            Triple(Screen.Profile.route, Icons.Default.Person, Strings.get("nav_profile", language))
        )

        items.forEach { (route, icon, label) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PujoCrimson,
                    selectedTextColor = PujoCrimson,
                    indicatorColor = Color(0xFFFFD9DF),
                    unselectedIconColor = PujoCharcoal.copy(alpha = 0.6f),
                    unselectedTextColor = PujoCharcoal.copy(alpha = 0.6f)
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}
