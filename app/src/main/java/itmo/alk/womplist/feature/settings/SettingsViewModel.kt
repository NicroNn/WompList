package itmo.alk.womplist.feature.settings

import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.core.mvi.MviViewModel
import itmo.alk.womplist.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>(SettingsState()) {

    private var observeJob: Job? = null

    init {
        observeSettings()
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkTheme -> {
                userPreferencesRepository.saveDarkTheme(intent.enabled)
            }

            is SettingsIntent.SetLanguage -> {
                userPreferencesRepository.saveLanguage(intent.languageCode)
            }
        }
    }

    private fun observeSettings() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            launch {
                userPreferencesRepository.darkThemeFlow.collect { value ->
                    setState { it.copy(darkTheme = value) }
                }
            }
            launch {
                userPreferencesRepository.languageFlow.collect { value ->
                    setState { it.copy(language = value) }
                }
            }
        }
    }
}