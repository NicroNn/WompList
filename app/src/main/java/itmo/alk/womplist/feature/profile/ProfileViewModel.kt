package itmo.alk.womplist.feature.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import itmo.alk.womplist.core.error.toAppError
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.domain.profile.usecase.ObserveProfileCompletedListUseCase
import itmo.alk.womplist.domain.profile.usecase.ObserveProfilePlannedListUseCase
import itmo.alk.womplist.domain.profile.usecase.ObserveProfileWatchingListUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeWatchingList: ObserveProfileWatchingListUseCase,
    private val observePlannedList: ObserveProfilePlannedListUseCase,
    private val observeCompletedList: ObserveProfileCompletedListUseCase
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    private var observeJob: Job? = null

    init {
        observeProfileData()
    }

    override suspend fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OpenTitle -> sendEffect(ProfileEffect.NavigateToTitle(intent.id))
            ProfileIntent.OpenSettings -> {
                setState { it.copy(error = null) }
                sendEffect(ProfileEffect.NavigateToSettings)
            }
            ProfileIntent.TriggerSecret -> sendEffect(ProfileEffect.TriggerSecretOverlay)
            ProfileIntent.Retry -> retry()
        }
    }

    private fun retry() {
        setState { it.copy(error = null) }
        observeJob?.cancel()
        observeJob = null
        observeProfileData()
    }

    private fun observeProfileData() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            launch {
                observeWatchingList()
                    .catch { throwable ->
                        setState { it.copy(error = throwable.toAppError()) }
                    }
                    .collect { list ->
                        setState { it.copy(watchingList = list) }
                    }
            }
            launch {
                observePlannedList()
                    .catch { throwable ->
                        setState { it.copy(error = throwable.toAppError()) }
                    }
                    .collect { list ->
                        setState { it.copy(plannedList = list) }
                    }
            }
            launch {
                observeCompletedList()
                    .catch { throwable ->
                        setState { it.copy(error = throwable.toAppError()) }
                    }
                    .collect { list ->
                        setState { it.copy(completedList = list) }
                    }
            }
        }
    }
}



