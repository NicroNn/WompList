package itmo.alk.womplist.core.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import itmo.alk.womplist.R
import itmo.alk.womplist.core.ui.components.BouncingEasterEggOverlay
import itmo.alk.womplist.core.ui.components.BottomBar
import itmo.alk.womplist.core.ui.components.EasterEggConfig
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.feature.home.HomeScreen
import itmo.alk.womplist.feature.home.HomeEffect
import itmo.alk.womplist.feature.home.HomeViewModel
import itmo.alk.womplist.feature.mylist.MyListScreen
import itmo.alk.womplist.feature.mylist.MyListEffect
import itmo.alk.womplist.feature.mylist.MyListViewModel
import itmo.alk.womplist.feature.profile.ProfileScreen
import itmo.alk.womplist.feature.profile.ProfileEffect
import itmo.alk.womplist.feature.profile.ProfileViewModel
import itmo.alk.womplist.feature.settings.SettingsScreen
import itmo.alk.womplist.feature.settings.SettingsViewModel
import itmo.alk.womplist.feature.title.TitleScreen
import itmo.alk.womplist.feature.title.TitleEffect
import itmo.alk.womplist.feature.title.TitleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var overlayTriggerKey by remember { mutableIntStateOf(0) }
    var overlayScreenType by remember { mutableStateOf(ScreenCornerType.HOME) }

    val triggerSecretOverlay: (ScreenCornerType) -> Unit = { type ->
        overlayScreenType = type
        overlayTriggerKey += 1
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppGraph(
                            navController = navController,
                            modifier = Modifier.fillMaxSize(),
                            onSecret = triggerSecretOverlay
                        )
                    }
                    VerticalNavigationRail(navController)
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    AppGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        onSecret = triggerSecretOverlay
                    )
                }
            }

            BouncingEasterEggOverlay(
                triggerKey = overlayTriggerKey,
                assetSpec = EasterEggConfig.assetFor(overlayScreenType),
                onFinished = { overlayTriggerKey = 0 }
            )
        }
    }
}

@Composable
private fun AppGraph(
    navController: NavHostController,
    modifier: Modifier,
    onSecret: (ScreenCornerType) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            CollectHomeEffects(
                viewModel = homeViewModel,
                navController = navController,
                onSecret = { onSecret(ScreenCornerType.HOME) }
            )
            HomeScreen(viewModel = homeViewModel)
        }
        composable(Routes.MY_LIST) {
            val myListViewModel: MyListViewModel = hiltViewModel()
            CollectMyListEffects(
                viewModel = myListViewModel,
                navController = navController,
                onSecret = { onSecret(ScreenCornerType.MY_LIST) }
            )
            MyListScreen(viewModel = myListViewModel)
        }
        composable(Routes.PROFILE) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            CollectProfileEffects(
                viewModel = profileViewModel,
                navController = navController,
                onSecret = { onSecret(ScreenCornerType.PROFILE) }
            )
            ProfileScreen(viewModel = profileViewModel)
        }
        composable(Routes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(settingsViewModel)
        }
        composable(Routes.TITLE) { backStackEntry ->
            val titleId = backStackEntry.arguments?.getString("id")?.toLong() ?: 0L
            val titleViewModel: TitleViewModel = hiltViewModel(backStackEntry)
            CollectTitleEffects(viewModel = titleViewModel, navController = navController)
            TitleScreen(
                viewModel = titleViewModel,
                titleId = titleId
            )
        }
    }
}

private fun NavController.navigateTopLevel(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

private fun NavController.navigateToTitle(id: Long) {
    navigate(Routes.title(id)) {
        launchSingleTop = true
    }
}

@Composable
private fun CollectHomeEffects(
    viewModel: HomeViewModel,
    navController: NavController,
    onSecret: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTitle -> navController.navigateToTitle(effect.id)
                HomeEffect.TriggerSecretOverlay -> onSecret()
            }
        }
    }
}

@Composable
private fun CollectMyListEffects(
    viewModel: MyListViewModel,
    navController: NavController,
    onSecret: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MyListEffect.NavigateToTitle -> navController.navigateToTitle(effect.id)
                MyListEffect.TriggerSecretOverlay -> onSecret()
            }
        }
    }
}

@Composable
private fun CollectProfileEffects(
    viewModel: ProfileViewModel,
    navController: NavController,
    onSecret: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToTitle -> navController.navigateToTitle(effect.id)
                ProfileEffect.NavigateToSettings -> navController.navigateTopLevel(Routes.SETTINGS)
                ProfileEffect.TriggerSecretOverlay -> onSecret()
            }
        }
    }
}

@Composable
private fun CollectTitleEffects(
    viewModel: TitleViewModel,
    navController: NavController
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TitleEffect.NavigateToTitle -> navController.navigateToTitle(effect.id)
            }
        }
    }
}

@Composable
fun VerticalNavigationRail(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        header = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.padding(8.dp)
            )
        }
    ) {
        val items = listOf(Routes.HOME, Routes.MY_LIST, Routes.PROFILE)
        items.forEach { screen ->
            val isSelected = backStackEntry.value?.destination?.route == screen
            val color = if (isSelected) Color.Blue else Color.Gray
            NavigationRailItem(
                icon = {
                    val icon = when (screen) {
                        Routes.HOME -> Icons.Default.Home
                        Routes.MY_LIST -> Icons.AutoMirrored.Filled.List
                        Routes.PROFILE -> Icons.Default.Person
                        else -> Icons.Default.Home
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = color)
                },
                selected = isSelected,
                onClick = { navController.navigateTopLevel(screen) },
                alwaysShowLabel = false
            )
        }
    }
}