package com.hotaro.strictclock.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Flashbang") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = backgroundDark,
                    scrolledContainerColor = surfaceContainerDark,
                    titleContentColor = onSurfaceDark,
                    navigationIconContentColor = onSurfaceDark
                )
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // Master Toggle
            ListItem(
                headlineContent = { Text("Enable Flashbang", style = MaterialTheme.typography.titleLarge) },
                supportingContent = { Text("Flash the screen to wake you up", style = MaterialTheme.typography.bodyMedium) },
                trailingContent = {
                    Switch(checked = isEnabled, onCheckedChange = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isEnabled = !isEnabled
                        prefs.edit().putBoolean("flashbang_enabled", isEnabled).apply()
                    },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
            )
            
            if (isEnabled) {
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = outlineVariantDark)
                
                // Full Brightness Toggle
                ListItem(
                    headlineContent = { Text("Full Display Brightness", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text("Maximize screen brightness while ringing", style = MaterialTheme.typography.bodyMedium) },
                    trailingContent = {
                        Switch(checked = fullBrightness, onCheckedChange = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            fullBrightness = !fullBrightness
                            prefs.edit().putBoolean("flashbang_full_brightness", fullBrightness).apply()
                        },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
                )
                
                // Blinking Toggle
                ListItem(
                    headlineContent = { Text("Blinking Effect", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text("Strobe the screen rather than a solid color", style = MaterialTheme.typography.bodyMedium) },
                    trailingContent = {
                        Switch(checked = isBlinking, onCheckedChange = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isBlinking = !isBlinking
                            prefs.edit().putBoolean("flashbang_blinking", isBlinking).apply()
                        },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
                )
                
                if (isBlinking) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Text("Blink Interval: ${blinkSpeed.toInt()} ms", style = MaterialTheme.typography.titleMedium, color = onSurfaceDark)
                        Spacer(modifier = Modifier.height(8.dp))
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
                            Text("Fast (100ms)", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariantDark)
                            Text("Slow (1000ms)", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariantDark)
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = outlineVariantDark)
                
                // Colors Multi-select
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text("Flash Colors", style = MaterialTheme.typography.titleMedium, color = primaryDark)
                    Text("Select multiple colors to alternate between them", style = MaterialTheme.typography.bodyMedium, color = onSurfaceVariantDark)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Avoid infinite height in ScrollView
                    ) {
                        items(availableColors.size) { index ->
                            val (hexCode, name) = availableColors[index]
                            val isSelected = selectedColors.contains(hexCode)
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hexCode)))
                                        .border(
                                            width = if (isSelected) 4.dp else 1.dp,
                                            color = if (isSelected) primaryDark else outlineVariantDark,
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
                                        val checkColor = if (hexCode == "#FFFFFF" || hexCode == "#FFFF00") Color.Black else Color.White
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = checkColor, modifier = Modifier.size(32.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(name, style = MaterialTheme.typography.labelLarge, color = onSurfaceDark)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
