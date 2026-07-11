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
fun StrictClockApp(isWakeUp: Boolean = false, challengeType: String = "None", qrCodeData: String = "", qrCodeName: String = "") {
    val context = LocalContext.current
    val hasAllPermissions = remember {
        val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED else true
        val hasCam = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) context.getSystemService(android.app.AlarmManager::class.java)?.canScheduleExactAlarms() == true else true
        hasNotif && hasCam && hasExact
    }
    
    var currentScreen by remember { mutableStateOf(if (isWakeUp) "WakeUp" else if (!hasAllPermissions) "Permissions" else "Clock") }
    var selectedAlarm by remember { mutableStateOf<com.hotaro.strictclock.data.AlarmEntity?>(null) }
    
    val app = context.applicationContext as StrictClockApplication
    val alarmViewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(app.repository, app.scheduler)
    )
    
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        bottomBar = {
            if (currentScreen != "Setup" && currentScreen != "WakeUp" && currentScreen != "Permissions") {
                NavigationBar(
                containerColor = surfaceContainerLowDark,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == "Clock",
                    onClick = { currentScreen = "Clock" },
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
                    onClick = { currentScreen = "Alarms" },
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
                    onClick = { currentScreen = "Timer" },
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
                    onClick = { currentScreen = "Settings" },
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
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
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
                        val soundUri = (context as android.app.Activity).intent.getStringExtra("SOUND_URI") ?: ""
                        val vibrationEnabled = context.intent.getBooleanExtra("VIBRATION_ENABLED", true)
                        
                        WakeUpScreen(
                            challengeType = challengeType, 
                            qrCodeData = qrCodeData, 
                            qrCodeName = qrCodeName,
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
                                
                                context.finish()
                            },
                            onSnoozeAlarm = {
                                val serviceIntent = android.content.Intent(context, com.hotaro.strictclock.service.AlarmService::class.java)
                                context.stopService(serviceIntent)
                                
                                val alarmId = context.intent.getIntExtra("ALARM_ID", -1)
                                val intent = android.content.Intent(context, com.hotaro.strictclock.service.AlarmReceiver::class.java).apply {
                                    putExtra("ALARM_ID", alarmId)
                                    putExtra("CHALLENGE_TYPE", challengeType)
                                    putExtra("SOUND_URI", soundUri)
                                    putExtra("VIBRATION_ENABLED", vibrationEnabled)
                                    putExtra("QR_CODE_DATA", qrCodeData)
                                    putExtra("QR_CODE_NAME", qrCodeName)
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
                                
                                context.finish()
                            }
                        )
                    }
                    "Timer" -> TimerScreen()
                    "Settings" -> SettingsScreen(
                        onNavigateToColorScheme = { currentScreen = "ColorScheme" },
                        onNavigateToThemeMode = { currentScreen = "ThemeMode" },
                        onNavigateToQrManagement = { currentScreen = "QrManagement" },
                        onNavigateToZenMode = { currentScreen = "ZenMode" }
                    )
                    "ColorScheme" -> ColorSchemeScreen(onBack = { currentScreen = "Settings" })
                    "ThemeMode" -> ThemeModeScreen(onBack = { currentScreen = "Settings" })
                    "QrManagement" -> QrManagementScreen(onBack = { currentScreen = "Settings" })
                    "ZenMode" -> ZenModeScreen(onBack = { currentScreen = "Settings" })
                    else -> ClockDashboard(onNavigateToSetup = { currentScreen = "Setup" })
                }
            }
        }
    }
}
