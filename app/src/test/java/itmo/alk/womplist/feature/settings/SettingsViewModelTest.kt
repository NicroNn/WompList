package itmo.alk.womplist.feature.settings

import itmo.alk.womplist.core.error.AppError
import itmo.alk.womplist.domain.settings.SettingsPreferencesGateway
import itmo.alk.womplist.domain.settings.usecase.ObserveDarkThemeUseCase
import itmo.alk.womplist.domain.settings.usecase.ObserveLanguageUseCase
import itmo.alk.womplist.domain.settings.usecase.SaveDarkThemeUseCase
import itmo.alk.womplist.domain.settings.usecase.SaveLanguageUseCase
import itmo.alk.womplist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init observes dark theme and language`() = runTest {
        val gateway = FakeSettingsPreferencesGateway(
            darkThemeInitial = true,
            languageInitial = "ru"
        )

        val viewModel = createViewModel(gateway)
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.darkTheme)
        assertEquals("ru", viewModel.state.value.language)
    }

    @Test
    fun `toggle dark theme intent saves and updates state`() = runTest {
        val gateway = FakeSettingsPreferencesGateway()
        val viewModel = createViewModel(gateway)

        viewModel.onIntent(SettingsIntent.ToggleDarkTheme(true))
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.darkTheme)
    }

    @Test
    fun `set language intent saves and updates state`() = runTest {
        val gateway = FakeSettingsPreferencesGateway()
        val viewModel = createViewModel(gateway)

        viewModel.onIntent(SettingsIntent.SetLanguage("ru"))
        advanceUntilIdle()

        assertEquals("ru", viewModel.state.value.language)
    }

    @Test
    fun `toggle dark theme save error updates error state`() = runTest {
        val gateway = FakeSettingsPreferencesGateway().apply {
            throwOnSaveDarkTheme = IllegalStateException("save failed")
        }
        val viewModel = createViewModel(gateway)

        viewModel.onIntent(SettingsIntent.ToggleDarkTheme(true))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.error is AppError.Unknown)
    }

    private fun createViewModel(gateway: FakeSettingsPreferencesGateway): SettingsViewModel {
        return SettingsViewModel(
            observeDarkTheme = ObserveDarkThemeUseCase(gateway),
            observeLanguage = ObserveLanguageUseCase(gateway),
            saveDarkTheme = SaveDarkThemeUseCase(gateway),
            saveLanguage = SaveLanguageUseCase(gateway)
        )
    }

    private class FakeSettingsPreferencesGateway(
        darkThemeInitial: Boolean = false,
        languageInitial: String = "en"
    ) : SettingsPreferencesGateway {
        var throwOnSaveDarkTheme: Throwable? = null
        var throwOnSaveLanguage: Throwable? = null
        private val darkThemeState = MutableStateFlow(darkThemeInitial)
        private val languageState = MutableStateFlow(languageInitial)

        override val darkThemeFlow = darkThemeState
        override val languageFlow = languageState

        override suspend fun saveDarkTheme(enabled: Boolean) {
            throwOnSaveDarkTheme?.let { throw it }
            darkThemeState.value = enabled
        }

        override suspend fun saveLanguage(languageCode: String) {
            throwOnSaveLanguage?.let { throw it }
            languageState.value = languageCode
        }
    }
}

