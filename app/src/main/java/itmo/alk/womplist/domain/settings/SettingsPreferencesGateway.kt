package itmo.alk.womplist.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsPreferencesGateway {
    val darkThemeFlow: Flow<Boolean>
    val languageFlow: Flow<String>

    suspend fun saveDarkTheme(enabled: Boolean)
    suspend fun saveLanguage(languageCode: String)
}

