package itmo.alk.womplist.core.ui.utils

import itmo.alk.womplist.core.model.Anime

fun preferredAnimeTitle(anime: Anime, languageCode: String): String {
    val isRussian = languageCode.startsWith("ru")
    return if (isRussian) {
        anime.russianName ?: anime.name
    } else {
        anime.name.ifBlank { anime.russianName ?: anime.name }
    }
}

