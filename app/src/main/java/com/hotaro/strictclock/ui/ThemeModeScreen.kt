package com.hotaro.strictclock.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun ThemeModeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val themeMode by ThemeManager.themeMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Mode", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ThemeModeOption(
                title = "System Default",
                subtitle = "Follows your device settings",
                isSelected = themeMode == "System",
                onClick = { ThemeManager.setThemeMode("System") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ThemeModeOption(
                title = "Light Mode",
                subtitle = "Always use light colors",
                isSelected = themeMode == "Light",
                onClick = { ThemeManager.setThemeMode("Light") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeModeOption(
                title = "Dark Mode",
                subtitle = "Always use dark colors",
                isSelected = themeMode == "Dark",
                onClick = { ThemeManager.setThemeMode("Dark") }
            )
        }
    }
}

@Composable
fun ThemeModeOption(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
            if (isSelected) {
                RadioButton(
                    selected = true,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
