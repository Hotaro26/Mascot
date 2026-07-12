package com.hotaro.strictclock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.togetherWith
import com.hotaro.strictclock.StrictClockApplication
import com.hotaro.strictclock.ui.theme.*
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun StrictClockApp(isWakeUp: Boolean = false, challengeType: String = "None", qrCodeData: String = "", qrCodeName: String = "", cameraObject: String = "", mathOperations: String = "", mathDifficulty: String = "") {
    val context = LocalContext.current
    val hasAllPermissions = remember {
        val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED else true
        val hasCam = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) context.getSystemService(android.app.AlarmManager::class.java)?.canScheduleExactAlarms() == true else true
        hasNotif && hasCam && hasExact
    }
    
    var currentScreen by remember { mutableStateOf(if (isWakeUp) "WakeUp" else if (!hasAllPermissions) "Permissions" else "Clock") }
    var selectedAlarm by remember { mutableStateOf<com.hotaro.strictclock.data.AlarmEntity?>(null) }
    
    var activeChallengeType by remember { mutableStateOf(challengeType) }
    var activeQrCodeData by remember { mutableStateOf(qrCodeData) }
    var activeQrCodeName by remember { mutableStateOf(qrCodeName) }
    var activeCameraObject by remember { mutableStateOf(cameraObject) }
    var activeMathOperations by remember { mutableStateOf(mathOperations) }
    var activeMathDifficulty by remember { mutableStateOf(mathDifficulty) }
    var activeSoundUri by remember { mutableStateOf("") }
    var activeVibrationEnabled by remember { mutableStateOf(true) }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (com.hotaro.strictclock.service.AlarmService.isRinging) {
                    currentScreen = "WakeUp"
                    activeChallengeType = com.hotaro.strictclock.service.AlarmService.currentChallengeType
                    activeQrCodeData = com.hotaro.strictclock.service.AlarmService.currentQrCodeData
                    activeQrCodeName = com.hotaro.strictclock.service.AlarmService.currentQrCodeName
                    activeCameraObject = com.hotaro.strictclock.service.AlarmService.currentCameraObject
                    activeMathOperations = com.hotaro.strictclock.service.AlarmService.currentMathOperations
                    activeMathDifficulty = com.hotaro.strictclock.service.AlarmService.currentMathDifficulty
                    activeSoundUri = com.hotaro.strictclock.service.AlarmService.currentSoundUri
                    activeVibrationEnabled = com.hotaro.strictclock.service.AlarmService.currentVibrationEnabled
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    val app = context.applicationContext as StrictClockApplication
    val alarmViewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(app.repository, app.scheduler)
    )
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp
        val availableWidth = maxWidth
        val showNavigation = currentScreen != "Setup" && currentScreen != "WakeUp" && currentScreen != "Permissions"
        
        Row(modifier = Modifier.fillMaxSize()) {
            if (isTablet && showNavigation) {
                MainNavigationRail(currentScreen = currentScreen, onNavigate = { currentScreen = it })
            }
            
            Scaffold(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
                bottomBar = {
                    if (!isTablet && showNavigation) {
                        MainNavigationBar(currentScreen = currentScreen, onNavigate = { currentScreen = it })
                    }
                },
                containerColor = backgroundDark
            ) { innerPadding ->
                val horizontalMargin = if (isTablet) availableWidth * 0.1f else 0.dp
                Box(modifier = Modifier.padding(innerPadding).padding(horizontal = horizontalMargin)) {
            androidx.compose.animation.AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (androidx.compose.animation.scaleIn(
                        initialScale = 0.9f,
                        animationSpec = androidx.compose.animation.core.tween(150)
                    ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(150))) togetherWith (androidx.compose.animation.scaleOut(
                        targetScale = 0.9f,
                        animationSpec = androidx.compose.animation.core.tween(150)
                    ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(150)))
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    "Permissions" -> PermissionsScreen(onComplete = { currentScreen = "Clock" })
                    "Clock" -> ClockOnlyScreen(viewModel = alarmViewModel)
                    "Alarms" -> ClockDashboard(
                        viewModel = alarmViewModel, 
                        onNavigateToSetup = { 
                            selectedAlarm = null
                            currentScreen = "Setup" 
                        },
                        onEditAlarm = { alarm ->
                            selectedAlarm = alarm
                            currentScreen = "Setup"
                        }
                    )
                    "Setup" -> SetupAlarmScreen(viewModel = alarmViewModel, alarm = selectedAlarm, onBack = { currentScreen = "Alarms" })
                    "WakeUp" -> {
                        val activity = context as android.app.Activity
                        val soundUri = if (activeSoundUri.isNotEmpty()) activeSoundUri else activity.intent.getStringExtra("SOUND_URI") ?: ""
                        val vibrationEnabled = if (com.hotaro.strictclock.service.AlarmService.isRinging) activeVibrationEnabled else activity.intent.getBooleanExtra("VIBRATION_ENABLED", true)
                        
                        WakeUpScreen(
                            challengeType = activeChallengeType, 
                            qrCodeData = activeQrCodeData, 
                            qrCodeName = activeQrCodeName,
                            cameraObject = activeCameraObject,
                            mathOperations = activeMathOperations,
                            mathDifficulty = activeMathDifficulty,
                            onStopAlarm = {
                                val serviceIntent = android.content.Intent(context, com.hotaro.strictclock.service.AlarmService::class.java)
                                context.stopService(serviceIntent)
                                
                                val prefs = context.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
                                val currentStreak = prefs.getInt("wake_up_streak", 0)
                                val lastDate = prefs.getString("last_streak_date", "")
                                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                                
                                if (lastDate != today) {
                                    val calendar = java.util.Calendar.getInstance()
                                    calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
                                    val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)
                                    
                                    val newStreak = if (lastDate == yesterday) currentStreak + 1 else 1
                                    prefs.edit().putInt("wake_up_streak", newStreak).putString("last_streak_date", today).apply()
                                }
                                
                                activity.finish()
                            },
                            onSnoozeAlarm = {
                                val serviceIntent = android.content.Intent(context, com.hotaro.strictclock.service.AlarmService::class.java)
                                context.stopService(serviceIntent)
                                
                                val alarmId = if (com.hotaro.strictclock.service.AlarmService.isRinging) com.hotaro.strictclock.service.AlarmService.currentAlarmId else activity.intent.getIntExtra("ALARM_ID", -1)
                                val intent = android.content.Intent(context, com.hotaro.strictclock.service.AlarmReceiver::class.java).apply {
                                    putExtra("ALARM_ID", alarmId)
                                    putExtra("CHALLENGE_TYPE", activeChallengeType)
                                    putExtra("SOUND_URI", soundUri)
                                    putExtra("VIBRATION_ENABLED", vibrationEnabled)
                                    putExtra("QR_CODE_DATA", activeQrCodeData)
                                    putExtra("QR_CODE_NAME", activeQrCodeName)
                                    putExtra("CAMERA_OBJECT", activeCameraObject)
                                    putExtra("MATH_OPERATIONS", activeMathOperations)
                                    putExtra("MATH_DIFFICULTY", activeMathDifficulty)
                                }
                                
                                val pendingIntent = android.app.PendingIntent.getBroadcast(
                                    context, 
                                    alarmId, 
                                    intent, 
                                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                                )
                                
                                val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
                                val triggerTime = System.currentTimeMillis() + 5 * 60 * 1000
                                alarmManager.setAlarmClock(android.app.AlarmManager.AlarmClockInfo(triggerTime, pendingIntent), pendingIntent)
                                
                                activity.finish()
                            }
                        )
                    }
                    "Timer" -> TimerScreen()
                    "Settings" -> SettingsScreen(
                        onNavigateToColorScheme = { currentScreen = "ColorScheme" },
                        onNavigateToThemeMode = { currentScreen = "ThemeMode" },
                        onNavigateToQrManagement = { currentScreen = "QrManagement" },
                        onNavigateToZenMode = { currentScreen = "ZenMode" },
                        onNavigateToCustomisation = { currentScreen = "Customisation" },
                        onNavigateToMathSettings = { currentScreen = "MathSettings" },
                        onNavigateToPuzzles = { currentScreen = "PuzzleSettings" },
                        onNavigateToFlashbang = { currentScreen = "FlashbangSettings" },
                        onNavigateToAiReadiness = { currentScreen = "AiReadiness" },
                        onNavigateToAbout = { currentScreen = "About" },
                        onNavigateToAppIcons = { currentScreen = "AppIcons" }
                    )
                    "AppIcons" -> AppIconsScreen(onBack = { currentScreen = "Settings" })
                    "MathSettings" -> MathSettingsScreen(onBack = { currentScreen = "Settings" })
                    "PuzzleSettings" -> PuzzleSettingsScreen(onBack = { currentScreen = "Settings" })
                    "FlashbangSettings" -> FlashbangSettingsScreen(onBack = { currentScreen = "Settings" })
                    "ColorScheme" -> ColorSchemeScreen(onBack = { currentScreen = "Settings" })
                    "ThemeMode" -> ThemeModeScreen(onBack = { currentScreen = "Settings" })
                    "QrManagement" -> QrManagementScreen(onBack = { currentScreen = "Settings" })
                    "ZenMode" -> ZenModeScreen(onBack = { currentScreen = "Settings" })
                    "Customisation" -> CustomisationScreen(onBack = { currentScreen = "Settings" })
                    "AiReadiness" -> AiReadinessScreen(onBack = { currentScreen = "Settings" })
                    "About" -> AboutScreen(onBack = { currentScreen = "Settings" })
                    else -> ClockDashboard(onNavigateToSetup = { currentScreen = "Setup" })
                }
            }
        }
        }
    }
}
}

