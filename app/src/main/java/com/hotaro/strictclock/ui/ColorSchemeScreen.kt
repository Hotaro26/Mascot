package com.hotaro.strictclock.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.hotaro.strictclock.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSchemeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val activeScheme by ThemeManager.activeScheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Color Scheme", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select your preferred color scheme", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            SchemeOption(
                title = "Dynamic (Material You)",
                subtitle = "Follows system wallpaper colors",
                isSelected = activeScheme == "Dynamic",
                onClick = { ThemeManager.setScheme("Dynamic") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SchemeOption(
                title = "Strict Default",
                subtitle = "The original mastco dark theme",
                isSelected = activeScheme == "Strict Default",
                onClick = { ThemeManager.setScheme("Strict Default") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SchemeOption(
                title = "Noctali",
                subtitle = "Dark blue and gray tones",
                isSelected = activeScheme == "Noctali",
                onClick = { ThemeManager.setScheme("Noctali") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SchemeOption(
                title = "Lavender",
                subtitle = "Deep purples and violets",
                isSelected = activeScheme == "Lavender",
                onClick = { ThemeManager.setScheme("Lavender") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SchemeOption(
                title = "Pink",
                subtitle = "Vibrant pink and crimson",
                isSelected = activeScheme == "Pink",
                onClick = { ThemeManager.setScheme("Pink") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SchemeOption(
                title = "Mocha",
                subtitle = "Warm browns and creams",
                isSelected = activeScheme == "Mocha",
                onClick = { ThemeManager.setScheme("Mocha") }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SchemeOption(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 14.sp, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Outlined.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
