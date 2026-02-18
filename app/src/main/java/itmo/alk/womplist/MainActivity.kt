package itmo.alk.womplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import itmo.alk.womplist.core.navigation.AppNavHost
import itmo.alk.womplist.data.LocalUserPreferencesRepository
import itmo.alk.womplist.core.ui.theme.WompListTheme
import itmo.alk.womplist.core.ui.utils.updateLocale
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.UserPreferencesRepository

class MainActivity : ComponentActivity() {
    private val userPreferencesRepository by lazy { UserPreferencesRepository(applicationContext) }
    private val animeRepository by lazy { AnimeRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme by userPreferencesRepository.darkThemeFlow.collectAsState(initial = false)
            val language by userPreferencesRepository.languageFlow.collectAsState(initial = "en")

            val originalContext = LocalContext.current
            val localizedContext = remember(language) {
                originalContext.updateLocale(language)
            }

            CompositionLocalProvider(
                LocalUserPreferencesRepository provides userPreferencesRepository,
                LocalAnimeRepository provides animeRepository
            ) {
                CompositionLocalProvider(
                    LocalContext provides localizedContext
                ) {
                    WompListTheme(
                        darkTheme = darkTheme,
                        dynamicColor = true
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}