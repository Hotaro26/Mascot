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
    onNavigateToZenMode: () -> Unit = {}
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
                    modifier = Modifier.weight(1f).height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = primaryContainerDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = onPrimaryContainerDark, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text("AI Readiness", color = onPrimaryContainerDark.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("Optimal", color = onPrimaryContainerDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Alarm Behavior
            Text("Alarm Behavior", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(icon = Icons.Outlined.Snooze, title = "Zen Mode", subtitle = "Manage snoozing", showArrow = true, onClick = onNavigateToZenMode)
            Spacer(modifier = Modifier.height(8.dp))
            val isAmoled by ThemeManager.isAmoled.collectAsState()
            SettingsRowSwitch(icon = Icons.Outlined.DarkMode, title = "Amoled Mode", subtitle = "True black background", checked = isAmoled, onCheckedChange = { ThemeManager.setAmoled(it) })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Strict Tasks
            Text("Strict Tasks", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsRow(icon = Icons.Outlined.FitnessCenter, title = "Task Difficulty", subtitle = "Intermediate level", showArrow = true)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsRow(icon = Icons.Outlined.QrCodeScanner, title = "QR Management", subtitle = "Manage saved codes", showArrow = true, onClick = onNavigateToQrManagement)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Appearance
            Text("Appearance", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            val activeScheme by ThemeManager.activeScheme.collectAsState()
            SettingsRow(
                icon = Icons.Outlined.Palette, 
                title = "Color Scheme", 
                subtitle = activeScheme, 
                showArrow = true,
                onClick = onNavigateToColorScheme
            )
            Spacer(modifier = Modifier.height(16.dp))

            val themeMode by ThemeManager.themeMode.collectAsState()
            SettingsRow(
                icon = Icons.Outlined.DarkMode, 
                title = "Theme Mode", 
                subtitle = themeMode, 
                showArrow = true,
                onClick = onNavigateToThemeMode
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
