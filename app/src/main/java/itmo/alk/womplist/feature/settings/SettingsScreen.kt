package itmo.alk.womplist.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import itmo.alk.womplist.R
import itmo.alk.womplist.core.ui.components.ErrorBanner
import itmo.alk.womplist.core.ui.components.SettingsRadioGroup
import itmo.alk.womplist.core.ui.components.SettingsSection
import itmo.alk.womplist.core.ui.components.SettingsSwitchItem

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        state.error?.let { error ->
            item {
                ErrorBanner(
                    error = error,
                    onRetry = { viewModel.onIntent(SettingsIntent.Retry) }
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.appearance)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.dark_theme),
                    checked = state.darkTheme,
                    onCheckedChange = { viewModel.onIntent(SettingsIntent.ToggleDarkTheme(it)) }
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.language)) {
                SettingsRadioGroup(
                    options = listOf(
                        "en" to stringResource(R.string.english),
                        "ru" to stringResource(R.string.russian)
                    ),
                    selectedOption = state.language,
                    onOptionSelected = { viewModel.onIntent(SettingsIntent.SetLanguage(it)) }
                )
            }
        }
    }
}