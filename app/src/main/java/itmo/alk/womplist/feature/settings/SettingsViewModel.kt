package itmo.alk.womplist.feature.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import itmo.alk.womplist.core.error.toAppError
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.domain.settings.usecase.ObserveDarkThemeUseCase
import itmo.alk.womplist.domain.settings.usecase.ObserveLanguageUseCase
import itmo.alk.womplist.domain.settings.usecase.SaveDarkThemeUseCase
import itmo.alk.womplist.domain.settings.usecase.SaveLanguageUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeDarkTheme: ObserveDarkThemeUseCase,
    private val observeLanguage: ObserveLanguageUseCase,
    private val saveDarkTheme: SaveDarkThemeUseCase,
    private val saveLanguage: SaveLanguageUseCase
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>(SettingsState()) {

    private var observeJob: Job? = null

    init {
        observeSettings()
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkTheme -> {
                try {
                    setState { it.copy(error = null) }
                    saveDarkTheme(intent.enabled)
                } catch (throwable: Throwable) {
                    setState { it.copy(error = throwable.toAppError()) }
                }
            }

            is SettingsIntent.SetLanguage -> {
                try {
                    setState { it.copy(error = null) }
                    saveLanguage(intent.languageCode)
                } catch (throwable: Throwable) {
                    setState { it.copy(error = throwable.toAppError()) }
                }
            }

            SettingsIntent.Retry -> retry()
        }
    }

    private fun retry() {
        setState { it.copy(error = null) }
        observeJob?.cancel()
        observeJob = null
        observeSettings()
    }

    private fun observeSettings() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            launch {
                observeDarkTheme()
                    .catch { throwable ->
                        setState { it.copy(error = throwable.toAppError()) }
                    }
                    .collect { value ->
                        setState { it.copy(darkTheme = value) }
                    }
            }
            launch {
                observeLanguage()
                    .catch { throwable ->
                        setState { it.copy(error = throwable.toAppError()) }
                    }
                    .collect { value ->
                        setState { it.copy(language = value) }
                    }
            }
        }
    }
}