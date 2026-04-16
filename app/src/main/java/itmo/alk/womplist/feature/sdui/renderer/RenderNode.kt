package itmo.alk.womplist.feature.sdui.renderer

import androidx.compose.runtime.Composable
import itmo.alk.womplist.feature.sdui.registry.ComponentRegistry
import itmo.alk.womplist.feature.sdui.ui.UiNode

@Composable
fun RenderNode(
    node: UiNode,
    registry: ComponentRegistry,
    ctx: RenderContext
) {
    registry.Render(node, ctx)
}