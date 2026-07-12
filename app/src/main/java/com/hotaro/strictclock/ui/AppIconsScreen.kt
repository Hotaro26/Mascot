package com.hotaro.strictclock.ui

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.strictclock.R
import com.hotaro.strictclock.ui.theme.*

data class AppIcon(val id: Int, val name: String, val aliasClass: String, val resourceId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconsScreen(onBack: () -> Unit) {
    androidx.activity.compose.BackHandler { onBack() }
    
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("strict_clock_prefs", Context.MODE_PRIVATE)
    var selectedIconId by remember { mutableStateOf(prefs.getInt("selected_app_icon", 1)) }

    val icons = listOf(
        AppIcon(1, "Mascot 1", "com.hotaro.strictclock.AliasIcon1", R.mipmap.mascot_icon1),
        AppIcon(2, "Mascot 2", "com.hotaro.strictclock.AliasIcon2", R.mipmap.mascot_icon2),
        AppIcon(3, "Mascot 3", "com.hotaro.strictclock.AliasIcon3", R.mipmap.mascot_icon3),
        AppIcon(4, "Mascot 4", "com.hotaro.strictclock.AliasIcon4", R.mipmap.mascot_icon4),
        AppIcon(5, "Mascot 5", "com.hotaro.strictclock.AliasIcon5", R.mipmap.mascot_icon5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Icons", fontWeight = FontWeight.Bold, color = onSurfaceDark, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark)
            )
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Choose your mascot",
                color = onSurfaceVariantDark,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(icons) { icon ->
                    val isSelected = selectedIconId == icon.id
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) primaryContainerDark else surfaceContainerHighDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                if (!isSelected) {
                                    selectedIconId = icon.id
                                    prefs.edit().putInt("selected_app_icon", icon.id).apply()
                                    changeAppIcon(context, icons, icon)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = icon.resourceId),
                                contentDescription = icon.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = icon.name,
                                color = if (isSelected) onPrimaryContainerDark else onSurfaceDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun changeAppIcon(context: Context, icons: List<AppIcon>, selectedIcon: AppIcon) {
    val packageManager = context.packageManager
    
    // Disable all other aliases first
    icons.forEach { icon ->
        if (icon.id != selectedIcon.id) {
            packageManager.setComponentEnabledSetting(
                ComponentName(context, icon.aliasClass),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    // Enable the selected alias
    packageManager.setComponentEnabledSetting(
        ComponentName(context, selectedIcon.aliasClass),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
    
    Toast.makeText(context, "App icon changed! Your launcher might refresh.", Toast.LENGTH_SHORT).show()
}
