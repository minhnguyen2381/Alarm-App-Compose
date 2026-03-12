package com.nguyennhatminh614.alarmappcompose.ui.details

import com.nguyennhatminh614.alarmappcompose.domain.Details
import com.nguyennhatminh614.alarmappcompose.util.formatDate

data class DetailsUiState(
    val detail: Details = Details(),
    val offline: Boolean = false
) {
    val formattedUserSince = formatDate(detail.userSince)
}