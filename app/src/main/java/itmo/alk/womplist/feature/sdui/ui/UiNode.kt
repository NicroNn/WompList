package itmo.alk.womplist.feature.sdui.ui

sealed class UiNode {

    data class Column(
        val children: List<UiNode>,
        override val style: UiStyleRef = UiStyleRef(),
        override val weight: Float? = null
    ) : UiNode()

    data class Header(
        val title: String,
        val showSecret: Boolean,
        override val style: UiStyleRef = UiStyleRef(),
        override val weight: Float? = null
    ) : UiNode()

    data class Search(
        val hint: String,
        override val style: UiStyleRef = UiStyleRef(),
        override val weight: Float? = null
    ) : UiNode()

    data class AnimeList(
        override val style: UiStyleRef = UiStyleRef(),
        override val weight: Float? = null
    ) : UiNode()

    abstract val style: UiStyleRef
    abstract val weight: Float?
}