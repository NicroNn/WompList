package itmo.alk.womplist.domain.settings.usecase

import itmo.alk.womplist.domain.settings.SettingsPreferencesGateway
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveDarkThemeUseCase @Inject constructor(
    private val gateway: SettingsPreferencesGateway
) {
    operator fun invoke(): Flow<Boolean> {
        return gateway.darkThemeFlow
    }
}

class ObserveLanguageUseCase @Inject constructor(
    private val gateway: SettingsPreferencesGateway
) {
    operator fun invoke(): Flow<String> {
        return gateway.languageFlow
    }
}

class SaveDarkThemeUseCase @Inject constructor(
    private val gateway: SettingsPreferencesGateway
) {
    suspend operator fun invoke(enabled: Boolean) {
        gateway.saveDarkTheme(enabled)
    }
}

class SaveLanguageUseCase @Inject constructor(
    private val gateway: SettingsPreferencesGateway
) {
    suspend operator fun invoke(languageCode: String) {
        gateway.saveLanguage(languageCode)
    }
}

