package com.hotaro.strictclock.ui


import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.boundsInParent


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
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    
    val settingsScrollState = androidx.compose.foundation.rememberScrollState()
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
    
    val prefs = context.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
    val predictiveBackEnabled = prefs.getBoolean("predictive_back_enabled", false)
    val isRootScreen = currentScreen == "Dashboard" || currentScreen == "Alarms" || currentScreen == "Clock" || currentScreen == "Stopwatch" || currentScreen == "Timer" || currentScreen == "Settings"
    
    if (isRootScreen && !predictiveBackEnabled) {
        val activity = context as? android.app.Activity
        androidx.activity.compose.BackHandler(enabled = true) {
            activity?.finish()
        }
    }
    
    val app = context.applicationContext as StrictClockApplication
    val alarmViewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(app.repository, app.scheduler)
    )
    
    val density = androidx.compose.ui.platform.LocalDensity.current
    var bottomNavOffset by remember { mutableStateOf(0f) }
    val maxBottomNavOffset = with(density) { 120.dp.toPx() } // Hide distance
    val nestedScrollConnection = remember {
        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
            override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: androidx.compose.ui.input.nestedscroll.NestedScrollSource): androidx.compose.ui.geometry.Offset {
                val delta = available.y
                val newOffset = bottomNavOffset - delta
                bottomNavOffset = newOffset.coerceIn(0f, maxBottomNavOffset)
                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }
    
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
                
                containerColor = backgroundDark
            ) { innerPadding ->
                val horizontalMargin = if (isTablet) availableWidth * 0.1f else 0.dp
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = horizontalMargin).nestedScroll(nestedScrollConnection)) {
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
                                    
                                    val historySet = prefs.getStringSet("wake_up_history", setOf())?.toMutableSet() ?: mutableSetOf()
                                    val nowStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                    historySet.add(nowStr)
                                    
                                    prefs.edit()
                                        .putInt("wake_up_streak", newStreak)
                                        .putString("last_streak_date", today)
                                        .putStringSet("wake_up_history", historySet)
                                        .apply()
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
                        onNavigateToWakeUpStreak = { currentScreen = "WakeUpStreak" },
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
                        onNavigateToAppIcons = { currentScreen = "AppIcons" },
                        onNavigateToClockFormat = { currentScreen = "ClockFormat" },
                        scrollState = settingsScrollState
                    )
                    "AppIcons" -> AppIconsScreen(onBack = { currentScreen = "Settings" })
                    "WakeUpStreak" -> WakeUpStreakScreen(onBack = { currentScreen = "Settings" })
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
                    "ClockFormat" -> ClockFormatScreen(onBack = { currentScreen = "Settings" })
                    else -> ClockDashboard(onNavigateToSetup = { currentScreen = "Setup" })
                }
            }
            if (!isTablet && showNavigation) {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = androidx.compose.ui.Alignment.BottomCenter
                ) {
                    val animatedOffset by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = bottomNavOffset, 
                        label = "navOffset",
                        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                    )
                    Box(modifier = Modifier.offset { androidx.compose.ui.unit.IntOffset(0, animatedOffset.toInt()) }) {
                        MainNavigationBar(currentScreen = currentScreen, onNavigate = { currentScreen = it })
                    }
                }
            }
        }
        }
    }
}
}

@Composable
fun MainNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    val destinations = listOf("Clock", "Alarms", "Timer", "Settings")
    val selectedIndex = destinations.indexOf(currentScreen).takeIf { it >= 0 } ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.safeDrawing.only(androidx.compose.foundation.layout.WindowInsetsSides.Bottom))
            .padding(bottom = 16.dp),
        contentAlignment = androidx.compose.ui.Alignment.BottomCenter,
    ) {
        val buttonBounds = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }
        val targetRect = buttonBounds[selectedIndex]
        val button0Rect = buttonBounds[0]
        val pillRelativeX = (targetRect?.left ?: 0f) - (button0Rect?.left ?: 0f)

        val pillAnimatedX by androidx.compose.animation.core.animateFloatAsState(
            targetValue = pillRelativeX,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow,
            ),
            label = "pillX",
        )
        val pillAnimatedWidth by androidx.compose.animation.core.animateFloatAsState(
            targetValue = targetRect?.width ?: 0f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow,
            ),
            label = "pillWidth",
        )
        val pillColor = MaterialTheme.colorScheme.primary

        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 6.dp,
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .height(56.dp)
                    .drawBehind {
                        if (pillAnimatedWidth > 0f) {
                            drawRoundRect(
                                color = pillColor,
                                topLeft = androidx.compose.ui.geometry.Offset(pillAnimatedX, 0f),
                                size = androidx.compose.ui.geometry.Size(pillAnimatedWidth, size.height),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
                            )
                        }
                    },
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    val selected = currentScreen == destination
                    
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { onNavigate(destination) }
                            )
                            .onGloballyPositioned { coords ->
                                buttonBounds[index] = coords.boundsInParent()
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val icon = when (destination) {
                                "Clock" -> if (selected) Icons.Filled.Schedule else Icons.Outlined.Schedule
                                "Alarms" -> if (selected) Icons.Filled.Alarm else Icons.Outlined.Alarm
                                "Timer" -> if (selected) Icons.Filled.Timer else Icons.Outlined.Timer
                                "Settings" -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
                                else -> Icons.Filled.Schedule
                            }
                            
                            val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            
                            Icon(
                                imageVector = icon,
                                contentDescription = destination,
                                modifier = Modifier.size(24.dp),
                                tint = contentColor
                            )
                            
                            androidx.compose.animation.AnimatedVisibility(
                                visible = selected,
                                enter = androidx.compose.animation.expandHorizontally(
                                    animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                                ),
                                exit = androidx.compose.animation.shrinkHorizontally(
                                    animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                                ),
                            ) {
                                Text(
                                    text = destination,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    ),
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
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
