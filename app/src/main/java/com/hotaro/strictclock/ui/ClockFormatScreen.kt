package com.hotaro.strictclock.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockFormatScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    var clockFormat by remember { mutableStateOf(prefs.getString("clock_format", "12") ?: "12") }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal + androidx.compose.foundation.layout.WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = { Text("Clock Format", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = onSurfaceDark) },
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
            Text("Choose your preferred time format", color = onSurfaceVariantDark, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            // 12 Hour format
            Card(
                onClick = { 
                    clockFormat = "12"
                    prefs.edit().putString("clock_format", "12").apply()
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (clockFormat == "12") primaryContainerDark else surfaceContainerHighDark
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text("12-hour (AM/PM)", color = if (clockFormat == "12") onPrimaryContainerDark else onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 24 Hour format
            Card(
                onClick = { 
                    clockFormat = "24"
                    prefs.edit().putString("clock_format", "24").apply()
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (clockFormat == "24") primaryContainerDark else surfaceContainerHighDark
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text("24-hour", color = if (clockFormat == "24") onPrimaryContainerDark else onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
