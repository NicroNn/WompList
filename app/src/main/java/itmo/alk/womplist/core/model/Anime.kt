package itmo.alk.womplist.core.model

import itmo.alk.womplist.R

data class Anime(
    val id: Long,
    val title: String,
    val posterResId: Int = R.drawable.womp,
    val description: String = "",
    val episodes: Int = 0,
    val status: String = "",
    val genres: List<String> = emptyList(),
    val episodesList: List<String> = emptyList(),
    val rating: Double = 0.0,
    val year: Int = 0
)