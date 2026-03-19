package itmo.alk.womplist.data

import androidx.compose.runtime.compositionLocalOf
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.UserPreferencesRepository

val LocalUserPreferencesRepository = compositionLocalOf<UserPreferencesRepository> {
    error("No UserPreferencesRepository provided")
}

val LocalAnimeRepository = compositionLocalOf<AnimeRepository> {
    error("No AnimeRepository provided")
}