package com.nguyennhatminh614.alarmappcompose.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.Details
import com.nguyennhatminh614.alarmappcompose.repository.DetailsRepository
import com.nguyennhatminh614.alarmappcompose.ui.Argument
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
class DetailsViewModel @Inject constructor(
    private val detailsRepository: DetailsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val username: String? = savedStateHandle[Argument.USERNAME]
    private val isOfflineState = MutableStateFlow(false)

    val uiState: StateFlow<DetailsUiState> = combine(
        detailsRepository.getUserDetails(username ?: ""),
        isOfflineState
    ) { detail, offlineError ->
        val offline = detail == null && offlineError
        DetailsUiState(
            detail = detail ?: Details(),
            offline = offline
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsUiState()
    )

    init {
        username?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    isOfflineState.update { false }
                    detailsRepository.refreshDetails(it)
                } catch (e: Exception) {
                    Timber.w(e)
                    isOfflineState.update { true }
                }
            }
        }
    }

}