package com.hotaro.strictclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import com.hotaro.strictclock.ui.theme.ExpressiveSwitch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockDashboard(viewModel: AlarmViewModel? = null, onNavigateToSetup: () -> Unit = {}, onEditAlarm: (com.hotaro.strictclock.data.AlarmEntity) -> Unit = {}) {
    val rawAlarms by viewModel?.allAlarms?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val alarms = remember(rawAlarms) {
        rawAlarms.sortedBy { alarm ->
            if (alarm.isActive) com.hotaro.strictclock.utils.AlarmUtils.getNextTriggerTime(alarm) else Long.MAX_VALUE
        }
    }
    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal + androidx.compose.foundation.layout.WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = { 
                    Text("Alarms", fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 22.sp)
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundDark
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSetup,
                shape = RoundedCornerShape(24.dp), // Squircle shape from design
                containerColor = tertiaryDark, // Pinkish color from design
                contentColor = onTertiaryDark,
                modifier = Modifier
                    .padding(bottom = 90.dp, end = 16.dp)
                    .size(80.dp) // Large FAB
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm", modifier = Modifier.size(32.dp))
            }
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // System Clock removed from here
            
            items(alarms, key = { it.id }) { alarm ->
                var showDeleteConfirm by remember { mutableStateOf(false) }
                val localContext = androidx.compose.ui.platform.LocalContext.current
                val localPrefs = localContext.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
                val noSwipeConfirm = localPrefs.getBoolean("no_swipe_delete_confirm", false)
                
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                            if (noSwipeConfirm) {
                                viewModel?.delete(alarm)
                                true
                            } else {
                                showDeleteConfirm = true
                                false
                            }
                        } else {
                            false
                        }
                    }
                )
                
                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("Delete Alarm", color = onSurfaceDark) },
                        text = { Text("Are you sure you want to delete this alarm?", color = onSurfaceVariantDark) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel?.delete(alarm)
                                showDeleteConfirm = false
                            }) {
                                Text("Delete", color = errorDark)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancel", color = primaryDark)
                            }
                        },
                        containerColor = surfaceContainerHighDark,
                        titleContentColor = onSurfaceDark,
                        textContentColor = onSurfaceVariantDark
                    )
                }
                
                val prefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
                val is24Hour = prefs.getString("clock_format", "12") == "24"
                
                val amPm = if (is24Hour) "" else if (alarm.timeHour >= 12) "PM" else "AM"
                val displayHour = if (is24Hour) alarm.timeHour else if (alarm.timeHour % 12 == 0) 12 else alarm.timeHour % 12
                val timeStr = String.format("%02d:%02d", displayHour, alarm.timeMinute)
                
                val icon = when (alarm.challengeType) {
                    "Math" -> Icons.Outlined.Calculate
                    "QR Code", "QR" -> Icons.Outlined.QrCodeScanner
                    else -> null
                }
                
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color = errorContainerDark
                        val alignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color, RoundedCornerShape(28.dp))
                                .padding(horizontal = 24.dp),
                            contentAlignment = alignment
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = onErrorContainerDark, modifier = Modifier.size(32.dp))
                        }
                    }
                ) {
                    AlarmCard(
                        time = timeStr,
                        amPm = amPm,
                        days = alarm.daysOfWeek,
                        challengeIcon = icon,
                        challengeText = if (alarm.challengeType == "None") "No Challenge" else "${alarm.challengeType} Challenge",
                        isActive = alarm.isActive,
                        onToggle = { isChecked ->
                            val updatedAlarm = alarm.copy(isActive = isChecked)
                            viewModel?.update(updatedAlarm)
                            if (isChecked) {
                                val triggerTime = com.hotaro.strictclock.utils.AlarmUtils.getNextTriggerTime(updatedAlarm)
                                val toastMsg = com.hotaro.strictclock.utils.AlarmUtils.formatTimeUntil(triggerTime)
                                android.widget.Toast.makeText(localContext, toastMsg, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = { onEditAlarm(alarm) }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(180.dp)) // Space for FAB
            }
        }
    }
}

