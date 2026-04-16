package itmo.alk.womplist.feature.sdui.ui

import kotlinx.serialization.json.*

class UiParser {
    fun parse(dto: UiDto): UiNode {
        return when (dto.type) {

            "column" -> UiNode.Column(
                children = dto.children?.map { parse(it) } ?: emptyList(),
                style = dto.styleRef(),
                weight = dto.weight()
            )

            "header" -> UiNode.Header(
                title = dto.props?.get("title")?.jsonPrimitive?.content ?: "",
                showSecret = dto.props?.get("showSecret")?.jsonPrimitive?.boolean ?: false,
                style = dto.styleRef(),
                weight = dto.weight()
            )

            "search" -> UiNode.Search(
                hint = dto.props?.get("hint")?.jsonPrimitive?.content ?: "",
                style = dto.styleRef(),
                weight = dto.weight()
            )

            "anime_list" -> UiNode.AnimeList(
                style = dto.styleRef(),
                weight = dto.weight()
            )

            else -> error("Unknown type: ${dto.type}")
        }
    }

    private fun UiDto.weight(): Float? {
        return weight ?: (props?.get("weight") as? JsonPrimitive)?.floatOrNull
    }

    private fun UiDto.styleRef(): UiStyleRef {
        val styleObject = style ?: (props?.get("style") as? JsonObject)
        return UiStyleRef(
            variant = styleObject?.get("variant")?.jsonPrimitive?.contentOrNull,
            tone = styleObject?.get("tone")?.jsonPrimitive?.contentOrNull,
            density = styleObject?.get("density")?.jsonPrimitive?.contentOrNull,
            emphasis = styleObject?.get("emphasis")?.jsonPrimitive?.contentOrNull
        )
    }
}