@Composable
fun MainNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding(),
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0,0,0,0)
    ) {
            NavigationBarItem(
            selected = currentScreen == "Clock",
            onClick = { onNavigate("Clock") },
            icon = { 
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (currentScreen == "Clock") 180f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(300),
                    label = "ClockRotation"
                )
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Clock") { isSelected ->
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Schedule else Icons.Outlined.Schedule, 
                        contentDescription = "Clock",
                        modifier = Modifier.graphicsLayer(rotationZ = rotation)
                    ) 
                }
            },
            label = { Text("Clock") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationBarItem(
            selected = currentScreen == "Alarms",
            onClick = { onNavigate("Alarms") },
            icon = { 
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Alarms") { isSelected ->
                    Icon(if (isSelected) Icons.Filled.Alarm else Icons.Outlined.Alarm, contentDescription = "Alarms") 
                }
            },
            label = { Text("Alarms") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationBarItem(
            selected = currentScreen == "Timer",
            onClick = { onNavigate("Timer") },
            icon = { 
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Timer") { isSelected ->
                    Icon(if (isSelected) Icons.Filled.Timer else Icons.Outlined.Timer, contentDescription = "Timer") 
                }
            },
            label = { Text("Timer") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationBarItem(
            selected = currentScreen == "Settings",
            onClick = { onNavigate("Settings") },
            icon = { 
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (currentScreen == "Settings") 180f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(300),
                    label = "SettingsRotation"
                )
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Settings") { isSelected ->
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings, 
                        contentDescription = "Settings",
                        modifier = Modifier.graphicsLayer(rotationZ = rotation)
                    ) 
                }
            },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
    }
}

