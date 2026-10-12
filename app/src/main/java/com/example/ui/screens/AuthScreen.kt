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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.PujoGoldDark
import com.example.ui.theme.PujoGoldLight
import com.example.ui.theme.PujoPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun AuthScreen(
    language: AppLanguage,
    onAuthSuccess: (UserEntity) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // 0 = Login (লগইন), 1 = Register (নতুন নিবন্ধন)
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    // Login state
    var loginPhoneInput by remember { mutableStateOf("+91 98300 12345") }
    var loginOtpInput by remember { mutableStateOf("123456") }
    var isLoginOtpSent by remember { mutableStateOf(false) }

    // Register state
    var registerStep by remember { mutableIntStateOf(1) } // 1 = Phone & OTP, 2 = Profile details
    var regPhoneInput by remember { mutableStateOf("+91 98311 54321") }
    var regOtpInput by remember { mutableStateOf("123456") }
    var isRegOtpSent by remember { mutableStateOf(false) }
    var regNameInput by remember { mutableStateOf("প্রিয়াঙ্কা সেন") }
    var regEmailInput by remember { mutableStateOf("priyanka.saltlake@example.com") }
    var termsAccepted by remember { mutableStateOf(true) }
    var locationConsentAccepted by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .testTag("auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top header with close button if cancelable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PujoCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = PujoGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.get("app_title", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PujoCrimson
                                )
                            )
                            Text(
                                text = Strings.get("app_tagline", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    if (onDismiss != null) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                }

                // Auth Tabs: Login vs Register
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = PujoCream,
                    contentColor = PujoCrimson,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("auth_mode_tabs")
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = {
                            selectedAuthTab = 0
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.get("login_title", language),
                                    fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = {
                            selectedAuthTab = 1
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.get("create_account", language),
                                    fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }

                if (selectedAuthTab == 0) {
                    // ================= LOGIN FLOW =================
                    Text(
                        text = "মোবাইল ওটিপি দিয়ে লগইন করুন",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCharcoal
                        )
                    )

                    OutlinedTextField(
                        value = loginPhoneInput,
                        onValueChange = { loginPhoneInput = it },
                        label = { Text("মোবাইল নম্বর") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PujoCrimson) },
                        modifier = Modifier.fillMaxWidth().testTag("login_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (!isLoginOtpSent) {
                        Button(
                            onClick = {
                                if (loginPhoneInput.length >= 10) {
                                    isLoginOtpSent = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "অনুগ্রহ করে বৈধ মোবাইল নম্বর প্রদান করুন।"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_send_otp_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                        ) {
                            Text(text = "ওটিপি পাঠান (Send OTP)", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = SuccessGreenLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ওটিপি পাঠানো হয়েছে! টেস্ট কোড: 123456",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        OutlinedTextField(
                            value = loginOtpInput,
                            onValueChange = { loginOtpInput = it },
                            label = { Text("৬ ডিজিট ওটিপি কোড") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PujoCrimson) },
                            modifier = Modifier.fillMaxWidth().testTag("login_otp_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (loginOtpInput.trim() == "123456" || loginOtpInput.length == 6) {
                                    onAuthSuccess(
                                        UserEntity(
                                            id = 1L,
                                            name = "সৌমিক চ্যাটার্জী",
                                            phone = loginPhoneInput,
                                            email = "soumik.kolkata@example.com",
                                            isPhoneVerified = true,
                                            isTermsAccepted = true,
                                            selectedLanguage = language.code
                                        )
                                    )
                                } else {
                                    errorMessage = "ভুল ওটিপি কোড! অনুগ্রহ করে 123456 দিন।"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_submit_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text(text = Strings.get("btn_login", language), fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE5E7EB))

                    // Quick One-Tap Demo Logins for Fast Testing
                    Text(
                        text = "বা তাৎক্ষণিক ডেমো প্রোফাইল নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onAuthSuccess(
                                    UserEntity(
                                        id = 1L,
                                        name = "সৌমিক চ্যাটার্জী",
                                        phone = "+91 98300 12345",
                                        email = "soumik.kolkata@example.com",
                                        isPhoneVerified = true,
                                        isTermsAccepted = true,
                                        selectedLanguage = language.code
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("quick_login_soumik_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "সৌমিক চ্যাটার্জী", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PujoCrimson)
                                Text(text = "কলকাতা", fontSize = 9.sp, color = Color.Gray)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                onAuthSuccess(
                                    UserEntity(
                                        id = 2L,
                                        name = "প্রিয়াঙ্কা সেন",
                                        phone = "+91 98311 99887",
                                        email = "priyanka.saltlake@example.com",
                                        isPhoneVerified = true,
                                        isTermsAccepted = true,
                                        selectedLanguage = language.code
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("quick_login_priyanka_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "প্রিয়াঙ্কা সেন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PujoPurple)
                                Text(text = "সল্টলেক", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Google Sign-In Option
                    OutlinedButton(
                        onClick = {
                            onAuthSuccess(
                                UserEntity(
                                    id = 3L,
                                    name = "Shibnath Mondal",
                                    phone = "+91 98000 11223",
                                    email = "shibnathmondal451@gmail.com",
                                    isPhoneVerified = true,
                                    isTermsAccepted = true,
                                    selectedLanguage = language.code
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("google_signin_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = PujoCrimson)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = Strings.get("google_login", language), color = PujoCharcoal, fontWeight = FontWeight.SemiBold)
                    }

                } else {
                    // ================= REGISTER FLOW =================
                    Text(
                        text = if (registerStep == 1) "পদক্ষেপ ১/২: মোবাইল নম্বর ও ওটিপি" else "পদক্ষেপ ২/২: নাম, ইমেল ও শর্তাবলী সম্মতি",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoGoldDark
                        )
                    )

                    if (registerStep == 1) {
                        OutlinedTextField(
                            value = regPhoneInput,
                            onValueChange = { regPhoneInput = it },
                            label = { Text("মোবাইল নম্বর") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PujoCrimson) },
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        if (!isRegOtpSent) {
                            Button(
                                onClick = {
                                    if (regPhoneInput.length >= 10) {
                                        isRegOtpSent = true
                                        errorMessage = null
                                    } else {
                                        errorMessage = "অনুগ্রহ করে বৈধ মোবাইল নম্বর দিন।"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_send_otp_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
                            ) {
                                Text(text = "ওটিপি পাঠান (Send OTP)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = SuccessGreenLight,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "ওটিপি পাঠানো হয়েছে! টেস্ট কোড: 123456",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold)
                                )
                            }

                            OutlinedTextField(
                                value = regOtpInput,
                                onValueChange = { regOtpInput = it },
                                label = { Text("৬ ডিজিট ওটিপি কোড") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PujoCrimson) },
                                modifier = Modifier.fillMaxWidth().testTag("reg_otp_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (regOtpInput.trim() == "123456" || regOtpInput.length == 6) {
                                        registerStep = 2
                                        errorMessage = null
                                    } else {
                                        errorMessage = "ভুল ওটিপি কোড! অনুগ্রহ করে 123456 দিন।"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_verify_otp_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Text(text = "পরবর্তী ধাপ (Next Step)", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = regNameInput,
                            onValueChange = { regNameInput = it },
                            label = { Text("আপনার সম্পূর্ণ নাম") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = regEmailInput,
                            onValueChange = { regEmailInput = it },
                            label = { Text("ইমেল ঠিকানা (রিডেম্পশন ভাউচারের জন্য)") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = termsAccepted,
                                onCheckedChange = { termsAccepted = it },
                                colors = CheckboxDefaults.colors(checkedColor = PujoCrimson)
                            )
                            Text(
                                text = "আমি শারদোৎসব হাঁটার রিওয়ার্ড নীতি ও শর্তাবলীতে সম্মত।",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = PujoCharcoal)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = locationConsentAccepted,
                                onCheckedChange = { locationConsentAccepted = it },
                                colors = CheckboxDefaults.colors(checkedColor = PujoCrimson)
                            )
                            Text(
                                text = "হাঁটার দূরত্ব যাচাইকরণের জন্য জিপিএস লোকেশন ট্র্যাকিংয়ে সম্মত।",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = PujoCharcoal)
                            )
                        }

                        Button(
                            onClick = {
                                if (regNameInput.isNotBlank() && termsAccepted && locationConsentAccepted) {
                                    onAuthSuccess(
                                        UserEntity(
                                            id = 1L,
                                            name = regNameInput,
                                            phone = regPhoneInput,
                                            email = regEmailInput,
                                            isPhoneVerified = true,
                                            isTermsAccepted = true,
                                            selectedLanguage = language.code
                                        )
                                    )
                                } else {
                                    errorMessage = "অনুগ্রহ করে নাম পূরণ করুন এবং উভয় শর্তাবলীতে সম্মত হন।"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_complete_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson),
                            enabled = termsAccepted && locationConsentAccepted
                        ) {
                            Text(text = "নিবন্ধন সম্পূর্ণ করুন ও এগিয়ে যান", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                errorMessage?.let { err ->
                    Text(text = err, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
