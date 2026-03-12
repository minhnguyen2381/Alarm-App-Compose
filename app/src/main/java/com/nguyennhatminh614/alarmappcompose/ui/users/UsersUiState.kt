package com.nguyennhatminh614.alarmappcompose.ui.users

import com.nguyennhatminh614.alarmappcompose.domain.User

data class UsersUiState(
    val list: List<User> = listOf(),
    val offline: Boolean = false
)