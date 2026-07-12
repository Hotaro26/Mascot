package com.hotaro.strictclock.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)

    var puzzleType by remember { mutableStateOf(prefs.getString("puzzle_type", "Memory Match") ?: "Memory Match") }
    var puzzleDifficulty by remember { mutableStateOf(prefs.getString("puzzle_difficulty", "Medium") ?: "Medium") }
    var puzzleCount by remember { mutableStateOf(prefs.getInt("puzzle_count", 1)) }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Puzzle Settings", fontWeight = FontWeight.SemiBold) },
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

            // Puzzle Type
            Text("Puzzle Type", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Row 1 of Types
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = puzzleType == "Memory Match",
                    onClick = {
                        puzzleType = "Memory Match"
                        prefs.edit().putString("puzzle_type", puzzleType).apply()
                    },
                    label = { Text("Memory Match") }
                )
                FilterChip(
                    selected = puzzleType == "Sequence",
                    onClick = {
                        puzzleType = "Sequence"
                        prefs.edit().putString("puzzle_type", puzzleType).apply()
                    },
                    label = { Text("Sequence") }
                )
            }
            // Row 2 of Types
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = puzzleType == "Sliding Puzzle",
                    onClick = {
                        puzzleType = "Sliding Puzzle"
                        prefs.edit().putString("puzzle_type", puzzleType).apply()
                    },
                    label = { Text("Sliding Puzzle") }
                )
                FilterChip(
                    selected = puzzleType == "Color Trick",
                    onClick = {
                        puzzleType = "Color Trick"
                        prefs.edit().putString("puzzle_type", puzzleType).apply()
                    },
                    label = { Text("Color Trick") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Difficulty
            Text("Difficulty", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = puzzleDifficulty == "Easy",
                    onClick = { 
                        puzzleDifficulty = "Easy"
                        prefs.edit().putString("puzzle_difficulty", puzzleDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) { Text("Easy") }
                SegmentedButton(
                    selected = puzzleDifficulty == "Medium",
                    onClick = { 
                        puzzleDifficulty = "Medium"
                        prefs.edit().putString("puzzle_difficulty", puzzleDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) { Text("Medium") }
                SegmentedButton(
                    selected = puzzleDifficulty == "Hard",
                    onClick = { 
                        puzzleDifficulty = "Hard"
                        prefs.edit().putString("puzzle_difficulty", puzzleDifficulty).apply()
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) { Text("Hard") }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Number of Puzzles
            Text("Number of puzzles to solve", color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("$puzzleCount Puzzles", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            if (puzzleCount > 1) {
                                puzzleCount--
                                prefs.edit().putInt("puzzle_count", puzzleCount).apply()
                            }
                        },
                        modifier = Modifier
                            .background(surfaceVariantDark, RoundedCornerShape(12.dp))
                            .size(40.dp)
                    ) { Text("-", color = onSurfaceDark, fontSize = 24.sp) }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = { 
                            if (puzzleCount < 10) {
                                puzzleCount++
                                prefs.edit().putInt("puzzle_count", puzzleCount).apply()
                            }
                        },
                        modifier = Modifier
                            .background(surfaceVariantDark, RoundedCornerShape(12.dp))
                            .size(40.dp)
                    ) { Text("+", color = onSurfaceDark, fontSize = 24.sp) }
                }
            }
        }
    }
}
