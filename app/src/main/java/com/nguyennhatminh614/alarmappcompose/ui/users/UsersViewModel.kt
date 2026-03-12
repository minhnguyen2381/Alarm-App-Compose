package com.nguyennhatminh614.alarmappcompose.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val isOfflineState = MutableStateFlow(false)

    val uiState: StateFlow<UsersUiState> = combine(
        usersRepository.users,
        isOfflineState
    ) { list, offlineError ->
        val offline = list.isNullOrEmpty() && offlineError
        UsersUiState(
            list = list ?: emptyList(),
            offline = offline
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UsersUiState()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isOfflineState.update { false }
                usersRepository.refreshUsers()
            } catch (e: Exception) {
                Timber.w(e)
                isOfflineState.update { true }
            }
        }
    }

}