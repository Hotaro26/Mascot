package com.hotaro.mascot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.mascot.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ClockOnlyScreen(viewModel: AlarmViewModel? = null) {
    val rawAlarms by viewModel?.allAlarms?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val nextAlarm = remember(rawAlarms) {
        rawAlarms.filter { it.isActive }.minByOrNull { com.hotaro.mascot.utils.AlarmUtils.getNextTriggerTime(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SystemClockCard(nextAlarm = nextAlarm)
    }
}
