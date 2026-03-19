package itmo.alk.womplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import itmo.alk.womplist.core.navigation.AppNavHost
import itmo.alk.womplist.core.ui.theme.WompListTheme
import itmo.alk.womplist.core.ui.utils.updateLocale
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.LocalUserPreferencesRepository
import itmo.alk.womplist.data.local.database.AppDatabase
import itmo.alk.womplist.data.local.repository.LocalAnimeStatusRepository
import itmo.alk.womplist.data.network.apollo.ApolloClientProvider
import itmo.alk.womplist.data.network.repository.NetworkAnimeRepository
import itmo.alk.womplist.data.repository.AnimeRepositoryImpl
import itmo.alk.womplist.data.repository.UserPreferencesRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val userPreferencesRepository by lazy { UserPreferencesRepository(applicationContext) }
    private val database by lazy { AppDatabase.getInstance(applicationContext) }
    private val localStatusRepo by lazy { LocalAnimeStatusRepository(database) }
    private val apolloClient by lazy { ApolloClientProvider.getClient(accessToken = null) } // токен пока не нужен
    private val networkRepo by lazy { NetworkAnimeRepository(apolloClient) }
    private val animeRepository by lazy { AnimeRepositoryImpl(networkRepo, localStatusRepo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            networkRepo.loadAnimeList(page = 1, limit = 30)
        }

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