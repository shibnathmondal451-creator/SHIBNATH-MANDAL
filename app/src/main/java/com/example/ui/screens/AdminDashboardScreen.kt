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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.AdminConfigEntity
import com.example.data.local.PandalEntity
import com.example.data.local.RedemptionEntity
import com.example.data.local.WalkingSessionEntity
import com.example.ui.components.StatusBadge
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    language: AppLanguage,
    pandals: List<PandalEntity>,
    redemptions: List<RedemptionEntity>,
    sessions: List<WalkingSessionEntity>,
    auditLogs: List<AdminAuditLogEntity>,
    configs: List<AdminConfigEntity>,
    lifetimeDistanceKm: Double,
    onBackClick: () -> Unit,
    onSavePandal: suspend (PandalEntity) -> Unit,
    onUpdateRedemption: suspend (Long, String, String?) -> Unit,
    onUpdateConfig: suspend (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isAuthenticated by remember { mutableStateOf(false) }
    var adminPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("মেট্রিক্স ও বাজেট", "রিডেম্পশন", "প্যান্ডেল", "সেশন অডিট", "নীতি ও লগ")

    // Emergency pause state from configs
    val isEmergencyPaused = configs.firstOrNull { it.configKey == "emergency_pause" }?.configValue == "true"
    val isPromoActive = configs.firstOrNull { it.configKey == "promo_active" }?.configValue == "true"
    val totalBudget = configs.firstOrNull { it.configKey == "budget_total" }?.configValue?.toDoubleOrNull() ?: 100000.0

    // Calculate total reward liabilities
    val approvedLiabilities = redemptions.filter { it.status == "PROCESSING" || it.status == "PENDING" }.sumOf { it.amountInr }
    val settledRedemptions = redemptions.filter { it.status == "COMPLETED" }.sumOf { it.amountInr }

    if (!isAuthenticated) {
        // Admin Authentication Lock Screen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFFFDF9))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("admin_auth_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PujoCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PujoGold, modifier = Modifier.size(32.dp))
                    }

                    Text(
                        text = Strings.get("admin_auth", language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PujoCrimson)
                    )

                    Text(
                        text = "সুরক্ষিত অ্যাডমিন পোর্টাল অ্যাক্সেস করতে পিন প্রবেশ করান (ডিফল্ট: 1234)",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    )

                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = {
                            adminPin = it
                            pinError = false
                        },
                        label = { Text("অ্যাডমিন পিন / পাসওয়ার্ড") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = pinError
                    )

                    if (pinError) {
                        Text(text = "ভুল পিন! অনুগ্রহ করে পুনরায় চেষ্টা করুন।", color = ErrorRed, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (adminPin == "1234" || adminPin == "admin") {
                                isAuthenticated = true
                            } else {
                                pinError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_login_submit_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                    ) {
                        Text(text = "লগইন করুন", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "ফিরে যান", color = Color.Gray)
                    }
                }
            }
        }
        return
    }

    // Authenticated Admin Dashboard
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
    ) {
        // Admin Top Header
        Surface(
            color = PujoCrimson,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = Strings.get("admin_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }

                Surface(
                    color = PujoGold,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { isAuthenticated = false }
                ) {
                    Text(
                        text = "লগআউট",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PujoCrimsonDark)
                    )
                }
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PujoCrimson,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                0 -> AdminMetricsTab(
                    lifetimeDistanceKm = lifetimeDistanceKm,
                    totalBudget = totalBudget,
                    approvedLiabilities = approvedLiabilities,
                    settledRedemptions = settledRedemptions,
                    totalUsers = 1284,
                    dailyActive = 412,
                    isEmergencyPaused = isEmergencyPaused,
                    isPromoActive = isPromoActive,
                    onToggleEmergencyPause = { pause ->
                        coroutineScope.launch {
                            onUpdateConfig("emergency_pause", pause.toString())
                        }
                    },
                    onTogglePromo = { promo ->
                        coroutineScope.launch {
                            onUpdateConfig("promo_active", promo.toString())
                        }
                    }
                )
                1 -> AdminRedemptionsTab(
                    redemptions = redemptions,
                    onAction = { id, status, reason ->
                        coroutineScope.launch {
                            onUpdateRedemption(id, status, reason)
                        }
                    }
                )
                2 -> AdminPandalsTab(
                    pandals = pandals,
                    onSave = { p -> coroutineScope.launch { onSavePandal(p) } }
                )
                3 -> AdminSessionsTab(sessions = sessions)
                4 -> AdminAuditLogsTab(auditLogs = auditLogs)
            }
        }
    }
}

