package com.hotaro.mascot.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.mascot.ui.theme.ThemeManager

@Composable
fun ColorSchemeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val activeScheme by ThemeManager.activeScheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Custom sleek header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        
        Text(
            text = "Color Scheme",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Text(
            text = "Select your preferred color scheme",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic Colors Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.large
        ) {
            SchemeOption(
                title = "Dynamic (Material You)",
                subtitle = "Follows system wallpaper colors",
                isSelected = activeScheme == "Dynamic",
                onClick = { ThemeManager.setScheme("Dynamic") },
                isTopLevel = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Presets",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )

        // Preset Colors Card
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column {
                val presets = listOf(
                    "Strict Default" to "The original mascot dark theme",
                    "Noctali" to "Dark blue and gray tones",
                    "Lavender" to "Deep purples and violets",
                    "Pink" to "Vibrant pink and crimson",
                    "Mocha" to "Warm browns and creams"
                )
                
                presets.forEachIndexed { index, (title, subtitle) ->
                    SchemeOption(
                        title = title,
                        subtitle = subtitle,
                        isSelected = activeScheme == title,
                        onClick = { ThemeManager.setScheme(title) },
                        isTopLevel = false
                    )
                    
                    if (index < presets.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun SchemeOption(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit, isTopLevel: Boolean) {
    ListItem(
        headlineContent = { 
            Text(
                title, 
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ) 
        },
        supportingContent = { 
            Text(
                subtitle, 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    Icons.Default.Check, 
                    contentDescription = "Selected", 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}
