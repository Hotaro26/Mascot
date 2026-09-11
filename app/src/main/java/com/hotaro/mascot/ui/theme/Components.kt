package com.hotaro.mascot.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue

@Composable
fun ExpressiveSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val icon: (@Composable () -> Unit)? = if (checked) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    }

    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = icon,
        colors = SwitchDefaults.colors(
            checkedThumbColor = primaryDark,
            checkedTrackColor = primaryContainerDark,
            checkedIconColor = onPrimaryDark,
            uncheckedThumbColor = outlineDark,
            uncheckedTrackColor = surfaceContainerHighestDark,
            uncheckedIconColor = surfaceContainerHighestDark
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isActive = isPressed || isDragged

    // Expressive Motion: Shape morphing on press
    val thumbWidth by animateDpAsState(
        targetValue = if (isActive) 16.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "thumbWidth"
    )
    val thumbHeight by animateDpAsState(
        targetValue = if (isActive) 32.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "thumbHeight"
    )
    val thumbRadius by animateDpAsState(
        targetValue = if (isActive) 16.dp else 4.dp, // Morphs from pill to rounder shape
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "thumbRadius"
    )

    val colors = SliderDefaults.colors(
        thumbColor = primaryDark,
        activeTrackColor = primaryDark,
        activeTickColor = onPrimaryDark,
        inactiveTrackColor = surfaceContainerHighDark,
        inactiveTickColor = primaryDark.copy(alpha = 0.38f)
    )
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        colors = colors,
        interactionSource = interactionSource,
        thumb = {
            Box(
                modifier = Modifier
                    .size(width = thumbWidth, height = thumbHeight)
                    .background(colors.thumbColor, RoundedCornerShape(thumbRadius))
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                colors = colors,
                sliderState = sliderState,
                modifier = Modifier.height(16.dp)
            )
        }
    )
}
