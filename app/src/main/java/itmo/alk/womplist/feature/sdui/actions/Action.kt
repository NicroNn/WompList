package itmo.alk.womplist.feature.sdui.actions

sealed class Action {
    data class Navigate(val route: String) : Action()
}
