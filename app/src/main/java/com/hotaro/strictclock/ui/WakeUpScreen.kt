package com.hotaro.strictclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.challenges.MathChallenge
import com.hotaro.strictclock.ui.challenges.QRScannerView
import com.hotaro.strictclock.ui.challenges.CameraChallengeView
import com.hotaro.strictclock.ui.theme.*
import java.util.Calendar
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpScreen(
    challengeType: String = "None", 
    qrCodeData: String = "", 
    qrCodeName: String = "", 
    cameraObject: String = "",
    mathOperations: String = "Addition,Subtraction",
    mathDifficulty: String = "Easy",
    onStopAlarm: () -> Unit = {},
    onSnoozeAlarm: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    val zenModeEnabled = prefs.getBoolean("zen_mode", true)
    
    // Flashbang preferences
    val flashbangEnabled = prefs.getBoolean("flashbang_enabled", false)
    val fullBrightness = prefs.getBoolean("flashbang_full_brightness", true)
    val isBlinking = prefs.getBoolean("flashbang_blinking", true)
    val blinkSpeed = prefs.getFloat("flashbang_blink_speed", 500f)
    val defaultColors = listOf("#FFFFFF", "#FF0000", "#FFFF00").joinToString(",")
    val flashbangColorsStr = prefs.getString("flashbang_colors", defaultColors) ?: defaultColors
    
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    val timeStr = String.format("%02d:%02d", hour, minute)
    val dayStr = android.text.format.DateFormat.format("EEEE, MMM d", currentTime.timeInMillis).toString()

    val defaultBgColor = backgroundDark
    var currentFlashColor by remember { mutableStateOf(defaultBgColor) }

    if (flashbangEnabled) {
        val activity = context as? android.app.Activity
        if (fullBrightness) {
            LaunchedEffect(Unit) {
                val window = activity?.window
                val layoutParams = window?.attributes
                layoutParams?.screenBrightness = 1.0f
                window?.attributes = layoutParams
            }
        }

        val colorsList = flashbangColorsStr.split(",").filter { it.isNotEmpty() }.map { Color(android.graphics.Color.parseColor(it)) }
        if (colorsList.isNotEmpty()) {
            LaunchedEffect(isBlinking, blinkSpeed) {
                var colorIndex = 0
                while (true) {
                    currentFlashColor = colorsList[colorIndex % colorsList.size]
                    colorIndex++
                    kotlinx.coroutines.delay(blinkSpeed.toLong())
                    if (isBlinking) {
                        currentFlashColor = defaultBgColor
                        kotlinx.coroutines.delay(blinkSpeed.toLong())
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Alarm, contentDescription = null, tint = onSurfaceDark, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RISE", fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 20.sp, letterSpacing = 1.sp)
                }
                IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = onSurfaceDark) }
            }
        },
        containerColor = currentFlashColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (flashbangEnabled) Color.Black.copy(alpha = 0.4f) else Color.Transparent)
        ) {
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("WAKE UP!", letterSpacing = 4.sp, fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 14.sp)
            Text(timeStr, fontSize = 110.sp, fontWeight = FontWeight.Medium, color = primaryDark, letterSpacing = (-2).sp)
            Text(dayStr, color = onSurfaceVariantDark, fontSize = 18.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (challengeType) {
                "Math" -> MathChallengeView(mathOperations, mathDifficulty, zenModeEnabled, onSnoozeAlarm, onStopAlarm)
                "QR Code" -> QRChallengeView(qrCodeData, qrCodeName, zenModeEnabled, onSnoozeAlarm, onStopAlarm)
                "QR" -> QRChallengeView(qrCodeData, qrCodeName, zenModeEnabled, onSnoozeAlarm, onStopAlarm)
                "Camera" -> CameraChallengeViewWrapper(cameraObject, zenModeEnabled, onSnoozeAlarm, onStopAlarm)
                "Puzzle" -> PuzzleChallengeView(zenModeEnabled, onSnoozeAlarm, onStopAlarm)
                else -> RegularWakeUpView(zenModeEnabled, onSnoozeAlarm, onStopAlarm)
            }
        }
        }
    }
}

