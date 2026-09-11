package com.hotaro.mascot.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.mascot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpStreakScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", android.content.Context.MODE_PRIVATE)
    val streak = prefs.getInt("wake_up_streak", 0)
    
    val historySet = prefs.getStringSet("wake_up_history", setOf()) ?: setOf()
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    val parsedDates = historySet.mapNotNull { 
        try { dateTimeFormat.parse(it) } catch (e: Exception) { null }
    }.sorted()

    val dateStrings = parsedDates.map { dateFormat.format(it) }.toSet()
    
    var avgTimeString = "--:-- --"
    var totalWakes = parsedDates.size
    var bestDayStr = "--"
    
    val points = mutableListOf<Float>()
    
    if (parsedDates.isNotEmpty()) {
        var totalMinutes = 0
        val dayCounts = mutableMapOf<Int, Int>()
        
        parsedDates.forEach { date ->
            val cal = Calendar.getInstance().apply { time = date }
            totalMinutes += cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            dayCounts[dayOfWeek] = dayCounts.getOrDefault(dayOfWeek, 0) + 1
        }
        
        val avgMins = totalMinutes / parsedDates.size
        val avgHour = avgMins / 60
        val avgMin = avgMins % 60
        
        val avgCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, avgHour)
            set(Calendar.MINUTE, avgMin)
        }
        avgTimeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(avgCal.time)
        
        val bestDayInt = dayCounts.maxByOrNull { it.value }?.key ?: Calendar.SUNDAY
        bestDayStr = when (bestDayInt) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "--"
        }
        
        // Populate points for the graph based on the last 10 entries
        val recentDates = parsedDates.takeLast(10)
        recentDates.forEach { date ->
            val cal = Calendar.getInstance().apply { time = date }
            val hour = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f
            // Map 0-24 hours to 0-1 float where 1 is earlier (better), 0 is later
            // Say we expect wakeups between 4 AM and 12 PM
            val normalized = 1f - ((hour - 4f) / 8f).coerceIn(0f, 1f)
            points.add(normalized)
        }
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars.only(androidx.compose.foundation.layout.WindowInsetsSides.Horizontal + androidx.compose.foundation.layout.WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = { Text("Wake-up Streak", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark, titleContentColor = onSurfaceDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Current Streak Card
            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Current Streak", color = onPrimaryContainerDark.copy(alpha = 0.8f), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$streak Days", color = onPrimaryContainerDark, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Activity Heatmap", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Heatmap
            Card(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    HeatmapCanvas(dateStrings)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Average Wake-up Time", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Line Graph
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    if (points.isNotEmpty()) {
                        WakeUpTimeGraph(points)
                    } else {
                        Text("No data available yet", color = onSurfaceVariantDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Insights", color = onSurfaceDark, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Insights Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Schedule,
                    title = "Avg Time",
                    value = avgTimeString
                )
                Spacer(modifier = Modifier.width(16.dp))
                InsightCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CheckCircle,
                    title = "On Time",
                    value = if (totalWakes > 0) "100%" else "--%" // Based on streak logic
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Analytics,
                    title = "Best Day",
                    value = bestDayStr
                )
                Spacer(modifier = Modifier.width(16.dp))
                InsightCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Analytics,
                    title = "Total Wakes",
                    value = "$totalWakes"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeatmapCanvas(activeDates: Set<String>) {
    val boxSize = 24f // Make it bigger
    val spacing = 6f
    val cols = 10
    val rows = 7 // 7 days a week
    
    val pColor = primaryDark
    val emptyColor = surfaceContainerHighestDark
    
    val calendar = Calendar.getInstance()
    // Go back in time for the number of columns
    calendar.add(Calendar.DAY_OF_YEAR, -(cols * rows) + 1)
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalWidth = cols * (boxSize + spacing) - spacing
        val totalHeight = rows * (boxSize + spacing) - spacing
        
        val startX = (size.width - totalWidth) / 2
        val startY = (size.height - totalHeight) / 2

        val tempCal = calendar.clone() as Calendar

        for (col in 0 until cols) {
            for (row in 0 until rows) {
                val dateStr = dateFormat.format(tempCal.time)
                val isFilled = activeDates.contains(dateStr)
                
                val color = if (isFilled) pColor else emptyColor
                
                drawRoundRect(
                    color = color,
                    topLeft = Offset(startX + col * (boxSize + spacing), startY + row * (boxSize + spacing)),
                    size = Size(boxSize, boxSize),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                
                tempCal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }
}

@Composable
fun WakeUpTimeGraph(points: List<Float>) {
    val pColor = primaryDark
    val gridColor = outlineVariantDark

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val stepX = if (points.size > 1) width / (points.size - 1) else width
        
        // Draw grid lines
        for (i in 0..4) {
            val y = height * (i / 4f)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 2f
            )
        }

        if (points.size == 1) {
            val x = width / 2f
            val y = height * (1f - points[0])
            drawCircle(color = pColor, radius = 10f, center = Offset(x, y))
            return@Canvas
        }

        val path = Path()
        path.moveTo(0f, height * (1f - points[0]))
        
        for (i in 1 until points.size) {
            val x = i * stepX
            val y = height * (1f - points[i])
            path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = pColor,
            style = Stroke(width = 6f)
        )
        
        // Draw points
        for (i in points.indices) {
            val x = i * stepX
            val y = height * (1f - points[i])
            drawCircle(
                color = pColor,
                radius = 10f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun InsightCard(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Icon(icon, contentDescription = null, tint = primaryDark)
            Spacer(modifier = Modifier.weight(1f))
            Text(title, color = onSurfaceVariantDark, fontSize = 12.sp)
            Text(value, color = onSurfaceDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
