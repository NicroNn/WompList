package itmo.alk.womplist.feature.profile

import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AnimeRepository
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    private var observeJob: Job? = null

    init {
        observeProfileData()
    }

    override suspend fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OpenTitle -> sendEffect(ProfileEffect.NavigateToTitle(intent.id))
            ProfileIntent.OpenSettings -> sendEffect(ProfileEffect.NavigateToSettings)
            ProfileIntent.TriggerSecret -> sendEffect(ProfileEffect.TriggerSecretOverlay)
        }
    }

    private fun observeProfileData() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            launch {
                repository.watchingList.collect { list ->
                    setState { it.copy(watchingList = list) }
                }
            }
            launch {
                repository.plannedList.collect { list ->
                    setState { it.copy(plannedList = list) }
                }
            }
            launch {
                repository.completedList.collect { list ->
                    setState { it.copy(completedList = list) }
                }
            }
        }
    }
}



