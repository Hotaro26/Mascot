package com.hotaro.strictclock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToColorScheme: () -> Unit = {},
    onNavigateToThemeMode: () -> Unit = {},
    onNavigateToQrManagement: () -> Unit = {},
    onNavigateToZenMode: () -> Unit = {},
    onNavigateToCustomisation: () -> Unit = {},
    onNavigateToMathSettings: () -> Unit = {},
    onNavigateToPuzzles: () -> Unit = {},
    onNavigateToFlashbang: () -> Unit = {},
    onNavigateToAiReadiness: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToAppIcons: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Settings", fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 20.sp)
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = context.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
            val streak = prefs.getInt("wake_up_streak", 0)
            
            var defaultRingtoneName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Default ringtone") }
            val ringtoneLauncher = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val uri: android.net.Uri? = result.data?.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    val uriStr = uri?.toString() ?: ""
                    prefs.edit().putString("default_alarm_sound", uriStr).apply()
                    val ringtone = android.media.RingtoneManager.getRingtone(context, uri)
                    defaultRingtoneName = ringtone?.getTitle(context) ?: "Selected Sound"
                }
            }
            
            androidx.compose.runtime.LaunchedEffect(Unit) {
                val savedUri = prefs.getString("default_alarm_sound", "")
                if (!savedUri.isNullOrEmpty()) {
                    val uri = android.net.Uri.parse(savedUri)
                    val ringtone = android.media.RingtoneManager.getRingtone(context, uri)
                    defaultRingtoneName = ringtone?.getTitle(context) ?: "Selected Sound"
                }
            }
            
            androidx.compose.runtime.LaunchedEffect(Unit) {
                ThemeManager.checkAiStatus(context)
            }
            val aiStatusText by ThemeManager.aiStatus.collectAsState()
            
            // Top Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Icon(Icons.Outlined.Bolt, contentDescription = null, tint = onSurfaceDark, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Wake-up streak", color = onSurfaceVariantDark, fontSize = 14.sp)
                        Text("$streak Days", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Card(
                    onClick = onNavigateToAiReadiness,
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = primaryContainerDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = onPrimaryContainerDark, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text("AI Readiness", color = onPrimaryContainerDark.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text(aiStatusText, color = onPrimaryContainerDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Alarm Behavior
            Text("Alarm Behavior", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(
                icon = Icons.Outlined.MusicNote, 
                title = "Alarm Sound", 
                subtitle = defaultRingtoneName, 
                showArrow = true, 
                bottomStart = 4.dp, 
                bottomEnd = 4.dp,
                onClick = {
                    val intent = android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_ALARM)
                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                    }
                    ringtoneLauncher.launch(intent)
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            SettingsRow(icon = Icons.Outlined.Snooze, title = "Zen Mode", subtitle = "Manage snoozing", showArrow = true, topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp, onClick = onNavigateToZenMode)
            Spacer(modifier = Modifier.height(2.dp))
            SettingsRow(icon = Icons.Outlined.FlashlightOn, title = "Flashbang", subtitle = "Screen flashing options", showArrow = true, topStart = 4.dp, topEnd = 4.dp, onClick = onNavigateToFlashbang)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Strict Tasks
            Text("Strict Tasks", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(icon = Icons.Outlined.Calculate, title = "Math Settings", subtitle = "Customise math challenges", showArrow = true, bottomStart = 4.dp, bottomEnd = 4.dp, onClick = onNavigateToMathSettings)
            Spacer(modifier = Modifier.height(2.dp))
            SettingsRow(icon = Icons.Outlined.Extension, title = "Puzzles", subtitle = "Configure puzzle challenges", showArrow = true, topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp, onClick = onNavigateToPuzzles)
            Spacer(modifier = Modifier.height(2.dp))
            SettingsRow(icon = Icons.Outlined.QrCodeScanner, title = "QR Management", subtitle = "Manage saved codes", showArrow = true, topStart = 4.dp, topEnd = 4.dp, onClick = onNavigateToQrManagement)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Appearance
            Text("Appearance", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(
                icon = Icons.Outlined.Apps,
                title = "App Icons",
                subtitle = "Choose your mascot",
                showArrow = true,
                bottomStart = 4.dp, bottomEnd = 4.dp,
                onClick = onNavigateToAppIcons
            )
            Spacer(modifier = Modifier.height(2.dp))
            SettingsRow(
                icon = Icons.Outlined.DashboardCustomize,
                title = "Customisation",
                subtitle = "Input preferences and styles",
                showArrow = true,
                topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp,
                onClick = onNavigateToCustomisation
            )
            Spacer(modifier = Modifier.height(2.dp))
            val activeScheme by ThemeManager.activeScheme.collectAsState()
            SettingsRow(
                icon = Icons.Outlined.Palette, 
                title = "Color Scheme", 
                subtitle = activeScheme, 
                showArrow = true,
                topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp,
                onClick = onNavigateToColorScheme
            )
            Spacer(modifier = Modifier.height(2.dp))

            val themeMode by ThemeManager.themeMode.collectAsState()
            SettingsRow(
                icon = Icons.Outlined.DarkMode, 
                title = "Theme Mode", 
                subtitle = themeMode, 
                showArrow = true,
                topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp,
                onClick = onNavigateToThemeMode
            )
            Spacer(modifier = Modifier.height(2.dp))

            val isAmoled by ThemeManager.isAmoled.collectAsState()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDarkTheme = themeMode == "Dark" || (themeMode == "System" && isSystemDark)
            
            SettingsRowSwitch(
                icon = Icons.Outlined.DarkMode, 
                title = "Amoled Mode", 
                subtitle = "True black background", 
                checked = isAmoled,
                enabled = isDarkTheme,
                onDisabledClick = {
                    android.widget.Toast.makeText(context, "AMOLED mode cannot be used in Light mode", android.widget.Toast.LENGTH_SHORT).show()
                },
                topStart = 4.dp, topEnd = 4.dp, 
                onCheckedChange = { ThemeManager.setAmoled(it) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // About
            Text("About", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(
                icon = Icons.Outlined.Info, 
                title = "About mascot", 
                subtitle = "App version and developer info", 
                showArrow = true,
                onClick = onNavigateToAbout
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
