package itmo.alk.womplist.feature.sdui.ui

sealed class UiNode {

    data class Column(
        val children: List<UiNode>
    ) : UiNode()

    data class Header(
        val title: String,
        val showSecret: Boolean
    ) : UiNode()

    data class Search(
        val hint: String
    ) : UiNode()

    object AnimeList : UiNode()
}