package itmo.alk.womplist.feature.sdui.ui

sealed class UiNode {

    data class Column(
        val children: List<UiNode>,
        override val weight: Float? = null
    ) : UiNode()

    data class Header(
        val title: String,
        val showSecret: Boolean,
        override val weight: Float? = null
    ) : UiNode()

    data class Search(
        val hint: String,
        override val weight: Float? = null
    ) : UiNode()

    data class AnimeList(
        override val weight: Float? = null
    ) : UiNode()

    abstract val weight: Float?
}