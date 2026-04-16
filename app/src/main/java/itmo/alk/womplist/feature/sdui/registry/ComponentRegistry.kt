package itmo.alk.womplist.feature.sdui.registry

import androidx.compose.runtime.Composable
import itmo.alk.womplist.feature.sdui.renderer.RenderContext
import itmo.alk.womplist.feature.sdui.ui.UiNode

typealias Renderer = @Composable (UiNode, RenderContext) -> Unit

class ComponentRegistry {

    private val map = mutableMapOf<Class<out UiNode>, Renderer>()

    fun <T : UiNode> register(
        clazz: Class<T>,
        renderer: @Composable (T, RenderContext) -> Unit
    ) {
        map[clazz] = { node, ctx ->
            renderer(node as T, ctx)
        }
    }

    @Composable
    fun Render(node: UiNode, ctx: RenderContext) {
        val renderer = map[node::class.java]
            ?: error("No renderer for ${node::class}")
        renderer(node, ctx)
    }
}
