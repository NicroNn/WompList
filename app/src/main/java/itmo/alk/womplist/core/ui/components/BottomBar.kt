package itmo.alk.womplist.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import itmo.alk.womplist.core.navigation.Routes

@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        val items = listOf(Routes.HOME, Routes.MY_LIST, Routes.PROFILE)

        items.forEach { screen ->
            val isSelected = backStackEntry.value?.destination?.route == screen
            val color = if (isSelected) Color.Blue else Color.Gray

            IconButton(
                onClick = {
                    navController.navigate(screen) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                val icon = when (screen) {
                    Routes.HOME -> Icons.Default.Home
                    Routes.MY_LIST -> Icons.AutoMirrored.Filled.List
                    Routes.PROFILE -> Icons.Default.Person
                    else -> Icons.Default.Home
                }
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
        }
    }
}


