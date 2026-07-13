package com.hotaro.strictclock.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.hotaro.strictclock.service.StopwatchManager
import com.hotaro.strictclock.service.StopwatchService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchTabContent() {
    val context = LocalContext.current
    val elapsedMillis by StopwatchManager.elapsedMillis.collectAsState()
    val isRunning by StopwatchManager.isRunning.collectAsState()
    val laps by StopwatchManager.laps.collectAsState()

    val minutes = (elapsedMillis / 1000 / 60)
    val seconds = (elapsedMillis / 1000) % 60
    val centiseconds = (elapsedMillis % 1000) / 10

    val topText = if (minutes > 0) {
        String.format("%02d:%02d", minutes, seconds)
    } else {
        String.format("%02d", seconds)
    }
    val bottomText = String.format("%02d", centiseconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Spacer(modifier = Modifier.weight(1f))

            // The Stopwatch Circle
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circle border
                val outlineColor = MaterialTheme.colorScheme.surfaceVariant
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = outlineColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Time text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = topText,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 80.sp
                    )
                    Text(
                        text = bottomText,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 48.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = { 
                        val intent = Intent(context, StopwatchService::class.java)
                        intent.action = StopwatchService.ACTION_RESET
                        context.startService(intent)
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Play/Pause Button
                val playPauseColor by animateColorAsState(
                    targetValue = if (isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                )
                val playPauseIconColor by animateColorAsState(
                    targetValue = if (isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
                )

                IconButton(
                    onClick = { 
                        val intent = Intent(context, StopwatchService::class.java)
                        intent.action = if (isRunning) StopwatchService.ACTION_PAUSE else StopwatchService.ACTION_START
                        if (!isRunning && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(playPauseColor)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = playPauseIconColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Lap Button
                IconButton(
                    onClick = { 
                        val intent = Intent(context, StopwatchService::class.java)
                        intent.action = StopwatchService.ACTION_LAP
                        context.startService(intent)
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Lap",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
    }
}
