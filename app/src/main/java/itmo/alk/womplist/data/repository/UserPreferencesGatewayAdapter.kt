package itmo.alk.womplist.data.repository

import itmo.alk.womplist.domain.settings.SettingsPreferencesGateway
import javax.inject.Inject

class UserPreferencesGatewayAdapter @Inject constructor(
    private val repository: UserPreferencesRepository
) : SettingsPreferencesGateway {

    override val darkThemeFlow = repository.darkThemeFlow
    override val languageFlow = repository.languageFlow

    override suspend fun saveDarkTheme(enabled: Boolean) {
        repository.saveDarkTheme(enabled)
    }

    override suspend fun saveLanguage(languageCode: String) {
        repository.saveLanguage(languageCode)
    }
}

