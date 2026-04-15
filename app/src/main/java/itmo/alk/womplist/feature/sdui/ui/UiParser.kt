package itmo.alk.womplist.feature.sdui.ui

import kotlinx.serialization.json.*

class UiParser {
    fun parse(dto: UiDto): UiNode {
        return when (dto.type) {

            "column" -> UiNode.Column(
                dto.children?.map { parse(it) } ?: emptyList()
            )

            "header" -> UiNode.Header(
                title = dto.props?.get("title")?.jsonPrimitive?.content ?: "",
                showSecret = dto.props?.get("showSecret")?.jsonPrimitive?.boolean ?: false
            )

            "search" -> UiNode.Search(
                hint = dto.props?.get("hint")?.jsonPrimitive?.content ?: ""
            )

            "anime_list" -> UiNode.AnimeList

            else -> error("Unknown type: ${dto.type}")
        }
    }
}