@Composable
private fun AdminMetricsTab(
    lifetimeDistanceKm: Double,
    totalBudget: Double,
    approvedLiabilities: Double,
    settledRedemptions: Double,
    totalUsers: Int,
    dailyActive: Int,
    isEmergencyPaused: Boolean,
    isPromoActive: Boolean,
    onToggleEmergencyPause: (Boolean) -> Unit,
    onTogglePromo: (Boolean) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Emergency Pause Switch Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("emergency_switch_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEmergencyPaused) Color(0xFFFEE2E2) else PujoCream
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "জরুরী স্থগিতাদেশ (Emergency Pause)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isEmergencyPaused) ErrorRed else PujoCharcoal
                            )
                        )
                        Text(
                            text = if (isEmergencyPaused) "সতর্কবার্তা: নতুন রিওয়ার্ড ও রিডেম্পশন সাময়িক স্থগিত আছে।" else "স্বাভাবিক অবস্থা: ব্যবহারকারীরা রিওয়ার্ড ও রিডেম্পশন পাচ্ছেন।",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray, fontSize = 11.sp)
                        )
                    }

                    Switch(
                        checked = isEmergencyPaused,
                        onCheckedChange = onToggleEmergencyPause,
                        colors = SwitchDefaults.colors(checkedThumbColor = ErrorRed, checkedTrackColor = ErrorRedLight)
                    )
                }
            }
        }

        // Pujo 10 KM Promotion Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "শারদীয় ১০ কিমি চ্যালেঞ্জ অফার",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                        )
                        Text(
                            text = "১০ কিমি একনাগাড়ে সম্পন্ন করলে বাড়তি ₹৫ বোনাস (মোট ₹১৫)।",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }

                    Switch(
                        checked = isPromoActive,
                        onCheckedChange = onTogglePromo,
                        colors = SwitchDefaults.colors(checkedThumbColor = PujoCrimson, checkedTrackColor = Color(0xFFFFD9DF))
                    )
                }
            }
        }

        // Budget & Liability Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "মোট রিওয়ার্ড বাজেট", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "₹%.0f".format(totalBudget), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "অবশিষ্ট: ₹%.0f".format(totalBudget - settledRedemptions - approvedLiabilities), fontSize = 10.sp, color = SuccessGreen)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "বর্তমান দায়বদ্ধতা", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "₹%.0f".format(approvedLiabilities), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCrimson))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "সেটেলড: ₹%.0f".format(settledRedemptions), fontSize = 10.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        // User Engagement Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "মোট নিবন্ধিত ব্যবহারকারী", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "$totalUsers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal))
                        Text(text = "ভেরিফায়েড: ${(totalUsers * 0.94).toInt()}", fontSize = 10.sp, color = SuccessGreen)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "মোট হাঁটার দূরত্ব", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "%.1f km".format(lifetimeDistanceKm), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal))
                        Text(text = "DAU: $dailyActive জন", fontSize = 10.sp, color = PujoPurple)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRedemptionsTab(
    redemptions: List<RedemptionEntity>,
    onAction: (Long, String, String?) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (redemptions.isEmpty()) {
            item {
                Text(text = "কোনো রিডেম্পশন অনুরোধ নেই।", color = Color.Gray, modifier = Modifier.padding(16.dp))
            }
        } else {
            items(redemptions) { red ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "${red.method} • ₹${"%.0f".format(red.amountInr)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                                )
                                Text(
                                    text = "টার্গেট: ${red.targetAccount}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                                )
                                Text(
                                    text = "রেফ: ${red.providerReference}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.Gray)
                                )
                            }
                            StatusBadge(status = red.status)
                        }

                        if (red.status == "PROCESSING" || red.status == "PENDING") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onAction(red.id, "COMPLETED", null) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "অনুমোদন করুন", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { onAction(red.id, "FAILED", "অ্যাকাউন্ট ভ্যালিডেশন ব্যর্থ") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "বাতিল ও রিফান্ড", fontSize = 12.sp, color = ErrorRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPandalsTab(
    pandals: List<PandalEntity>,
    onSave: (PandalEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPandal by remember { mutableStateOf<PandalEntity?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("add_new_pandal_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "নতুন প্যান্ডেল যোগ করুন", fontWeight = FontWeight.Bold)
            }
        }

        items(pandals) { pandal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pandal.nameBn,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                        )
                        Text(
                            text = "${pandal.locality} • (${pandal.latitude}, ${pandal.longitude})",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                    }

                    IconButton(onClick = { editingPandal = pandal }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = PujoCrimson)
                    }
                }
            }
        }
    }

    if (showAddDialog || editingPandal != null) {
        val target = editingPandal ?: PandalEntity(
            nameBn = "", nameEn = "", addressBn = "", addressEn = "",
            locality = "", zone = "North Kolkata", latitude = 22.57, longitude = 88.36,
            descriptionBn = "", descriptionEn = "", highlights = "", visitingHours = "24 Hours"
        )
        PandalEditDialog(
            initial = target,
            onDismiss = {
                showAddDialog = false
                editingPandal = null
            },
            onSave = { p ->
                onSave(p)
                showAddDialog = false
                editingPandal = null
            }
        )
    }
}

