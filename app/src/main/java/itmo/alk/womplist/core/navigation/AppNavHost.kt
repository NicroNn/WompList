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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import itmo.alk.womplist.R
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.LocalUserPreferencesRepository
import itmo.alk.womplist.core.ui.components.BouncingEasterEggOverlay
import itmo.alk.womplist.core.ui.components.BottomBar
import itmo.alk.womplist.core.ui.components.EasterEggConfig
import itmo.alk.womplist.core.ui.components.ScreenCornerType
import itmo.alk.womplist.feature.home.HomeScreen
import itmo.alk.womplist.feature.home.HomeEffect
import itmo.alk.womplist.feature.home.HomeViewModel
import itmo.alk.womplist.feature.home.HomeViewModelFactory
import itmo.alk.womplist.feature.mylist.MyListScreen
import itmo.alk.womplist.feature.mylist.MyListEffect
import itmo.alk.womplist.feature.mylist.MyListViewModel
import itmo.alk.womplist.feature.mylist.MyListViewModelFactory
import itmo.alk.womplist.feature.profile.ProfileScreen
import itmo.alk.womplist.feature.profile.ProfileEffect
import itmo.alk.womplist.feature.profile.ProfileViewModel
import itmo.alk.womplist.feature.profile.ProfileViewModelFactory
import itmo.alk.womplist.feature.settings.SettingsScreen
import itmo.alk.womplist.feature.settings.SettingsViewModel
import itmo.alk.womplist.feature.settings.SettingsViewModelFactory
import itmo.alk.womplist.feature.title.TitleScreen
import itmo.alk.womplist.feature.title.TitleEffect
import itmo.alk.womplist.feature.title.TitleViewModel
import itmo.alk.womplist.feature.title.TitleViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val repository = LocalUserPreferencesRepository.current
    val animeRepository = LocalAnimeRepository.current

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(repository)
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(animeRepository)
    )
    val myListViewModel: MyListViewModel = viewModel(
        factory = MyListViewModelFactory(animeRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(animeRepository)
    )

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var overlayTriggerKey by remember { mutableIntStateOf(0) }
    var overlayScreenType by remember { mutableStateOf(ScreenCornerType.HOME) }

    val triggerSecretOverlay: (ScreenCornerType) -> Unit = { type ->
        overlayScreenType = type
        overlayTriggerKey += 1
    }

    CollectHomeEffects(
        viewModel = homeViewModel,
        navController = navController,
        onSecret = { triggerSecretOverlay(ScreenCornerType.HOME) }
    )
    CollectMyListEffects(
        viewModel = myListViewModel,
        navController = navController,
        onSecret = { triggerSecretOverlay(ScreenCornerType.MY_LIST) }
    )
    CollectProfileEffects(
        viewModel = profileViewModel,
        navController = navController,
        onSecret = { triggerSecretOverlay(ScreenCornerType.PROFILE) }
    )

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
                        NavHost(
                            navController = navController,
                            startDestination = Routes.HOME,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(Routes.HOME) {
                                HomeScreen(
                                    viewModel = homeViewModel
                                )
                            }
                            composable(Routes.MY_LIST) {
                                MyListScreen(
                                    viewModel = myListViewModel
                                )
                            }
                            composable(Routes.PROFILE) {
                                ProfileScreen(
                                    viewModel = profileViewModel
                                )
                            }
                            composable(Routes.SETTINGS) { SettingsScreen(settingsViewModel) }
                            composable(Routes.TITLE) { backStackEntry ->
                                val titleId = backStackEntry.arguments?.getString("id")?.toLong() ?: 0L
                                val titleViewModel: TitleViewModel = viewModel(
                                    key = "title-$titleId",
                                    factory = TitleViewModelFactory(animeRepository)
                                )
                                CollectTitleEffects(viewModel = titleViewModel, navController = navController)
                                TitleScreen(
                                    viewModel = titleViewModel,
                                    titleId = titleId
                                )
                            }
                        }
                    }
                    VerticalNavigationRail(navController)
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                viewModel = homeViewModel
                            )
                        }
                        composable(Routes.MY_LIST) {
                            MyListScreen(
                                viewModel = myListViewModel
                            )
                        }
                        composable(Routes.PROFILE) {
                            ProfileScreen(
                                viewModel = profileViewModel
                            )
                        }
                        composable(Routes.SETTINGS) { SettingsScreen(settingsViewModel) }
                        composable(Routes.TITLE) { backStackEntry ->
                            val titleId = backStackEntry.arguments?.getString("id")?.toLong() ?: 0L
                            val titleViewModel: TitleViewModel = viewModel(
                                key = "title-$titleId",
                                factory = TitleViewModelFactory(animeRepository)
                            )
                            CollectTitleEffects(viewModel = titleViewModel, navController = navController)
                            TitleScreen(
                                viewModel = titleViewModel,
                                titleId = titleId
                            )
                        }
                    }
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
private fun CollectHomeEffects(
    viewModel: HomeViewModel,
    navController: NavController,
    onSecret: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToTitle -> navController.navigate(Routes.title(effect.id))
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
                is MyListEffect.NavigateToTitle -> navController.navigate(Routes.title(effect.id))
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
                is ProfileEffect.NavigateToTitle -> navController.navigate(Routes.title(effect.id))
                ProfileEffect.NavigateToSettings -> navController.navigate(Routes.SETTINGS)
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
                is TitleEffect.NavigateToTitle -> navController.navigate(Routes.title(effect.id))
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
                onClick = { navController.navigate(screen) },
                alwaysShowLabel = false
            )
        }
    }
}