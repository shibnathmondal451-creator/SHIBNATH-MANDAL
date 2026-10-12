package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.local.PandalEntity
import com.example.data.repository.PujoWalkRepository
import com.example.service.WalkingTrackerManager
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.FestiveTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ExplorePandalsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PandalDetailSheet
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RedeemScreen
import com.example.ui.screens.WalkingTrackerScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: PujoWalkRepository
    private lateinit var trackerManager: WalkingTrackerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(applicationContext)
        repository = PujoWalkRepository(database)
        trackerManager = WalkingTrackerManager(applicationContext, repository, lifecycleScope)

        setContent {
            MyApplicationTheme {
                MainAppRoot(
                    repository = repository,
                    trackerManager = trackerManager
                )
            }
        }
    }
}

@Composable
fun MainAppRoot(
    repository: PujoWalkRepository,
    trackerManager: WalkingTrackerManager
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation and state
    var currentScreen by remember { mutableStateOf<String>(Screen.Home.route) }
    var selectedLanguage by remember { mutableStateOf(AppLanguage.BENGALI) }
    var isUserRegistered by remember { mutableStateOf(true) }
    var showAuthScreen by remember { mutableStateOf(false) }
    var selectedPandalForModal by remember { mutableStateOf<PandalEntity?>(null) }

    // Database reactive states
    val user by repository.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val availableBalance by repository.getAvailableBalance().collectAsStateWithLifecycle(initialValue = 0.0)
    val pendingBalance by repository.getPendingBalance().collectAsStateWithLifecycle(initialValue = 0.0)
    val lifetimeEarned by repository.getLifetimeEarned().collectAsStateWithLifecycle(initialValue = 0.0)
    val totalRedeemed by repository.getTotalRedeemed().collectAsStateWithLifecycle(initialValue = 0.0)
    val todayDistanceKm by repository.getTodayDistance().collectAsStateWithLifecycle(initialValue = 0.0)
    val lifetimeDistanceKm by repository.getLifetimeDistance().collectAsStateWithLifecycle(initialValue = 0.0)
    val pandals by repository.activePandals.collectAsStateWithLifecycle(initialValue = emptyList())
    val transactions by repository.userTransactions().collectAsStateWithLifecycle(initialValue = emptyList())
    val allRedemptions by repository.allRedemptions.collectAsStateWithLifecycle(initialValue = emptyList())
    val recentSessions by repository.recentSessions.collectAsStateWithLifecycle(initialValue = emptyList())
    val auditLogs by repository.adminAuditLogs.collectAsStateWithLifecycle(initialValue = emptyList())
    val configs by repository.adminConfigs.collectAsStateWithLifecycle(initialValue = emptyList())

    // Live Walking Tracker state
    val trackerState by trackerManager.trackerState.collectAsStateWithLifecycle()

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            trackerManager.startWalk(useSimulation = false)
        } else {
            // Gracefully start simulated Kolkata route trail if location denied
            trackerManager.startWalk(useSimulation = true)
            coroutineScope.launch {
                snackbarHostState.showSnackbar("লোকেশন অনুমতি মেলেনি — কলকাতা প্যান্ডেল রুট ট্রায়াল শুরু হয়েছে!")
            }
        }
    }

    // Handle system back navigation
    BackHandler(enabled = showAuthScreen || currentScreen != Screen.Home.route) {
        if (showAuthScreen) {
            showAuthScreen = false
        } else {
            currentScreen = when (currentScreen) {
                Screen.Redeem.route -> Screen.Wallet.route
                Screen.Admin.route -> Screen.Home.route
                else -> Screen.Home.route
            }
        }
    }

    if (!isUserRegistered) {
        AuthScreen(
            language = selectedLanguage,
            onAuthSuccess = { newUser ->
                coroutineScope.launch {
                    repository.updateUser(newUser)
                    isUserRegistered = true
                }
            }
        )
        return
    }

    Scaffold(
        topBar = {
            if (currentScreen != Screen.Admin.route && currentScreen != Screen.Redeem.route) {
                FestiveTopBar(
                    language = selectedLanguage,
                    availableBalance = availableBalance,
                    onLanguageToggle = {
                        selectedLanguage = if (selectedLanguage == AppLanguage.BENGALI)
                            AppLanguage.ENGLISH else AppLanguage.BENGALI
                    },
                    onLoginClick = { showAuthScreen = true },
                    onAdminClick = { currentScreen = Screen.Admin.route }
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.Admin.route && currentScreen != Screen.Redeem.route) {
                AppBottomNavigation(
                    currentRoute = currentScreen,
                    language = selectedLanguage,
                    onNavigate = { route -> currentScreen = route }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home.route -> {
                    HomeScreen(
                        user = user,
                        language = selectedLanguage,
                        todayDistanceKm = (todayDistanceKm ?: 0.0) + trackerState.distanceKm,
                        lifetimeDistanceKm = (lifetimeDistanceKm ?: 0.0) + trackerState.distanceKm,
                        availableBalance = availableBalance,
                        pendingBalance = pendingBalance,
                        totalRedeemed = totalRedeemed,
                        pandals = pandals,
                        recentTransactions = transactions,
                        onStartWalkingClick = {
                            currentScreen = Screen.Walk.route
                        },
                        onExploreClick = { currentScreen = Screen.Explore.route },
                        onWalletClick = { currentScreen = Screen.Wallet.route },
                        onPandalClick = { p -> selectedPandalForModal = p }
                    )
                }

                Screen.Walk.route -> {
                    WalkingTrackerScreen(
                        trackerState = trackerState,
                        language = selectedLanguage,
                        onStartWalk = { isSimulation ->
                            if (isSimulation) {
                                trackerManager.startWalk(useSimulation = true)
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        onPauseWalk = { trackerManager.pauseWalk() },
                        onResumeWalk = { trackerManager.resumeWalk() },
                        onEndWalk = {
                            trackerManager.endWalk { completedSession ->
                                coroutineScope.launch {
                                    val bonusMsg = if (completedSession.specialBonusAwarded)
                                        "১০ কিমি সম্পন্ন! ₹১০ + ₹৫ বোনাস (মোট ₹১৫) জমা হয়েছে!"
                                    else
                                        "হাঁটার সেশন সম্পন্ন! অর্জিত পুরস্কার: ₹${"%.0f".format(completedSession.standardRewardInr)}"
                                    snackbarHostState.showSnackbar(bonusMsg)
                                }
                            }
                        },
                        onSimulateProgress = { addedKm ->
                            trackerManager.simulateDistanceProgress(addedKm)
                        }
                    )
                }

                Screen.Explore.route -> {
                    ExplorePandalsScreen(
                        pandals = pandals,
                        language = selectedLanguage,
                        userLat = trackerState.currentLat,
                        userLng = trackerState.currentLng
                    )
                }

                Screen.Wallet.route -> {
                    WalletScreen(
                        availableBalance = availableBalance,
                        pendingBalance = pendingBalance,
                        lifetimeEarned = lifetimeEarned,
                        totalRedeemed = totalRedeemed,
                        transactions = transactions,
                        language = selectedLanguage,
                        onRedeemClick = { currentScreen = Screen.Redeem.route }
                    )
                }

                Screen.Redeem.route -> {
                    RedeemScreen(
                        availableBalance = availableBalance,
                        language = selectedLanguage,
                        onBackClick = { currentScreen = Screen.Wallet.route },
                        onSubmitRedemption = { method, amount, target, extra ->
                            val result = repository.requestRedemption(
                                userId = user?.id ?: 1L,
                                method = method,
                                amount = amount,
                                targetAccount = target,
                                extraDetails = extra
                            )
                            result
                        }
                    )
                }

                Screen.Profile.route -> {
                    ProfileScreen(
                        user = user,
                        language = selectedLanguage,
                        onLanguageChange = { selectedLanguage = it },
                        onLoginClick = { showAuthScreen = true },
                        onAdminClick = { currentScreen = Screen.Admin.route },
                        onUpdateUser = { updated ->
                            coroutineScope.launch {
                                repository.updateUser(updated)
                            }
                        }
                    )
                }

                Screen.Admin.route -> {
                    AdminDashboardScreen(
                        language = selectedLanguage,
                        pandals = pandals,
                        redemptions = allRedemptions,
                        sessions = recentSessions,
                        auditLogs = auditLogs,
                        configs = configs,
                        lifetimeDistanceKm = (lifetimeDistanceKm ?: 0.0) + trackerState.distanceKm,
                        onBackClick = { currentScreen = Screen.Home.route },
                        onSavePandal = { p -> repository.savePandal(p) },
                        onUpdateRedemption = { id, status, reason ->
                            repository.adminUpdateRedemptionStatus(id, status, reason)
                        },
                        onUpdateConfig = { key, value ->
                            repository.updateAdminConfig(key, value)
                        }
                    )
                }
            }
        }
    }

    // Auth Screen Overlay for Login & Account Switching
    if (showAuthScreen) {
        AuthScreen(
            language = selectedLanguage,
            onAuthSuccess = { newUser ->
                coroutineScope.launch {
                    repository.updateUser(newUser)
                    showAuthScreen = false
                    isUserRegistered = true
                    snackbarHostState.showSnackbar("স্বাগতম, ${newUser.name}! সফলভাবে লগইন হয়েছে।")
                }
            },
            onDismiss = { showAuthScreen = false }
        )
    }

    // Modal Sheet for Pandal details when clicked from Home
    selectedPandalForModal?.let { p ->
        PandalDetailSheet(
            pandal = p,
            distanceKm = 1.2,
            language = selectedLanguage,
            onDismiss = { selectedPandalForModal = null },
            onDirectionsClick = { selectedPandalForModal = null }
        )
    }
}
