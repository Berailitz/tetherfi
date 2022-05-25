package com.pyamsoft.tetherfi.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.statusBarsHeight
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.tetherfi.R

@JvmOverloads
@Composable
internal fun MainTopBar(
    modifier: Modifier = Modifier,
    currentTab: MainView,
    onTabUpdated: (MainView) -> Unit,
    onSettingsOpen: () -> Unit,
) {
  val tabs = remember {
    listOf(
        MainView.Status,
        MainView.Activity,
        MainView.Errors,
    )
  }
  val index =
      remember(tabs, currentTab) {
        val idx = tabs.indexOfFirst { it == currentTab }
        return@remember if (idx < 0) 0 else idx
      }

  Surface(
      modifier = modifier,
      color = MaterialTheme.colors.background,
      elevation = ZeroElevation,
  ) {
    Surface(
        contentColor = Color.White,
        color = MaterialTheme.colors.primary,
        shape =
            MaterialTheme.shapes.medium.copy(
                topStart = ZeroCornerSize,
                topEnd = ZeroCornerSize,
            ),
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
      Column {
        Spacer(
            modifier = Modifier.statusBarsHeight(),
        )
        TopAppBar(
            elevation = ZeroElevation,
            backgroundColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            title = {
              Text(
                  text = stringResource(R.string.app_name),
              )
            },
            actions = {
              IconButton(
                  onClick = onSettingsOpen,
              ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Open Settings",
                )
              }
            })
        TabRow(
            modifier = modifier,
            backgroundColor = Color.Transparent,
            selectedTabIndex = index,
        ) {
          tabs.forEach { tab ->
            ScreenTab(
                current = currentTab,
                tab = tab,
                onTabUpdated = onTabUpdated,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ScreenTab(
    modifier: Modifier = Modifier,
    tab: MainView,
    current: MainView,
    onTabUpdated: (MainView) -> Unit,
) {
  Tab(
      modifier = modifier,
      selected = tab == current,
      onClick = { onTabUpdated(tab) },
  ) {
    Text(
        modifier = Modifier.padding(vertical = MaterialTheme.keylines.typography),
        text = tab.display,
    )
  }
}