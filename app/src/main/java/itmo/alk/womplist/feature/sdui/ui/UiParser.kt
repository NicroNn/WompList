package itmo.alk.womplist.feature.sdui.ui

import kotlinx.serialization.json.*

class UiParser {
    fun parse(dto: UiDto): UiNode {
        return when (dto.type) {

            "column" -> UiNode.Column(
                children = dto.children?.map { parse(it) } ?: emptyList(),
                weight = dto.weight()
            )

            "header" -> UiNode.Header(
                title = dto.props?.get("title")?.jsonPrimitive?.content ?: "",
                showSecret = dto.props?.get("showSecret")?.jsonPrimitive?.boolean ?: false,
                weight = dto.weight()
            )

            "search" -> UiNode.Search(
                hint = dto.props?.get("hint")?.jsonPrimitive?.content ?: "",
                weight = dto.weight()
            )

            "anime_list" -> UiNode.AnimeList(
                weight = dto.weight()
            )

            else -> error("Unknown type: ${dto.type}")
        }
    }

    private fun UiDto.weight(): Float? {
        return weight ?: (props?.get("weight") as? JsonPrimitive)?.floatOrNull
    }
}