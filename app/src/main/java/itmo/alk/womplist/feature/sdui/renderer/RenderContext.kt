package itmo.alk.womplist.feature.sdui.renderer

import androidx.navigation.NavController
import itmo.alk.womplist.feature.home.HomeState
import itmo.alk.womplist.feature.sdui.actions.ActionHandler

data class RenderContext(
    val navController: NavController,
    val state: HomeState,
    val actionHandler: ActionHandler
)