@Composable
fun SystemClockCard(nextAlarm: com.hotaro.strictclock.data.AlarmEntity? = null) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }
    
    val prefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
    val is24Hour = prefs.getString("clock_format", "12") == "24"
    
    val calendar = java.util.Calendar.getInstance().apply { timeInMillis = currentTime }
    val isAm = calendar.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM
    val amPmStr = if (is24Hour) "" else if (isAm) "AM" else "PM"
    var hour = if (is24Hour) calendar.get(java.util.Calendar.HOUR_OF_DAY) else {
        val h = calendar.get(java.util.Calendar.HOUR)
        if (h == 0) 12 else h
    }
    val minute = calendar.get(java.util.Calendar.MINUTE)
    
    val timeStr = String.format("%02d:%02d", hour, minute)
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 80.sp, fontWeight = FontWeight.Medium, color = primaryDark)) {
                    append(timeStr)
                }
                withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal, color = onSurfaceVariantDark)) {
                    append(" $amPmStr")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        val dayStr = android.text.format.DateFormat.format("EEEE, MMM d", currentTime).toString()
        Text(
            text = dayStr,
            style = MaterialTheme.typography.bodyLarge.copy(color = onSurfaceVariantDark)
        )
        
        if (nextAlarm != null) {
            Spacer(modifier = Modifier.height(16.dp))
            val context = androidx.compose.ui.platform.LocalContext.current
            val nextDisplayHour = if (is24Hour) nextAlarm.timeHour else if (nextAlarm.timeHour % 12 == 0) 12 else nextAlarm.timeHour % 12
            val nextTimeStr = String.format("%02d:%02d", nextDisplayHour, nextAlarm.timeMinute)
            val nextAmPm = if (is24Hour) "" else if (nextAlarm.timeHour >= 12) "PM" else "AM"
            
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceContainerHighDark,
                modifier = Modifier.clickable {
                    val triggerTime = com.hotaro.strictclock.utils.AlarmUtils.getNextTriggerTime(nextAlarm)
                    val diff = triggerTime - System.currentTimeMillis()
                    val minutesLeft = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff)
                    val hoursLeft = minutesLeft / 60
                    val minLeft = minutesLeft % 60
                    val toastMsg = if (hoursLeft > 0) "Alarm rings in $hoursLeft hr $minLeft min" else "Alarm rings in $minLeft min"
                    android.widget.Toast.makeText(context, toastMsg, android.widget.Toast.LENGTH_SHORT).show()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.Alarm, contentDescription = null, tint = primaryDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$nextTimeStr $nextAmPm",
                        color = onSurfaceDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmCard(
    time: String, 
    amPm: String, 
    days: String, 
    challengeIcon: androidx.compose.ui.graphics.vector.ImageVector?, 
    challengeText: String, 
    isActive: Boolean,
    onToggle: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    var checked by remember(isActive) { mutableStateOf(isActive) }
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerHighDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 36.sp, fontWeight = FontWeight.Normal, color = if(checked) onSurfaceDark else onSurfaceVariantDark)) {
                            append(time)
                        }
                        withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = onSurfaceVariantDark)) {
                            append(" $amPm")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = days,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = onSurfaceVariantDark
                        )
                    )
                    
                    Text(
                        text = "  •  ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = outlineVariantDark
                        )
                    )
                    
                    if (challengeIcon != null) {
                        Icon(
                            imageVector = challengeIcon,
                            contentDescription = challengeText,
                            tint = onSurfaceVariantDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = challengeText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = onSurfaceVariantDark
                        )
                    )
                }
            }
            ExpressiveSwitch(
                checked = checked,
                onCheckedChange = { 
                    checked = it
                    onToggle(it)
                }
            )
        }
    }
}
