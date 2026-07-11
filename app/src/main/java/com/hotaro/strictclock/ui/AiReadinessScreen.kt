package com.hotaro.strictclock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReadinessScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Readiness", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp).size(28.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundDark,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Bundled AI Model", color = primaryDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = primaryDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Google ML Kit", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text("Image Labeling (Default Common)", color = onSurfaceVariantDark, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Security, contentDescription = null, tint = onSurfaceVariantDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("100% Offline & Privacy Preserving", color = onSurfaceVariantDark, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Speed, contentDescription = null, tint = onSurfaceVariantDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Optimized 5.7 MB Bundled Model", color = onSurfaceVariantDark, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Camera Challenge Targets", color = onSurfaceDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "These are the highly accurate targets supported by the bundled model. You can select any of these when setting up a Camera strict mode task.",
                color = onSurfaceVariantDark, fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val targets = listOf("Sink", "Chair", "Mug", "Keyboard", "Shoe", "Toothbrush")
            
            targets.forEach { target ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(target, color = onSurfaceDark, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = "Supported", tint = primaryDark, modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = outlineDark.copy(alpha = 0.5f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
