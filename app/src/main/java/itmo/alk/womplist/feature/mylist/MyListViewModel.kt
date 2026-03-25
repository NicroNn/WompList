package itmo.alk.womplist.feature.mylist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import itmo.alk.womplist.core.error.toAppError
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.domain.mylist.usecase.ObserveCompletedListUseCase
import itmo.alk.womplist.domain.mylist.usecase.ObservePlannedListUseCase
import itmo.alk.womplist.domain.mylist.usecase.ObserveWatchingListUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val observeWatchingList: ObserveWatchingListUseCase,
    private val observePlannedList: ObservePlannedListUseCase,
    private val observeCompletedList: ObserveCompletedListUseCase
) : MviViewModel<MyListState, MyListIntent, MyListEffect>(MyListState()) {

    private var observeJob: Job? = null

    init {
        observeLists()
    }

    override suspend fun handleIntent(intent: MyListIntent) {
        when (intent) {
            is MyListIntent.SelectFilter -> {
                setState { it.copy(selectedFilter = intent.filter, isStatusMenuExpanded = false, error = null) }
            }

            is MyListIntent.ToggleStatusMenu -> setState { it.copy(isStatusMenuExpanded = intent.expanded, error = null) }
            is MyListIntent.OpenTitle -> sendEffect(MyListEffect.NavigateToTitle(intent.id))
            MyListIntent.TriggerSecret -> sendEffect(MyListEffect.TriggerSecretOverlay)
            MyListIntent.Retry -> retry()
        }
    }

    private fun retry() {
        setState { it.copy(error = null) }
        observeJob?.cancel()
        observeJob = null
        observeLists()
    }

    private fun observeLists() {
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



