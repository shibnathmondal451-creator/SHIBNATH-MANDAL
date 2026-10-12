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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PujoCharcoal
import com.example.ui.theme.PujoCream
import com.example.ui.theme.PujoCrimson
import com.example.ui.theme.PujoCrimsonDark
import com.example.ui.theme.PujoGold
import com.example.ui.theme.PujoGoldLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun ProfileScreen(
    user: UserEntity?,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLoginClick: () -> Unit,
    onAdminClick: () -> Unit,
    onUpdateUser: (UserEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLegalDocDialog by remember { mutableStateOf<String?>(null) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isAccountDeletedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = Strings.get("nav_profile", language),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PujoCrimson
            )
        )

        // User Avatar & Verification Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("profile_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PujoCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PujoGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "ব্যবহারকারী",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PujoCharcoal
                            )
                        )
                        Text(
                            text = user?.phone ?: "+91 98300 12345",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Text(
                            text = user?.email ?: "soumik.kolkata@example.com",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }

                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = PujoCrimson)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2-Step Verification Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PujoCream, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "মোবাইল ওটিপি যাচাইকৃত", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "আইডি ও শর্তাবলী সম্মত", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            }
        }

        // Login & Account Switch Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_options_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "লগইন ও অ্যাকাউন্ট পরিচালনা",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("profile_switch_account_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                    ) {
                        Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = Strings.get("switch_account", language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("profile_logout_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = PujoCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = Strings.get("btn_logout", language), fontSize = 12.sp, color = PujoCrimson, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Language Selection Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("language_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = PujoCrimson)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ভাষা নির্বাচন (Language)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isBn = language == AppLanguage.BENGALI
                    Button(
                        onClick = { onLanguageChange(AppLanguage.BENGALI) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBn) PujoCrimson else PujoCream
                        )
                    ) {
                        Text(
                            text = "বাংলা (ডিফল্ট)",
                            fontWeight = FontWeight.Bold,
                            color = if (isBn) Color.White else PujoCharcoal
                        )
                    }

                    Button(
                        onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isBn) PujoCrimson else PujoCream
                        )
                    ) {
                        Text(
                            text = "English",
                            fontWeight = FontWeight.Bold,
                            color = if (!isBn) Color.White else PujoCharcoal
                        )
                    }
                }
            }
        }

        // Legal & Compliance Menu
        Card(
            modifier = Modifier.fillMaxWidth().testTag("legal_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "আইনি ও গোপনীয়তা নীতিসমূহ",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                )

                LegalMenuItem(
                    title = "নিয়মাবলী ও শর্তাবলী (Terms & Conditions)",
                    subtitle = "শারদোৎসব হাঁটার রিওয়ার্ড নীতি ও প্রযোজ্য শর্ত",
                    onClick = { showLegalDocDialog = "TERMS" }
                )

                LegalMenuItem(
                    title = "গোপনীয়তা নীতি (Privacy Policy)",
                    subtitle = "জিপিএস অবস্থান ও অ্যাকাউন্ট ডেটা সংরক্ষণ পদ্ধতি",
                    onClick = { showLegalDocDialog = "PRIVACY" }
                )

                LegalMenuItem(
                    title = "লোকেশন ও জিপিএস সম্মতি (Location Consent)",
                    subtitle = "শুধুমাত্র সক্রিয় হাঁটার সময়ে ব্যাকগ্রাউন্ড ট্র্যাকিং",
                    onClick = { showLegalDocDialog = "LOCATION" }
                )
            }
        }

        // Admin Portal Entry
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAdminClick() }
                .testTag("admin_portal_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PujoCream)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(PujoCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PujoGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "অ্যাডমিনিস্ট্রেটর ড্যাশবোর্ড",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                        )
                        Text(
                            text = "বাজেট, প্যান্ডেল ডেটা ও রিডেম্পশন মনিটরিং",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }
                }

                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PujoCrimson)
            }
        }

        // Account Deletion & Data Rights (Legal requirement)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ব্যবহারকারীর ডেটা অধিকার",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PujoCharcoal)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ব্যবহারকারী যেকোনো সময় ব্যক্তিগত ডেটা ডাউনলোড বা অ্যাকাউন্ট সম্পূর্ণ মুছে ফেলার অনুরোধ জমা দিতে পারেন।",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier.fillMaxWidth().testTag("delete_account_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "অ্যাকাউন্ট ডিলিট করার অনুরোধ", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(user?.name ?: "") }
        var phoneInput by remember { mutableStateOf(user?.phone ?: "") }
        var emailInput by remember { mutableStateOf(user?.email ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("প্রোফাইল সম্পাদনা") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("নাম") }, singleLine = true)
                    OutlinedTextField(value = phoneInput, onValueChange = { phoneInput = it }, label = { Text("মোবাইল নম্বর") }, singleLine = true)
                    OutlinedTextField(value = emailInput, onValueChange = { emailInput = it }, label = { Text("ইমেল") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        user?.let {
                            onUpdateUser(it.copy(name = nameInput, phone = phoneInput, email = emailInput))
                        }
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                ) {
                    Text("সংরক্ষণ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Legal Document Modal
    showLegalDocDialog?.let { docType ->
        val (title, content) = when (docType) {
            "TERMS" -> Pair(
                "নিয়মাবলী ও শর্তাবলী (Terms & Conditions)",
                """
                ১. PujoWalk Rewards শারদোৎসবে স্বাস্থ্য সচেতনতা ও ঐতিহ্যবাহী প্যান্ডেল পরিদর্শনের লক্ষ্যে পরিচালিত।
                ২. রিওয়ার্ড রেট: প্রতি ভেরিফায়েড কিলোমিটারে ₹১, দৈনিক সর্বোচ্চ ১০ কিমি পর্যন্ত।
                ৩. পুজো ১০ কিমি চ্যালেঞ্জ: উৎসবের প্রচারকালীন সময়ে একক বিরতিহীন সেশনে ১০ কিমি সম্পূর্ণ করলে অতিরিক্ত ₹৫ বোনাস (মোট ₹১৫)।
                ৪. জিপিএস ও সেন্সর ডেটা দ্বারা গতি বিশ্লেষণ (সর্বোচ্চ ১৪ কিমি/ঘণ্টা) নিশ্চিত করে গাড়ি বা অস্বাভাবিক চলাচল রোধ করা হয়।
                ৫. রিডেম্পশন শুধুমাত্র অনুমোদিত ও বৈধ ব্যাঙ্ক/ইউপিআই অ্যাকাউন্টে সম্পন্ন করা হবে।
                """.trimIndent()
            )
            "PRIVACY" -> Pair(
                "গোপনীয়তা নীতি (Privacy Policy)",
                """
                ১. অবস্থান তথ্য: ব্যবহারকারীর স্পষ্ট অনুমতি সাপেক্ষে কেবলমাত্র হাঁটার সময় লোকেশন সংরক্ষণ করা হয়।
                ২. আমরা ব্যবহারকারীর অবস্থানের তথ্য কখনোই তৃতীয় পক্ষের কাছে বিপণনের উদ্দেশ্যে বিক্রয় করি না।
                ৩. অ্যাকাউন্টের লেনদেন এবং অডিটেবল লেজার ভারতের প্রযোজ্য আর্থিক নিয়মাবলী অনুযায়ী সংরক্ষিত থাকে।
                ৪. ব্যবহারকারী যেকোনো সময় নিজের ডেটা মুছে ফেলতে পারেন।
                """.trimIndent()
            )
            else -> Pair(
                "লোকেশন ও জিপিএস সম্মতি",
                """
                ১. হাঁটার দূরত্ব এবং প্যান্ডেলের নৈকট্য সঠিকভাবে পরিমাপ করতে আপনার ডিভাইসের উচ্চ-নির্ভুল জিপিএস প্রয়োজন।
                ২. যখন আপনি 'হাঁটা শুরু করুন' বোতামে চাপ দেন কেবল তখনই জিপিএস সক্রিয় হয়। হাঁটা শেষ করলে ট্র্যাকিং তৎক্ষণাৎ বন্ধ হয়ে যায়।
                """.trimIndent()
            )
        }

        AlertDialog(
            onDismissRequest = { showLegalDocDialog = null },
            title = { Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PujoCrimson)) },
            text = {
                Text(text = content, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp, color = PujoCharcoal))
            },
            confirmButton = {
                Button(onClick = { showLegalDocDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)) {
                    Text("ঠিক আছে")
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("অ্যাকাউন্ট ডিলিট নিশ্চিতকরণ", color = ErrorRed) },
            text = {
                Text("আপনি কি নিশ্চিতভাবে অ্যাকাউন্ট এবং সমস্ত হাঁটার রেকর্ড মুছে ফেলার অনুরোধ করতে চান? এই পদক্ষেপটি অপরিবর্তনীয়।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        isAccountDeletedNotice = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("বাতিল") }
            }
        )
    }

    if (isAccountDeletedNotice) {
        AlertDialog(
            onDismissRequest = { isAccountDeletedNotice = false },
            title = { Text("অনুরোধ জমা হয়েছে") },
            text = { Text("আপনার অ্যাকাউন্ট ও ডেটা মোছার অনুরোধ সফলভাবে গ্রহণ করা হয়েছে এবং প্রক্রিয়াকরণ করা হবে।") },
            confirmButton = {
                Button(onClick = { isAccountDeletedNotice = false }) { Text("সম্পন্ন") }
            }
        )
    }
}

@Composable
private fun LegalMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = PujoCharcoal))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}
