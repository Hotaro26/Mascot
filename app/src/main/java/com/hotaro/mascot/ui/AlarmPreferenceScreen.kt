package com.hotaro.mascot.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hotaro.mascot.ui.theme.*
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmPreferenceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    var autoDelete by remember { mutableStateOf(prefs.getBoolean("auto_delete_one_time", false)) }
    var noSwipeConfirm by remember { mutableStateOf(prefs.getBoolean("no_swipe_delete_confirm", false)) }

    BackHandler { onBack() }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal + androidx.compose.foundation.layout.WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = { Text("Alarm Preferences", fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 20.sp) },
                navigationIcon = {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp).size(40.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark, titleContentColor = onSurfaceDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SettingsRowSwitch(
                icon = Icons.Outlined.DeleteSweep,
                title = "Auto-delete One-time Alarms",
                subtitle = "Delete non-repeating alarms after they ring",
                checked = autoDelete,
                onCheckedChange = { 
                    autoDelete = it
                    prefs.edit().putBoolean("auto_delete_one_time", it).apply()
                },
                topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            SettingsRowSwitch(
                icon = Icons.Outlined.Delete,
                title = "No swipe delete confirmation",
                subtitle = "Delete alarms instantly without asking",
                checked = noSwipeConfirm,
                onCheckedChange = { 
                    noSwipeConfirm = it
                    prefs.edit().putBoolean("no_swipe_delete_confirm", it).apply()
                },
                topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp
            )
        }
    }
}