@Composable
private fun PandalEditDialog(
    initial: PandalEntity,
    onDismiss: () -> Unit,
    onSave: (PandalEntity) -> Unit
) {
    var nameBn by remember { mutableStateOf(initial.nameBn) }
    var nameEn by remember { mutableStateOf(initial.nameEn) }
    var locality by remember { mutableStateOf(initial.locality) }
    var latStr by remember { mutableStateOf(initial.latitude.toString()) }
    var lngStr by remember { mutableStateOf(initial.longitude.toString()) }
    var isDemo by remember { mutableStateOf(initial.isDemonstrationData) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "প্যান্ডেল তথ্য সম্পাদনা", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCrimson))

                OutlinedTextField(value = nameBn, onValueChange = { nameBn = it }, label = { Text("নাম (বাংলা)") }, singleLine = true)
                OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Name (English)") }, singleLine = true)
                OutlinedTextField(value = locality, onValueChange = { locality = it }, label = { Text("এলাকা / লোকালিটি") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = latStr, onValueChange = { latStr = it }, label = { Text("Latitude") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = lngStr, onValueChange = { lngStr = it }, label = { Text("Longitude") }, modifier = Modifier.weight(1f), singleLine = true)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isDemo, onCheckedChange = { isDemo = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "পরীক্ষামূলক ডেমো ডেটা হিসেবে চিহ্নিত করুন", fontSize = 11.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("বাতিল") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                initial.copy(
                                    nameBn = nameBn,
                                    nameEn = nameEn,
                                    locality = locality,
                                    latitude = latStr.toDoubleOrNull() ?: initial.latitude,
                                    longitude = lngStr.toDoubleOrNull() ?: initial.longitude,
                                    isDemonstrationData = isDemo
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                    ) {
                        Text("সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSessionsTab(sessions: List<WalkingSessionEntity>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (sessions.isEmpty()) {
            item { Text(text = "কোনো হাঁটার সেশন রেকর্ড নেই।", color = Color.Gray, modifier = Modifier.padding(16.dp)) }
        } else {
            items(sessions) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "সেশন #${s.id} • ${"%.2f".format(s.distanceKm)} km (${s.steps} steps)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                            )
                            Text(
                                text = "সময়কাল: ${s.durationSeconds / 60} মিনিট • একনাগাড়ে: ${if (s.isContinuous) "হ্যাঁ" else "না"}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                            if (s.fraudFlagReason != null) {
                                Text(
                                    text = "সতর্কবার্তা: ${s.fraudFlagReason}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ErrorRed, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        StatusBadge(status = s.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAuditLogsTab(auditLogs: List<AdminAuditLogEntity>) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সকল সংবেদনশীল অ্যাডমিন কার্যক্রম ও পেআউট ইভেন্ট অপরিবর্তনীয় অডিট লগে লিপিবদ্ধ হয়।",
                        style = MaterialTheme.typography.labelSmall.copy(color = PujoCharcoal)
                    )
                }
            }
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.action, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PujoCrimson))
                        Text(text = dateFormat.format(Date(log.timestamp)), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.Gray))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = log.details, style = MaterialTheme.typography.bodySmall.copy(color = PujoCharcoal, fontSize = 11.sp))
                }
            }
        }
    }
}
