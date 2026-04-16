package itmo.alk.womplist.feature.sdui.ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UiDto(
    val type: String,
    val props: JsonObject? = null,
    val children: List<UiDto>? = null,
    val weight: Float? = null
)