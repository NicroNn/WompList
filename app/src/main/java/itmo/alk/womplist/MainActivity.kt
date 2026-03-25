package itmo.alk.womplist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.LocaleListCompat
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import itmo.alk.womplist.core.navigation.AppNavHost
import itmo.alk.womplist.core.ui.theme.WompListTheme
import itmo.alk.womplist.domain.anime.AnimeRepository
import itmo.alk.womplist.data.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private var localeSwitchInProgress: Boolean = false
    }

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var animeRepository: AnimeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (localeSwitchInProgress) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            localeSwitchInProgress = false
        }

        lifecycleScope.launch {
            animeRepository.loadMoreAnime(page = 1)
        }

        lifecycleScope.launch {
            userPreferencesRepository.languageFlow
                .distinctUntilChanged()
                .collect { languageCode ->
                    val targetLocales = LocaleListCompat.forLanguageTags(languageCode)
                    if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != targetLocales.toLanguageTags()) {
                        localeSwitchInProgress = true
                        AppCompatDelegate.setApplicationLocales(targetLocales)
                    }
                }
        }

        setContent {
            val darkTheme by userPreferencesRepository.darkThemeFlow.collectAsState(initial = false)

            WompListTheme(
                darkTheme = darkTheme,
                dynamicColor = true
            ) {
                AppNavHost()
            }
        }
    }
}