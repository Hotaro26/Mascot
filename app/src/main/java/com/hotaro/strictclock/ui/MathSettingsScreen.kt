package com.hotaro.strictclock.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.CircleShape
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)

    var mathOperations by remember { mutableStateOf(prefs.getString("math_operations", "Addition,Subtraction") ?: "Addition,Subtraction") }
    var mathDifficulty by remember { mutableStateOf(prefs.getString("math_difficulty", "Easy") ?: "Easy") }
    var mathSums by remember { mutableStateOf(prefs.getInt("math_sums", 3)) }

    val ops = mathOperations.split(",").filter { it.isNotEmpty() }.toSet()

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Challenge Settings", fontWeight = FontWeight.SemiBold) },
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

            // Operations
            Text("Operations", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = ops.contains("Addition"),
                    onClick = {
                        val newOps = if (ops.contains("Addition")) ops - "Addition" else ops + "Addition"
                        val finalOps = if (newOps.isEmpty()) setOf("Addition") else newOps
                        mathOperations = finalOps.joinToString(",")
                        prefs.edit().putString("math_operations", mathOperations).apply()
                    },
                    label = { Text("+ Addition") }
                )
                FilterChip(
                    selected = ops.contains("Subtraction"),
                    onClick = {
                        val newOps = if (ops.contains("Subtraction")) ops - "Subtraction" else ops + "Subtraction"
                        val finalOps = if (newOps.isEmpty()) setOf("Subtraction") else newOps
                        mathOperations = finalOps.joinToString(",")
                        prefs.edit().putString("math_operations", mathOperations).apply()
                    },
                    label = { Text("- Subtraction") }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = ops.contains("Multiplication"),
                    onClick = {
                        val newOps = if (ops.contains("Multiplication")) ops - "Multiplication" else ops + "Multiplication"
                        val finalOps = if (newOps.isEmpty()) setOf("Multiplication") else newOps
                        mathOperations = finalOps.joinToString(",")
                        prefs.edit().putString("math_operations", mathOperations).apply()
                    },
                    label = { Text("× Multiplication") }
                )
                FilterChip(
                    selected = ops.contains("Division"),
                    onClick = {
                        val newOps = if (ops.contains("Division")) ops - "Division" else ops + "Division"
                        val finalOps = if (newOps.isEmpty()) setOf("Division") else newOps
                        mathOperations = finalOps.joinToString(",")
                        prefs.edit().putString("math_operations", mathOperations).apply()
                    },
                    label = { Text("÷ Division") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Difficulty
            Text("Difficulty", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = mathDifficulty == "Easy",
                    onClick = { 
                        mathDifficulty = "Easy"
                        prefs.edit().putString("math_difficulty", mathDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) { Text("Easy") }
                SegmentedButton(
                    selected = mathDifficulty == "Medium",
                    onClick = { 
                        mathDifficulty = "Medium"
                        prefs.edit().putString("math_difficulty", mathDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) { Text("Medium") }
                SegmentedButton(
                    selected = mathDifficulty == "Hard",
                    onClick = { 
                        mathDifficulty = "Hard"
                        prefs.edit().putString("math_difficulty", mathDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) { Text("Hard") }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Number of Sums
            Text("Number of sums to solve", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("$mathSums Sums", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            if (mathSums > 1) {
                                mathSums--
                                prefs.edit().putInt("math_sums", mathSums).apply()
                            }
                        },
                        modifier = Modifier.background(surfaceVariantDark, RoundedCornerShape(12.dp)).size(40.dp)
                    ) { Text("-", color = onSurfaceDark, fontSize = 24.sp) }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = { 
                            if (mathSums < 20) {
                                mathSums++
                                prefs.edit().putInt("math_sums", mathSums).apply()
                            }
                        },
                        modifier = Modifier.background(surfaceVariantDark, RoundedCornerShape(12.dp)).size(40.dp)
                    ) { Text("+", color = onSurfaceDark, fontSize = 24.sp) }
                }
            }
        }
    }
}
