package com.hotaro.strictclock.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.service.TimerManager
import com.hotaro.strictclock.service.TimerService
import com.hotaro.strictclock.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    
    val timeRemaining by TimerManager.timeRemaining.collectAsState()
    val isRunning by TimerManager.isRunning.collectAsState()

    val prefs = context.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
    val useKeyboardTimeInput = prefs.getBoolean("use_keyboard_time_input", false)
    
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 15,
        is24Hour = true,
    )
    
    var showTimePickerDialog by remember { mutableStateOf(false) }
    
    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("Set Timer", color = onSurfaceDark) },
            text = {
                val colors = TimePickerDefaults.colors(
                    clockDialColor = surfaceContainerHighDark,
                    selectorColor = primaryDark,
                    containerColor = backgroundDark,
                    periodSelectorBorderColor = outlineDark,
                    periodSelectorSelectedContainerColor = primaryContainerDark,
                    periodSelectorSelectedContentColor = onPrimaryContainerDark,
                    timeSelectorSelectedContainerColor = primaryContainerDark,
                    timeSelectorSelectedContentColor = onPrimaryContainerDark,
                    timeSelectorUnselectedContainerColor = surfaceContainerHighDark,
                    timeSelectorUnselectedContentColor = onSurfaceDark
                )
                if (useKeyboardTimeInput) {
                    TimeInput(state = timePickerState, colors = colors)
                } else {
                    TimePicker(state = timePickerState, colors = colors)
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("OK", color = primaryDark)
                }
            },
            containerColor = surfaceContainerHighDark
        )
    }
    
    var stopwatchRunning by remember { mutableStateOf(false) }
    var stopwatchTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(stopwatchRunning) {
        if (stopwatchRunning) {
            var lastTime = System.currentTimeMillis()
            while (stopwatchRunning) {
                delay(10)
                val now = System.currentTimeMillis()
                stopwatchTime += (now - lastTime)
                lastTime = now
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = onSurfaceDark,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = primaryDark
                )
            },
            divider = { }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Timer", fontSize = 18.sp, fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Stopwatch", fontSize = 18.sp, fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }
        
        if (selectedTab == 0) {
            // Timer View
            if (!isRunning && timeRemaining == 0L) {
                Spacer(modifier = Modifier.weight(1f))
                
                val h = timePickerState.hour
                val m = timePickerState.minute
                val timeStr = if (h > 0) String.format("%02d:%02d:00", h, m) else String.format("%02d:00", m)
                
                Text(timeStr, fontSize = 72.sp, fontWeight = FontWeight.Normal, color = onSurfaceDark)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Set timer (HH:MM)", color = onSurfaceVariantDark, fontSize = 20.sp)
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Pill FAB for start and setup
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    color = primaryDark,
                    modifier = Modifier
                        .height(88.dp)
                        .width(200.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val totalMs = (h * 60 * 60 + m * 60) * 1000L
                                if (totalMs > 0) {
                                    val intent = Intent(context, TimerService::class.java)
                                    intent.action = TimerService.ACTION_START
                                    intent.putExtra(TimerService.EXTRA_DURATION_MS, totalMs)
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent)
                                    } else {
                                        context.startService(intent)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = onPrimaryDark, modifier = Modifier.size(40.dp))
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .width(2.dp)
                                .background(onPrimaryDark.copy(alpha = 0.5f))
                        )
                        
                        IconButton(
                            onClick = { showTimePickerDialog = true },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            Icon(Icons.Default.Alarm, contentDescription = "Set Time", tint = onPrimaryDark, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            } else {
                val maxTime by TimerManager.maxTime.collectAsState()
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                    color = surfaceContainerLowDark
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val maxMins = maxTime / 60000
                            Text("${maxMins}m timer", fontSize = 24.sp, color = onSurfaceDark)
                            Surface(
                                shape = CircleShape,
                                color = surfaceContainerHighDark,
                                modifier = Modifier.size(32.dp)
                            ) {
                                IconButton(onClick = {
                                    val intent = Intent(context, TimerService::class.java)
                                    intent.action = TimerService.ACTION_STOP
                                    context.startService(intent)
                                }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = onSurfaceVariantDark, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier.size(280.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Circular Progress
                            val progress = if (maxTime > 0) timeRemaining.toFloat() / maxTime.toFloat() else 0f
                            val outlineColor = outlineVariantDark
                            val progressColor = primaryDark
                            
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = outlineColor,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                                drawArc(
                                    color = progressColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * progress,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val seconds = (timeRemaining / 1000) % 60
                                val minutes = (timeRemaining / (1000 * 60)) % 60
                                val hours = (timeRemaining / (1000 * 60 * 60))
                                
                                val timeStr = if (hours > 0) {
                                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                                } else {
                                    String.format("%d:%02d", minutes, seconds)
                                }
                                Text(timeStr, fontSize = 72.sp, fontWeight = FontWeight.Normal, color = onSurfaceDark)
                                
                                IconButton(onClick = {
                                    val intent = Intent(context, TimerService::class.java)
                                    intent.action = TimerService.ACTION_RESET
                                    context.startService(intent)
                                }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = onSurfaceVariantDark, modifier = Modifier.size(32.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val playPauseColor = if (isRunning) primaryContainerDark else primaryDark
                            val playPauseIconColor = if (isRunning) onPrimaryContainerDark else onPrimaryDark
                            
                            Button(
                                onClick = {
                                    val intent = Intent(context, TimerService::class.java)
                                    intent.action = if (isRunning) TimerService.ACTION_PAUSE else TimerService.ACTION_RESUME
                                    context.startService(intent)
                                },
                                modifier = Modifier.width(160.dp).height(80.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = playPauseColor, contentColor = playPauseIconColor)
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            // Stopwatch View
            StopwatchTabContent()
        }
    }
}
