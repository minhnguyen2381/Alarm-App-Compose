package com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingAccent
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingSwipeHandle
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingSwipeTrack
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


private enum class DismissValue { Start, End }

@Composable
fun SwipeToDismiss(
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val handleSizePx = with(density) { 56.dp.toPx() }

    val anchoredDraggableState by remember {
        AnchoredDraggableState(
            initialValue = DismissValue.Start,
            positionalThreshold = { totalDistance -> totalDistance * 0.7f },
            velocityThreshold = { with(density) { 200.dp.toPx() } },
            animationSpec = tween(),
        )
    }

    LaunchedEffect(anchoredDraggableState.currentValue) {
        if (anchoredDraggableState.currentValue == DismissValue.End) {
            onDismissed()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(AlarmRingSwipeTrack)
            .onSizeChanged { size ->
                val maxOffset = size.width.toFloat() - handleSizePx
                if (maxOffset > 0) {
                    anchoredDraggableState.updateAnchors(
                        DraggableAnchors {
                            DismissValue.Start at 0f
                            DismissValue.End at maxOffset
                        }
                    )
                }
            },
    ) {
        // "SWIPE TO TURN OFF" text, fades as handle progresses
        val offset = try {
            anchoredDraggableState.requireOffset()
        } catch (_: IllegalStateException) {
            0f
        }
        val maxOffset = anchoredDraggableState.anchors.positionOf(DismissValue.End)
        val textAlpha = if (maxOffset > 0f) {
            (1f - (offset / maxOffset)).coerceIn(0f, 1f)
        } else {
            1f
        }

        Text(
            text = stringResource(R.string.alarm_ring_swipe_to_turn_off),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AlarmRingAccent,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 56.dp)
                .alpha(textAlpha),
        )

        // Draggable handle
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.roundToInt(), 0) }
                .size(56.dp)
                .align(Alignment.CenterStart)
                .padding(4.dp)
                .clip(CircleShape)
                .background(AlarmRingSwipeHandle)
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Horizontal,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.alarm_ring_swipe_to_turn_off),
                tint = AlarmRingOnBackground,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
