package itmo.alk.womplist.core.model

import itmo.alk.womplist.R

data class Anime(
    val id: Long,
    val name: String,
    val russianName: String?,
    val posterUrl: String,
    val descriptionHtml: String,
    val episodes: Int,
    val episodesAired: Int,
    val status: String,
    val genres: List<String>,
    val score: Double,
    val year: Int?
)