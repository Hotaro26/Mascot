package com.hotaro.strictclock.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.activity.compose.BackHandler
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp).size(40.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Image(
                painter = painterResource(id = R.drawable.about_image),
                contentDescription = "App Icon",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.size(100.dp).clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "mascot",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            val currentContext = LocalContext.current
            val versionName = remember {
                try {
                    val packageInfo = currentContext.packageManager.getPackageInfo(currentContext.packageName, 0)
                    packageInfo.versionName ?: "Unknown"
                } catch (e: Exception) {
                    "Unknown"
                }
            }

            Text(
                text = "Version $versionName",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About the App", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "mascot is an open-source productivity alarm app designed to force you out of bed with strict challenge tasks such as QR scanning, Math problems, and on-device AI Object recognition.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Developer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val uriHandler = LocalUriHandler.current
                    val context = LocalContext.current
                    var showDiscordPopup by remember { mutableStateOf(false) }
                    
                    if (showDiscordPopup) {
                        AlertDialog(
                            onDismissRequest = { showDiscordPopup = false },
                            title = { Text("Discord", color = MaterialTheme.colorScheme.onSurface) },
                            text = { Text("Add me on Discord: oi.hotaro", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            confirmButton = {
                                TextButton(onClick = { showDiscordPopup = false }) {
                                    Text("Close", color = MaterialTheme.colorScheme.primary)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // GitHub
                        IconButton(onClick = { uriHandler.openUri("https://github.com/Hotaro26") }) {
                            Icon(painterResource(id = R.drawable.ic_github), contentDescription = "GitHub", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        
                        // Pinterest
                        IconButton(onClick = { uriHandler.openUri("https://pinterest.com/hotaro344") }) {
                            Icon(painterResource(id = R.drawable.ic_pinterest), contentDescription = "Pinterest", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        
                        // Email
                        IconButton(onClick = { 
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:sakibreza035@gmail.com"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Outlined.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        
                        // Discord
                        IconButton(onClick = { showDiscordPopup = true }) {
                            Icon(painterResource(id = R.drawable.ic_discord), contentDescription = "Discord", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