@Composable
fun ColumnScope.MathChallengeView(operations: String, difficulty: String, zenModeEnabled: Boolean, onSnoozeAlarm: () -> Unit, onStopAlarm: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    val actualOperations = prefs.getString("math_operations", "Addition,Subtraction") ?: "Addition,Subtraction"
    val actualDifficulty = prefs.getString("math_difficulty", "Easy") ?: "Easy"
    
    var problemsSolved by remember { mutableStateOf(0) }
    val totalProblems = prefs.getInt("math_sums", 3)
    var currentProblem by remember { mutableStateOf(MathChallenge.generateProblem(actualOperations, actualDifficulty)) }
    var answerInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Surface(color = surfaceVariantDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Calculate, contentDescription = null, tint = onSurfaceDark)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Math Challenge", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = onSurfaceDark)
                    Text("Solve $totalProblems equations to stop", color = onSurfaceVariantDark, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text("${problemsSolved}/${totalProblems} Solved", color = primaryDark, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(currentProblem.expression + " = ?", fontSize = 48.sp, color = onSurfaceDark, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(64.dp)
                    .background(if (showError) errorDark.copy(alpha = 0.2f) else backgroundDark, RoundedCornerShape(16.dp))
                    .border(2.dp, if (showError) errorDark else outlineDark, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = answerInput.ifEmpty { "..." },
                    fontSize = 32.sp,
                    color = if (showError) errorDark else if (answerInput.isEmpty()) onSurfaceVariantDark else primaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Numeric Keypad
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val padKeys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("Del", "0", "OK")
                )
                
                for (row in padKeys) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (key in row) {
                            Button(
                                onClick = {
                                    showError = false
                                    when (key) {
                                        "Del" -> if (answerInput.isNotEmpty()) answerInput = answerInput.dropLast(1)
                                        "OK" -> {
                                            val ans = answerInput.toIntOrNull()
                                            if (ans == currentProblem.answer) {
                                                problemsSolved++
                                                if (problemsSolved >= totalProblems) {
                                                    onStopAlarm()
                                                } else {
                                                    currentProblem = MathChallenge.generateProblem(actualOperations, actualDifficulty)
                                                    answerInput = ""
                                                }
                                            } else {
                                                showError = true
                                                answerInput = ""
                                            }
                                        }
                                        else -> if (answerInput.length < 5) answerInput += key
                                    }
                                },
                                modifier = Modifier.weight(1f).aspectRatio(1.5f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (key == "OK") primaryDark else surfaceContainerHighestDark,
                                    contentColor = if (key == "OK") onPrimaryDark else onSurfaceDark
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(key, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (!zenModeEnabled) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onSnoozeAlarm,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = surfaceContainerHighDark, contentColor = onSurfaceDark),
            shape = RoundedCornerShape(32.dp)
        ) {
            Icon(Icons.Outlined.Snooze, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Snooze (5:00)", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun ColumnScope.QRChallengeView(qrCodeData: String, qrCodeName: String, zenModeEnabled: Boolean, onSnoozeAlarm: () -> Unit, onStopAlarm: () -> Unit) {
    var showError by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF4A4458), shape = CircleShape, modifier = Modifier.size(56.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = onSurfaceDark)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(if (showError) "Wrong Code" else "Scan QR Code", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = if (showError) errorDark else onSurfaceDark)
                    Text(if (qrCodeName.isNotEmpty()) "Scan '$qrCodeName' to stop" else "Find the code to stop", color = onSurfaceVariantDark, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
            ) {
                QRScannerView(onQrDetected = { code ->
                    if (qrCodeData.isEmpty() || code == qrCodeData) {
                        onStopAlarm()
                    } else {
                        showError = true
                    }
                })
                // Custom scanner overlay
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(200.dp).border(4.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)))
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFFFB4AB).copy(alpha = 0.5f)))
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Button(
        onClick = onStopAlarm,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryContainerDark, contentColor = onPrimaryContainerDark),
        shape = RoundedCornerShape(32.dp)
    ) {
        Icon(Icons.Outlined.CameraAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Capture & Stop", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
    
    if (!zenModeEnabled) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onSnoozeAlarm,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = surfaceContainerHighDark, contentColor = onSurfaceDark),
            shape = RoundedCornerShape(32.dp)
        ) {
            Icon(Icons.Outlined.Snooze, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Snooze (5:00)", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun ColumnScope.CameraChallengeViewWrapper(cameraObject: String, zenModeEnabled: Boolean, onSnoozeAlarm: () -> Unit, onStopAlarm: () -> Unit) {
    var showCamera by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF4A4458), shape = CircleShape, modifier = Modifier.size(56.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = onSurfaceDark)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Camera Challenge", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = onSurfaceDark)
                    Text("Take a picture of: $cameraObject", color = onSurfaceVariantDark, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
            ) {
                if (showCamera) {
                    CameraChallengeView(
                        targetObject = cameraObject,
                        onObjectDetected = onStopAlarm
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Button(onClick = { showCamera = true }) {
                            Text("Start Camera")
                        }
                    }
                }
            }
        }
    }
    
    if (!zenModeEnabled) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onSnoozeAlarm,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = surfaceContainerHighDark, contentColor = onSurfaceDark),
            shape = RoundedCornerShape(32.dp)
        ) {
            Icon(Icons.Outlined.Snooze, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Snooze (5:00)", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun RegularWakeUpView(zenModeEnabled: Boolean, onSnoozeAlarm: () -> Unit, onStopAlarm: () -> Unit) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onStopAlarm,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryDark, contentColor = onPrimaryDark),
            shape = RoundedCornerShape(32.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Stop Alarm", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        
        if (!zenModeEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSnoozeAlarm,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = surfaceContainerHighDark, contentColor = onSurfaceDark),
                shape = RoundedCornerShape(32.dp)
            ) {
                Icon(Icons.Outlined.Snooze, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Snooze (5:00)", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
