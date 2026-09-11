package com.hotaro.strictclock.ui

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.ExpressiveSwitch
import com.hotaro.strictclock.service.AdminReceiver
import com.hotaro.strictclock.service.AntiPowerOffService
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenModeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, AdminReceiver::class.java)

    var zenModeEnabled by remember { mutableStateOf(prefs.getBoolean("zen_mode", true)) }
    var invincibleModeEnabled by remember { mutableStateOf(dpm.isAdminActive(adminComponent)) }
    var antiPowerOffEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context, AntiPowerOffService::class.java)) }

    val adminLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            invincibleModeEnabled = true
            Toast.makeText(context, "Invincible Mode Activated", Toast.LENGTH_SHORT).show()
        } else {
            invincibleModeEnabled = false
            Toast.makeText(context, "Admin access required for Invincible Mode", Toast.LENGTH_SHORT).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                invincibleModeEnabled = dpm.isAdminActive(adminComponent)
                antiPowerOffEnabled = isAccessibilityServiceEnabled(context, AntiPowerOffService::class.java)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal + androidx.compose.foundation.layout.WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = { Text("Zen Mode", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = onSurfaceDark) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp).size(40.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Zen Mode forces you to complete the challenge immediately without snoozing. Enable extra restrictions below to ensure you never oversleep.", color = onSurfaceVariantDark, fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Zen Mode
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Don't allow snoozing", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text("Forces you to wake up", color = onSurfaceVariantDark, fontSize = 14.sp)
                        }
                        ExpressiveSwitch(
                            checked = zenModeEnabled,
                            onCheckedChange = {
                                zenModeEnabled = it
                                prefs.edit().putBoolean("zen_mode", it).apply()
                            }
                        )
                    }

                    HorizontalDivider(color = backgroundDark, modifier = Modifier.padding(horizontal = 16.dp))

                    // Invincible Mode
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Invincible Mode", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text("Prevents app uninstallation", color = onSurfaceVariantDark, fontSize = 14.sp)
                        }
                        ExpressiveSwitch(
                            checked = invincibleModeEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Required to prevent the app from being uninstalled while you are trying to wake up.")
                                    }
                                    adminLauncher.launch(intent)
                                } else {
                                    dpm.removeActiveAdmin(adminComponent)
                                    invincibleModeEnabled = false
                                    Toast.makeText(context, "Invincible Mode Disabled", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = backgroundDark, modifier = Modifier.padding(horizontal = 16.dp))

                    // Anti Power Off Mode
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Anti Power Off", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text("Blocks powering off while ringing", color = onSurfaceVariantDark, fontSize = 14.sp)
                        }
                        ExpressiveSwitch(
                            checked = antiPowerOffEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    Toast.makeText(context, "Please enable Accessibility for mascot", Toast.LENGTH_LONG).show()
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                } else {
                                    Toast.makeText(context, "Please disable Accessibility in Settings", Toast.LENGTH_LONG).show()
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>): Boolean {
    val expectedComponentName = ComponentName(context, accessibilityService)
    val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        ?: return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        val componentNameString = colonSplitter.next()
        val enabledService = ComponentName.unflattenFromString(componentNameString)
        if (enabledService != null && enabledService == expectedComponentName) {
            return true
        }
    }
    return false
}
