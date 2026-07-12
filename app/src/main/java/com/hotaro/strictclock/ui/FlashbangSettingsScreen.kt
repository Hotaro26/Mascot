package com.hotaro.strictclock.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashbangSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)

    var isEnabled by remember { mutableStateOf(prefs.getBoolean("flashbang_enabled", false)) }
    var fullBrightness by remember { mutableStateOf(prefs.getBoolean("flashbang_full_brightness", true)) }
    var isBlinking by remember { mutableStateOf(prefs.getBoolean("flashbang_blinking", true)) }
    var blinkSpeed by remember { mutableStateOf(prefs.getFloat("flashbang_blink_speed", 500f)) } // ms interval
    
    // Stored as a comma-separated string of hex colors
    val defaultColors = listOf("#FFFFFF", "#FF0000", "#FFFF00").joinToString(",")
    var selectedColorsStr by remember { mutableStateOf(prefs.getString("flashbang_colors", defaultColors) ?: defaultColors) }
    
    val selectedColors = selectedColorsStr.split(",").filter { it.isNotEmpty() }.toMutableSet()
    
    val availableColors = listOf(
        "#FFFFFF" to "White",
        "#FF0000" to "Red",
        "#FFFF00" to "Yellow",
        "#0000FF" to "Blue",
        "#00FF00" to "Green",
        "#FF00FF" to "Magenta"
    )

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashbang Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark, titleContentColor = onSurfaceDark, navigationIconContentColor = onSurfaceDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Master Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Flashbang", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { 
                        isEnabled = it
                        prefs.edit().putBoolean("flashbang_enabled", it).apply()
                    }
                )
            }
            
            if (isEnabled) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Full Brightness Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Full Display Brightness", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Maximize screen brightness while ringing", color = onSurfaceVariantDark, fontSize = 14.sp)
                    }
                    Switch(
                        checked = fullBrightness,
                        onCheckedChange = { 
                            fullBrightness = it
                            prefs.edit().putBoolean("flashbang_full_brightness", it).apply()
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Blinking Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Blinking Effect", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isBlinking,
                        onCheckedChange = { 
                            isBlinking = it
                            prefs.edit().putBoolean("flashbang_blinking", it).apply()
                        }
                    )
                }
                
                if (isBlinking) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Blink Interval: ${blinkSpeed.toInt()} ms", color = onSurfaceDark, fontSize = 14.sp)
                    Slider(
                        value = blinkSpeed,
                        onValueChange = { 
                            blinkSpeed = it 
                            prefs.edit().putFloat("flashbang_blink_speed", it).apply()
                        },
                        valueRange = 100f..1000f,
                        steps = 8
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fast (100ms)", color = onSurfaceVariantDark, fontSize = 12.sp)
                        Text("Slow (1000ms)", color = onSurfaceVariantDark, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Colors Multi-select
                Text("Flash Colors", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Select multiple colors to alternate between them", color = onSurfaceVariantDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableColors.size) { index ->
                        val (hexCode, name) = availableColors[index]
                        val isSelected = selectedColors.contains(hexCode)
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hexCode)))
                                    .border(
                                        width = if (isSelected) 4.dp else 1.dp,
                                        color = if (isSelected) primaryDark else onSurfaceVariantDark,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (isSelected && selectedColors.size > 1) {
                                            selectedColors.remove(hexCode)
                                        } else {
                                            selectedColors.add(hexCode)
                                        }
                                        selectedColorsStr = selectedColors.joinToString(",")
                                        prefs.edit().putString("flashbang_colors", selectedColorsStr).apply()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    // Use a contrasting color for the checkmark based on the background
                                    val checkColor = if (hexCode == "#FFFFFF" || hexCode == "#FFFF00") Color.Black else Color.White
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = checkColor)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(name, color = onSurfaceDark, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
