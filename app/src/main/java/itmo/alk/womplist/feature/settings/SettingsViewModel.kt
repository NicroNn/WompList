package itmo.alk.womplist.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import itmo.alk.womplist.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val darkTheme: StateFlow<Boolean> = userPreferencesRepository.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val language: StateFlow<String> = userPreferencesRepository.languageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveDarkTheme(enabled)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLanguage(languageCode)
        }
    }
}