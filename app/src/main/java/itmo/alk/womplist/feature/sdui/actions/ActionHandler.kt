package itmo.alk.womplist.feature.sdui.actions

import androidx.navigation.NavController

class ActionHandler(
    private val navController: NavController
) {
    fun handle(action: Action) {
        when (action) {
            is Action.Navigate -> navController.navigate(action.route)
        }
    }
}