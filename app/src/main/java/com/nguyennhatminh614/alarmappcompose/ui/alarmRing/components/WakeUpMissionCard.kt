package com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.domain.model.WakeUpMission
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingAccent
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingAccentDim
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnSurface
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingSurface

@Composable
fun WakeUpMissionCard(
    mission: WakeUpMission,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
        label = "mission_progress",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AlarmRingSurface)
            .padding(20.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Mission icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AlarmRingAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(getMissionIcon(mission)),
                        contentDescription = null,
                        tint = AlarmRingAccent,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title + subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getMissionTitle(mission),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlarmRingOnBackground,
                    )
                    Text(
                        text = getMissionSubtitle(mission, totalCount),
                        fontSize = 13.sp,
                        color = AlarmRingOnSurface,
                    )
                }

                // Counter
                Text(
                    text = stringResource(R.string.alarm_ring_mission_counter, completedCount, totalCount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlarmRingAccent,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(AlarmRingAccentDim.copy(alpha = 0.3f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AlarmRingAccent),
                )
            }
        }
    }
}

private fun getMissionIcon(mission: WakeUpMission): Int = when (mission) {
    WakeUpMission.MATH -> R.drawable.ic_math
    WakeUpMission.SHAKE -> R.drawable.ic_vibration
    WakeUpMission.QR_CODE -> R.drawable.ic_qr_code
    WakeUpMission.NONE -> R.drawable.ic_math
}

@Composable
private fun getMissionTitle(mission: WakeUpMission): String = when (mission) {
    WakeUpMission.MATH -> stringResource(R.string.alarm_ring_math_mission)
    WakeUpMission.SHAKE -> stringResource(R.string.alarm_ring_shake_mission)
    WakeUpMission.QR_CODE -> stringResource(R.string.alarm_ring_qr_code_mission)
    WakeUpMission.NONE -> ""
}

@Composable
private fun getMissionSubtitle(mission: WakeUpMission, totalCount: Int): String = when (mission) {
    WakeUpMission.MATH -> stringResource(R.string.alarm_ring_math_subtitle, totalCount)
    WakeUpMission.SHAKE -> stringResource(R.string.alarm_ring_shake_subtitle, totalCount)
    WakeUpMission.QR_CODE -> stringResource(R.string.alarm_ring_qr_subtitle)
    WakeUpMission.NONE -> ""
}