@Composable
fun MainNavigationRail(currentScreen: String, onNavigate: (String) -> Unit) {
    NavigationRail(
        containerColor = surfaceContainerLowDark
    ) {
        Spacer(modifier = Modifier.weight(1f))
        NavigationRailItem(
            selected = currentScreen == "Clock",
            onClick = { onNavigate("Clock") },
            icon = { 
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (currentScreen == "Clock") 180f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(300),
                    label = "ClockRotation"
                )
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Clock") { isSelected ->
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Schedule else Icons.Outlined.Schedule, 
                        contentDescription = "Clock",
                        modifier = Modifier.graphicsLayer(rotationZ = rotation)
                    ) 
                }
            },
            label = { Text("Clock") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationRailItem(
            selected = currentScreen == "Alarms",
            onClick = { onNavigate("Alarms") },
            icon = { 
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Alarms") { isSelected ->
                    Icon(if (isSelected) Icons.Filled.Alarm else Icons.Outlined.Alarm, contentDescription = "Alarms") 
                }
            },
            label = { Text("Alarms") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationRailItem(
            selected = currentScreen == "Timer",
            onClick = { onNavigate("Timer") },
            icon = { 
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Timer") { isSelected ->
                    Icon(if (isSelected) Icons.Filled.Timer else Icons.Outlined.Timer, contentDescription = "Timer") 
                }
            },
            label = { Text("Timer") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        NavigationRailItem(
            selected = currentScreen == "Settings",
            onClick = { onNavigate("Settings") },
            icon = { 
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (currentScreen == "Settings") 180f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(300),
                    label = "SettingsRotation"
                )
                androidx.compose.animation.Crossfade(targetState = currentScreen == "Settings") { isSelected ->
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings, 
                        contentDescription = "Settings",
                        modifier = Modifier.graphicsLayer(rotationZ = rotation)
                    ) 
                }
            },
            label = { Text("Settings") },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = onPrimaryContainerDark,
                selectedTextColor = onSurfaceDark,
                indicatorColor = primaryContainerDark,
                unselectedIconColor = onSurfaceVariantDark,
                unselectedTextColor = onSurfaceVariantDark
            )
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
