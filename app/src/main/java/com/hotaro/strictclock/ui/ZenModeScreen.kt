package com.hotaro.strictclock.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.activity.compose.BackHandler
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenModeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    // Zen Mode ON means snoozing is NOT allowed.
    var zenModeEnabled by remember { mutableStateOf(prefs.getBoolean("zen_mode", true)) }

    Scaffold(
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
        ) {
            Text("Zen Mode forces you to complete the challenge immediately without snoozing.", color = onSurfaceVariantDark, fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Don't allow snoozing", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Forces you to wake up", color = onSurfaceVariantDark, fontSize = 14.sp)
                    }
                    Switch(
                        checked = zenModeEnabled,
                        onCheckedChange = {
                            zenModeEnabled = it
                            prefs.edit().putBoolean("zen_mode", it).apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryDark,
                            checkedTrackColor = primaryContainerDark
                        )
                    )
                }
            }
        }
    }
}
