package com.hotaro.strictclock.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.hotaro.strictclock.ui.theme.*
import kotlin.random.Random

@Composable
fun ColumnScope.PuzzleChallengeView(zenModeEnabled: Boolean, onSnoozeAlarm: () -> Unit, onStopAlarm: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    
    val puzzleType = prefs.getString("puzzle_type", "Memory Match") ?: "Memory Match"
    val puzzleCount = prefs.getInt("puzzle_count", 1)
    
    var puzzlesSolved by remember { mutableStateOf(0) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        colors = CardDefaults.cardColors(containerColor = surfaceDark),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(puzzleType.uppercase(), color = primaryDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Puzzles solved: $puzzlesSolved / $puzzleCount", color = onSurfaceDark, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Puzzle Content
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), contentAlignment = Alignment.Center) {
                
                val onPuzzleCompleted: () -> Unit = {
                    if (puzzlesSolved + 1 >= puzzleCount) {
                        onStopAlarm()
                    } else {
                        puzzlesSolved++
                    }
                }
                
                when (puzzleType) {
                    "Memory Match" -> MemoryMatchGame(onSolved = onPuzzleCompleted)
                    "Sequence" -> SequenceGame(onSolved = onPuzzleCompleted)
                    "Sliding Puzzle" -> SlidingPuzzleGame(onSolved = onPuzzleCompleted)
                    "Color Trick" -> ColorTrickGame(onSolved = onPuzzleCompleted)
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$puzzleType is coming soon!", color = onSurfaceDark)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onPuzzleCompleted) {
                                Text("Skip Puzzle")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (!zenModeEnabled) {
                Button(
                    onClick = onSnoozeAlarm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = surfaceVariantDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Snooze (+5 min)", color = onSurfaceVariantDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MemoryMatchGame(onSolved: () -> Unit) {
    val icons = listOf("🍎", "🍌", "🍉", "🍇", "🍓", "🍒", "🍑", "🍍")
    // Re-initialize deck only when the composable first enters or after a solve, but here we just need one puzzle per call
    val deck = remember { (icons + icons).shuffled(Random(System.currentTimeMillis())) }
    
    val flippedIndices = remember { mutableStateListOf<Int>() }
    val matchedIndices = remember { mutableStateListOf<Int>() }
    var isProcessing by remember { mutableStateOf(false) }
    
    LaunchedEffect(flippedIndices.size) {
        if (flippedIndices.size == 2) {
            isProcessing = true
            val first = flippedIndices[0]
            val second = flippedIndices[1]
            if (deck[first] == deck[second]) {
                matchedIndices.add(first)
                matchedIndices.add(second)
                flippedIndices.clear()
                if (matchedIndices.size == deck.size) {
                    delay(500)
                    onSolved()
                }
            } else {
                delay(800)
                flippedIndices.clear()
            }
            isProcessing = false
        }
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(deck.size) { index ->
            val isFlipped = flippedIndices.contains(index) || matchedIndices.contains(index)
            val isMatched = matchedIndices.contains(index)
            
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isMatched) primaryContainerDark else if (isFlipped) surfaceVariantDark else primaryDark)
                    .clickable(enabled = !isFlipped && !isProcessing) {
                        flippedIndices.add(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isFlipped) {
                    Text(deck[index], fontSize = 28.sp)
                }
            }
        }
    }
}

@Composable
fun SequenceGame(onSolved: () -> Unit) {
    val sequenceLength = 5
    var sequence by remember { mutableStateOf(List(sequenceLength) { Random.nextInt(9) }) }
    var userIndex by remember { mutableStateOf(0) }
    var showingSequence by remember { mutableStateOf(true) }
    var activeIndex by remember { mutableStateOf(-1) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(sequence, isError) {
        if (isError) {
            delay(500)
            isError = false
            sequence = List(sequenceLength) { Random.nextInt(9) }
        }
        
        showingSequence = true
        userIndex = 0
        delay(1000)
        for (index in sequence) {
            activeIndex = index
            delay(500)
            activeIndex = -1
            delay(200)
        }
        showingSequence = false
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (showingSequence) "Watch carefully..." else "Repeat the sequence!", 
            color = onSurfaceDark, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.width(240.dp)
        ) {
            items(9) { index ->
                val isActive = index == activeIndex
                val color by animateColorAsState(
                    targetValue = if (isError) errorDark else if (isActive) primaryContainerDark else primaryDark,
                    label = "color"
                )
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color)
                        .clickable(enabled = !showingSequence && !isError) {
                            if (index == sequence[userIndex]) {
                                userIndex++
                                if (userIndex == sequenceLength) {
                                    onSolved()
                                }
                            } else {
                                isError = true
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun SlidingPuzzleGame(onSolved: () -> Unit) {
    var board by remember { mutableStateOf(generateSolvableSlidingPuzzle()) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Order the numbers 1 to 8", color = onSurfaceDark, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.width(260.dp)
        ) {
            items(board.size) { index ->
                val num = board[index]
                val isEmpty = num == 8
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isEmpty) surfaceDark else primaryDark)
                        .clickable(enabled = !isEmpty) {
                            val emptyIndex = board.indexOf(8)
                            // Check if adjacent
                            val isAdjacent = (index == emptyIndex - 1 && emptyIndex % 3 != 0) ||
                                             (index == emptyIndex + 1 && emptyIndex % 3 != 2) ||
                                             (index == emptyIndex - 3) ||
                                             (index == emptyIndex + 3)
                            
                            if (isAdjacent) {
                                val newBoard = board.toMutableList()
                                newBoard[emptyIndex] = num
                                newBoard[index] = 8
                                board = newBoard
                                
                                if (board == listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)) {
                                    onSolved()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isEmpty) {
                        Text((num + 1).toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = onPrimaryDark)
                    }
                }
            }
        }
    }
}

fun generateSolvableSlidingPuzzle(): List<Int> {
    val current = (0..8).toMutableList()
    var emptyIndex = 8
    val moves = listOf(-1, 1, -3, 3)
    
    // Scramble by making 100 random valid moves
    for (i in 0..100) {
        val validMoves = moves.filter {
            val newIndex = emptyIndex + it
            if (newIndex !in 0..8) false
            else if (emptyIndex % 3 == 0 && it == -1) false
            else if (emptyIndex % 3 == 2 && it == 1) false
            else true
        }
        val move = validMoves.random()
        val swap = emptyIndex + move
        
        val temp = current[emptyIndex]
        current[emptyIndex] = current[swap]
        current[swap] = temp
        emptyIndex = swap
    }
    
    // Ensure it's not accidentally solved
    if (current == listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)) {
        return generateSolvableSlidingPuzzle()
    }
    return current
}

@Composable
fun ColorTrickGame(onSolved: () -> Unit) {
    val colorPairs = listOf(
        "RED" to Color(0xFFE57373),
        "BLUE" to Color(0xFF64B5F6),
        "GREEN" to Color(0xFF81C784),
        "YELLOW" to Color(0xFFFFF176)
    )
    
    var targetWord by remember { mutableStateOf(colorPairs.random()) }
    var targetColor by remember { mutableStateOf(colorPairs.random()) }
    
    // Ensure they are different
    LaunchedEffect(targetWord) {
        var nextColor = colorPairs.random()
        while (nextColor == targetWord) {
            nextColor = colorPairs.random()
        }
        targetColor = nextColor
    }
    
    var score by remember { mutableStateOf(0) }
    val requiredScore = 5
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tap the INK COLOR, not the word", color = onSurfaceDark, fontSize = 16.sp)
        Text("Streak: $score / $requiredScore", color = onSurfaceVariantDark, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceVariantDark)
        ) {
            Text(
                text = targetWord.first,
                color = targetColor.second,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Buttons for the 4 colors
        val shuffledOptions = remember(targetWord) { colorPairs.shuffled() }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(280.dp)
        ) {
            items(shuffledOptions.size) { index ->
                val option = shuffledOptions[index]
                Button(
                    onClick = {
                        if (option.first == targetColor.first) {
                            score++
                            if (score >= requiredScore) {
                                onSolved()
                            } else {
                                // Next round
                                targetWord = colorPairs.random()
                            }
                        } else {
                            score = 0 // Reset on failure
                            targetWord = colorPairs.random()
                        }
                    },
                    modifier = Modifier.height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(option.first, color = onPrimaryDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
