package com.hotaro.strictclock.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.data.AlarmEntity
import com.hotaro.strictclock.ui.theme.*
import java.util.Calendar
import android.media.RingtoneManager
import android.net.Uri
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.items
import com.hotaro.strictclock.ui.challenges.QRScannerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupAlarmScreen(viewModel: AlarmViewModel? = null, alarm: AlarmEntity? = null, onBack: () -> Unit) {
    val currentTime = Calendar.getInstance()
    
    val timePickerState = rememberTimePickerState(
        initialHour = alarm?.timeHour ?: java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY),
        initialMinute = alarm?.timeMinute ?: currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )
    
    var selectedChallenge by remember { mutableStateOf(alarm?.challengeType ?: "QR") }
    var repeatDays by remember { mutableStateOf(alarm?.daysOfWeek ?: "Daily") }
    var soundUri by remember { mutableStateOf(alarm?.soundUri ?: "") }
    var soundName by remember { mutableStateOf(alarm?.soundName ?: "Default") }
    var vibrationEnabled by remember { mutableStateOf(alarm?.vibrationEnabled ?: true) }
    var showRepeatDialog by remember { mutableStateOf(false) }
    var qrCodeData by remember { mutableStateOf(alarm?.qrCodeData ?: "") }
    var qrCodeName by remember { mutableStateOf(alarm?.qrCodeName ?: "") }
    var cameraObject by remember { mutableStateOf(alarm?.cameraObject ?: "") }

    var showQrScanner by remember { mutableStateOf(false) }
    var scannedQrCode by remember { mutableStateOf<String?>(null) }
    var showQrOptionsDialog by remember { mutableStateOf(false) }
    var showSelectQrDialog by remember { mutableStateOf(false) }
    var showCameraObjectDialog by remember { mutableStateOf(false) }
    var showStrictModeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val savedQrs = remember { loadQrs(context) }
    val ringtoneLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                soundUri = uri.toString()
                val ringtone = RingtoneManager.getRingtone(context, uri)
                soundName = ringtone?.getTitle(context) ?: "Selected Sound"
            } else {
                soundUri = "Silent"
                soundName = "Silent"
            }
        }
    }
    
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    val useKeyboardTimeInput = prefs.getBoolean("use_keyboard_time_input", false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Strict Alarm", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp).size(28.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (selectedChallenge == "QR Code" && qrCodeData.isEmpty()) {
                                showStrictModeDialog = true
                                return@Button
                            }
                            if (selectedChallenge == "Camera" && cameraObject.isEmpty()) {
                                showStrictModeDialog = true
                                return@Button
                            }
                            val newAlarm = AlarmEntity(
                                id = alarm?.id ?: 0,
                                timeHour = timePickerState.hour,
                                timeMinute = timePickerState.minute,
                                daysOfWeek = repeatDays,
                                isActive = true,
                                challengeType = selectedChallenge,
                                soundName = soundName,
                                soundUri = soundUri,
                                vibrationEnabled = vibrationEnabled,
                                qrCodeData = if (selectedChallenge == "QR Code") qrCodeData else "",
                                qrCodeName = if (selectedChallenge == "QR Code") qrCodeName else "",
                                cameraObject = if (selectedChallenge == "Camera") cameraObject else ""
                            )
                            if (alarm == null) {
                                viewModel?.insert(newAlarm)
                            } else {
                                viewModel?.update(newAlarm)
                            }
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryContainerDark, contentColor = onPrimaryContainerDark),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        if (showStrictModeDialog) {
            AlertDialog(
                onDismissRequest = { showStrictModeDialog = false },
                title = { Text("Strict Mode Required", color = onSurfaceDark) },
                text = { Text("You must select a strict mode task (like Math or QR) to save this alarm. If you select QR Code, please select a pre-saved QR code or scan a new one.", color = onSurfaceVariantDark) },
                confirmButton = {
                    TextButton(onClick = { showStrictModeDialog = false }) {
                        Text("OK", color = primaryDark)
                    }
                },
                containerColor = surfaceContainerHighDark,
                titleContentColor = onSurfaceDark,
                textContentColor = onSurfaceVariantDark
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SettingsRow(icon = Icons.Outlined.Repeat, title = "Repeat", subtitle = repeatDays, showArrow = true) {
                showRepeatDialog = true
            }
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRow(icon = Icons.Outlined.Notifications, title = "Alarm Sound", subtitle = soundName, showArrow = true) {
                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                }
                ringtoneLauncher.launch(intent)
            }
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRowSwitch(icon = Icons.Outlined.Vibration, title = "Vibration", subtitle = "Heartbeat pattern", checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Strict Mode Tasks", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onSurfaceDark)
                Surface(color = errorDark, shape = RoundedCornerShape(16.dp)) {
                    Text("Mandatory", color = onErrorDark, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                TaskCard(
                    icon = Icons.Outlined.QrCodeScanner,
                    title = "QR Code",
                    subtitle = if (selectedChallenge == "QR Code") {
                        if (qrCodeName.isNotEmpty()) "Saved: $qrCodeName" else "Tap to setup QR Code"
                    } else "Scan a code",
                    modifier = Modifier.weight(1f).clickable { 
                        if (selectedChallenge == "QR Code") {
                            showQrOptionsDialog = true
                        } else {
                            selectedChallenge = "QR Code"
                        }
                    },
                    isSelected = selectedChallenge == "QR Code"
                )
                Spacer(modifier = Modifier.width(16.dp))
                TaskCard(
                    icon = Icons.Outlined.Calculate, 
                    title = "Math", 
                    subtitle = "Coming later", 
                    modifier = Modifier.weight(1f), 
                    isSelected = selectedChallenge == "Math"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                TaskCard(
                    icon = Icons.Outlined.CameraAlt, 
                    title = "Camera", 
                    subtitle = if (selectedChallenge == "Camera") {
                        if (cameraObject.isNotEmpty()) "Target: $cameraObject" else "Tap to setup Object"
                    } else "Take a picture", 
                    modifier = Modifier.weight(1f).clickable {
                        if (selectedChallenge == "Camera") {
                            showCameraObjectDialog = true
                        } else {
                            selectedChallenge = "Camera"
                        }
                    },
                    isSelected = selectedChallenge == "Camera"
                )
                Spacer(modifier = Modifier.width(16.dp))
                TaskCard(
                    icon = Icons.Outlined.Extension, 
                    title = "Puzzle", 
                    subtitle = "Coming later", 
                    modifier = Modifier.weight(1f),
                    isSelected = selectedChallenge == "Puzzle"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        if (showRepeatDialog) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            var selectedDays by remember { mutableStateOf(
                when (repeatDays) {
                    "Daily" -> days.toSet()
                    "Mon-Fri" -> setOf("Mon", "Tue", "Wed", "Thu", "Fri")
                    "Sat-Sun" -> setOf("Sat", "Sun")
                    "Never" -> emptySet()
                    else -> repeatDays.split(", ").toSet()
                }
            ) }
            
            AlertDialog(
                onDismissRequest = { showRepeatDialog = false },
                title = { Text("Repeat Days", color = onSurfaceDark) },
                containerColor = surfaceContainerHighDark,
                text = {
                    Column {
                        days.forEach { day ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                                selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
                            }) {
                                Checkbox(checked = selectedDays.contains(day), onCheckedChange = { 
                                    selectedDays = if (it) selectedDays + day else selectedDays - day
                                }, colors = CheckboxDefaults.colors(checkedColor = primaryDark))
                                Text(day, color = onSurfaceDark)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        repeatDays = when (selectedDays.size) {
                            7 -> "Daily"
                            0 -> "Never"
                            5 -> if (selectedDays.containsAll(listOf("Mon", "Tue", "Wed", "Thu", "Fri"))) "Mon-Fri" else selectedDays.joinToString(", ")
                            2 -> if (selectedDays.containsAll(listOf("Sat", "Sun"))) "Sat-Sun" else selectedDays.joinToString(", ")
                            else -> days.filter { selectedDays.contains(it) }.joinToString(", ")
                        }
                        showRepeatDialog = false
                    }) {
                        Text("Save", color = primaryDark)
                    }
                }
            )
        }
        
        if (showQrOptionsDialog) {
            AlertDialog(
                onDismissRequest = { showQrOptionsDialog = false },
                title = { Text("Set up QR Code") },
                text = { Text("Would you like to use a pre-saved QR Code or scan a new one?") },
                confirmButton = {
                    TextButton(onClick = {
                        showQrOptionsDialog = false
                        showSelectQrDialog = true
                    }) { Text("Use Pre-saved", color = primaryDark) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showQrOptionsDialog = false
                        showQrScanner = true
                    }) { Text("Scan New", color = primaryDark) }
                },
                containerColor = surfaceContainerHighDark,
                titleContentColor = onSurfaceDark,
                textContentColor = onSurfaceVariantDark
            )
        }
        
        if (showSelectQrDialog) {
            AlertDialog(
                onDismissRequest = { showSelectQrDialog = false },
                title = { Text("Select QR Code") },
                text = {
                    if (savedQrs.isEmpty()) {
                        Text("No saved QR codes found. Please scan a new one.")
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn {
                            items(savedQrs) { qr ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable {
                                        qrCodeData = qr.data
                                        qrCodeName = qr.name
                                        showSelectQrDialog = false
                                    },
                                    colors = CardDefaults.cardColors(containerColor = surfaceContainerLowDark)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = primaryDark)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(qr.name, color = onSurfaceDark, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSelectQrDialog = false }) { Text("Cancel", color = onSurfaceVariantDark) }
                },
                containerColor = surfaceContainerHighDark,
                titleContentColor = onSurfaceDark
            )
        }

        if (showQrScanner) {
            Dialog(
                onDismissRequest = { showQrScanner = false; scannedQrCode = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = backgroundDark) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = { Text("Set Main QR Code", color = onSurfaceDark) },
                            navigationIcon = {
                                IconButton(onClick = { showQrScanner = false; scannedQrCode = null }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceDark)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark)
                        )
                        Box(modifier = Modifier.weight(1f).padding(16.dp).background(androidx.compose.ui.graphics.Color.Black, RoundedCornerShape(16.dp))) {
                            if (scannedQrCode == null) {
                                QRScannerView(onQrDetected = { code ->
                                    scannedQrCode = code
                                })
                                Text(
                                    "Point camera at a QR code",
                                    color = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (scannedQrCode != null) {
            AlertDialog(
                onDismissRequest = { scannedQrCode = null },
                title = { Text("Save QR Code?", color = onSurfaceDark) },
                text = { Text("Do you want to save this QR code as your main code to disable alarms?", color = onSurfaceVariantDark) },
                containerColor = surfaceContainerHighDark,
                confirmButton = {
                    TextButton(onClick = {
                        val data = scannedQrCode!!
                        prefs.edit().putString("main_qr_code", data).apply()
                        scannedQrCode = data
                        qrCodeData = data
                        qrCodeName = "Scanned Code"
                        showQrScanner = false
                    }) {
                        Text("Save", color = primaryDark)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { scannedQrCode = null }) {
                        Text("Cancel", color = onSurfaceVariantDark)
                    }
                }
            )
        }
        
        if (showCameraObjectDialog) {
            AlertDialog(
                onDismissRequest = { showCameraObjectDialog = false },
                title = { Text("Select Camera Target", color = onSurfaceDark) },
                text = {
                    Column {
                        listOf("Sink", "Chair", "Mug", "Keyboard", "Shoe", "Toothbrush").forEach { target ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { 
                                cameraObject = target
                                showCameraObjectDialog = false
                            }.padding(16.dp)) {
                                Text(target, color = onSurfaceDark, fontSize = 16.sp)
                            }
                            HorizontalDivider(color = outlineDark.copy(alpha = 0.5f))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCameraObjectDialog = false }) {
                        Text("Close", color = primaryDark)
                    }
                },
                containerColor = surfaceContainerHighDark
            )
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, subtitle: String, showArrow: Boolean = false, onClick: () -> Unit = {}) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = onSurfaceVariantDark)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = onSurfaceDark)
                Text(subtitle, color = onSurfaceVariantDark, fontSize = 14.sp)
            }
            if (showArrow) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = onSurfaceVariantDark)
            }
        }
    }
}

@Composable
fun SettingsRowSwitch(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit = {}) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = onSurfaceVariantDark)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = onSurfaceDark)
                Text(subtitle, color = onSurfaceVariantDark, fontSize = 14.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = primaryDark, checkedTrackColor = primaryContainerDark))
        }
    }
}

@Composable
fun TaskCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, isSelected: Boolean = false) {
    val border = if (isSelected) BorderStroke(2.dp, primaryDark) else null
    Card(shape = RoundedCornerShape(16.dp), border = border, colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark), modifier = modifier.height(110.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = if (isSelected) primaryDark else onSurfaceDark)
            Spacer(modifier = Modifier.weight(1f))
            Text(title, color = if (isSelected) primaryDark else onSurfaceDark, fontWeight = FontWeight.Bold)
            Text(subtitle, color = onSurfaceVariantDark, fontSize = 12.sp)
        }
    }
}
