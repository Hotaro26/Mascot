package com.hotaro.strictclock.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hotaro.strictclock.ui.theme.*

@Composable
fun PermissionsScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    var hasNotifications by remember { mutableStateOf(checkNotifications(context)) }
    var hasCamera by remember { mutableStateOf(checkCamera(context)) }
    var hasExactAlarm by remember { mutableStateOf(checkExactAlarm(context)) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        hasNotifications = checkNotifications(context)
        hasCamera = checkCamera(context)
    }

    val requestPermissions = {
        val perms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifications) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (!hasCamera) {
            perms.add(Manifest.permission.CAMERA)
        }
        if (perms.isNotEmpty()) {
            launcher.launch(perms.toTypedArray())
        }
        if (!hasExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    }

    // Refresh exact alarm state when returning from settings
    LaunchedEffect(Unit) {
        // We can't strictly listen to returning from settings easily without a lifecycle observer,
        // but checking on recompose is fine. The user can just hit "Allow Permissions" again.
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(64.dp))
        Text("Welcome to Mascot!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = onSurfaceDark)
        Spacer(modifier = Modifier.height(16.dp))
        Text("We need a few permissions to ensure your alarms ring on time and strict mode works flawlessly.", color = onSurfaceVariantDark, fontSize = 16.sp, textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(48.dp))

        PermissionCard(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            subtitle = "To show alarm alerts",
            isGranted = hasNotifications,
            onClick = { requestPermissions() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PermissionCard(
            icon = Icons.Outlined.CameraAlt,
            title = "Camera",
            subtitle = "To scan QR codes",
            isGranted = hasCamera,
            onClick = { requestPermissions() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PermissionCard(
            icon = Icons.Outlined.Alarm,
            title = "Exact Alarms",
            subtitle = "To wake you up exactly on time",
            isGranted = hasExactAlarm,
            onClick = { requestPermissions() }
        )

        Spacer(modifier = Modifier.weight(1f))

        if (hasNotifications && hasCamera && hasExactAlarm) {
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryDark, contentColor = onPrimaryDark)
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            Button(
                onClick = { requestPermissions() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryContainerDark, contentColor = onPrimaryContainerDark)
            ) {
                Text("Allow Permissions", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Let the user refresh the exact alarm state manually
        if (!hasExactAlarm) {
            TextButton(onClick = { hasExactAlarm = checkExactAlarm(context) }) {
                Text("I've allowed exact alarms", color = primaryDark)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun checkNotifications(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true
}

private fun checkCamera(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

private fun checkExactAlarm(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager?.canScheduleExactAlarms() == true
    } else true
}

@Composable
fun PermissionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, isGranted: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isGranted) primaryDark else onSurfaceVariantDark)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = onSurfaceDark, fontWeight = FontWeight.Medium)
                Text(subtitle, color = onSurfaceVariantDark, fontSize = 14.sp)
            }
            if (isGranted) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Granted", tint = primaryDark)
            } else {
                Switch(checked = false, onCheckedChange = { onClick() }, colors = SwitchDefaults.colors(uncheckedThumbColor = onSurfaceVariantDark, uncheckedTrackColor = surfaceVariantDark))
            }
        }
    }
}
