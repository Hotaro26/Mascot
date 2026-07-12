package com.hotaro.strictclock.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.draw.scale
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
                title = { Text("Flashbang", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = surfaceContainerDark,
                            contentColor = onSurfaceDark
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = backgroundDark,
                    scrolledContainerColor = surfaceContainerDark,
                    titleContentColor = onSurfaceDark
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
            Spacer(modifier = Modifier.height(16.dp))
            
            // Master Toggle Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = surfaceContainerDark),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.large
            ) {
                ListItem(
                    headlineContent = { Text("Enable Flashbang", style = MaterialTheme.typography.titleLarge) },
                    supportingContent = { Text("Flash the screen to wake you up", style = MaterialTheme.typography.bodyMedium) },
                    trailingContent = {
                        Switch(checked = isEnabled, onCheckedChange = { 
                            isEnabled = it
                            prefs.edit().putBoolean("flashbang_enabled", it).apply()
                        })
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent, 
                        headlineColor = onSurfaceDark, 
                        supportingColor = onSurfaceVariantDark
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = isEnabled,
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    Text(
                        "Behavior",
                        style = MaterialTheme.typography.titleSmall,
                        color = primaryDark,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                    
                    // Behavior Card
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = backgroundDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariantDark),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column {
                            // Full Brightness Toggle
                            ListItem(
                                headlineContent = { Text("Full Display Brightness", style = MaterialTheme.typography.titleMedium) },
                                supportingContent = { Text("Maximize screen brightness while ringing", style = MaterialTheme.typography.bodyMedium) },
                                trailingContent = {
                                    Switch(checked = fullBrightness, onCheckedChange = {
                                        fullBrightness = it
                                        prefs.edit().putBoolean("flashbang_full_brightness", it).apply()
                                    })
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
                            )
                            
                            HorizontalDivider(color = outlineVariantDark, modifier = Modifier.padding(horizontal = 16.dp))
                            
                            // Double Tap Dismiss Toggle
                            var doubleTapDismiss by remember { mutableStateOf(prefs.getBoolean("flashbang_double_tap_dismiss", false)) }
                            ListItem(
                                headlineContent = { Text("Double Tap to Dismiss", style = MaterialTheme.typography.titleMedium) },
                                supportingContent = { Text("Double tap on the wake-up screen to turn off the flash", style = MaterialTheme.typography.bodyMedium) },
                                trailingContent = {
                                    Switch(checked = doubleTapDismiss, onCheckedChange = {
                                        doubleTapDismiss = it
                                        prefs.edit().putBoolean("flashbang_double_tap_dismiss", it).apply()
                                    })
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
                            )
                            
                            HorizontalDivider(color = outlineVariantDark, modifier = Modifier.padding(horizontal = 16.dp))
                            
                            // Blinking Toggle
                            ListItem(
                                headlineContent = { Text("Blinking Effect", style = MaterialTheme.typography.titleMedium) },
                                supportingContent = { Text("Strobe the screen rather than a solid color", style = MaterialTheme.typography.bodyMedium) },
                                trailingContent = {
                                    Switch(checked = isBlinking, onCheckedChange = {
                                        isBlinking = it
                                        prefs.edit().putBoolean("flashbang_blinking", it).apply()
                                    })
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent, headlineColor = onSurfaceDark, supportingColor = onSurfaceVariantDark)
                            )
                            
                            AnimatedVisibility(visible = isBlinking) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                    Text("Blink Interval: ${blinkSpeed.toInt()} ms", style = MaterialTheme.typography.labelLarge, color = onSurfaceDark)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Slider(
                                        value = blinkSpeed,
                                        onValueChange = { 
                                            blinkSpeed = it 
                                            prefs.edit().putFloat("flashbang_blink_speed", it).apply()
                                        },
                                        valueRange = 100f..1000f,
                                        steps = 8,
                                        colors = SliderDefaults.colors(
                                            thumbColor = primaryDark,
                                            activeTrackColor = primaryDark,
                                            inactiveTrackColor = surfaceContainerHighDark
                                        )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Fast", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariantDark)
                                        Text("Slow", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariantDark)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "Flash Colors",
                        style = MaterialTheme.typography.titleSmall,
                        color = primaryDark,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                    
                    // Colors Card
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = surfaceContainerDark),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Select colors to strobe", style = MaterialTheme.typography.bodyMedium, color = onSurfaceVariantDark)
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                availableColors.forEach { (hexCode, _) ->
                                    val isSelected = selectedColors.contains(hexCode)
                                    val scale by androidx.compose.animation.core.animateFloatAsState(
                                        targetValue = if (isSelected) 1.15f else 1f,
                                        label = "color_scale"
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .scale(scale)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(hexCode)))
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
                                        androidx.compose.animation.AnimatedVisibility(visible = isSelected) {
                                            val checkColor = if (hexCode == "#FFFFFF" || hexCode == "#FFFF00") Color.Black else Color.White
                                            Icon(Icons.Default.Check, contentDescription = null, tint = checkColor, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}
