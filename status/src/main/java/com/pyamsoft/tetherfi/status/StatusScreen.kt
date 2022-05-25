package com.pyamsoft.tetherfi.status

import androidx.annotation.CheckResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.tetherfi.server.ServerDefaults
import com.pyamsoft.tetherfi.server.status.RunningStatus

@Composable
fun StatusScreen(
    modifier: Modifier = Modifier,
    appName: String,
    state: StatusViewState,
    onToggle: () -> Unit,
    onSsidChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
) {
  val wiDiStatus = state.wiDiStatus
  val isLoaded = state.preferencesLoaded

  val isButtonEnabled =
      remember(wiDiStatus) {
        wiDiStatus is RunningStatus.Running ||
            wiDiStatus is RunningStatus.NotRunning ||
            wiDiStatus is RunningStatus.Error
      }

  val buttonText =
      remember(wiDiStatus) {
        when (wiDiStatus) {
          is RunningStatus.Error -> "$appName Error"
          is RunningStatus.NotRunning -> "Turn $appName ON"
          is RunningStatus.Running -> "Turn $appName OFF"
          else -> "$appName is thinking..."
        }
      }

  val scaffoldState = rememberScaffoldState()

  val loadedContent =
      prepareLoadedContent(
          appName = appName,
          state = state,
          onSsidChanged = onSsidChanged,
          onPasswordChanged = onPasswordChanged,
          onPortChanged = onPortChanged,
          onOpenBatterySettings = onOpenBatterySettings,
      )

  Scaffold(
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
      item {
        Column(
            modifier =
                Modifier.padding(top = MaterialTheme.keylines.content)
                    .padding(horizontal = MaterialTheme.keylines.content),
        ) {
          Button(
              enabled = isButtonEnabled,
              onClick = onToggle,
          ) {
            Text(
                text = buttonText,
            )
          }
        }
      }

      item {
        Column(
            Modifier.padding(top = MaterialTheme.keylines.content)
                .padding(horizontal = MaterialTheme.keylines.content),
        ) {
          DisplayStatus(
              modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
              title = "Tethering Network Status:",
              status = wiDiStatus,
          )
        }
      }

      if (isLoaded) {
        loadedContent()
      } else {
        item {
          Column(
              modifier =
                  Modifier.padding(top = MaterialTheme.keylines.content)
                      .padding(horizontal = MaterialTheme.keylines.content),
          ) {
            Box(
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
          }
        }
      }
    }
  }
}

@Composable
@CheckResult
private fun prepareLoadedContent(
    appName: String,
    state: StatusViewState,
    onSsidChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
): LazyListScope.() -> Unit {
  val canUseCustomConfig = remember { ServerDefaults.canUseCustomConfig() }
  val isEditable =
      remember(state.wiDiStatus) {
        when (state.wiDiStatus) {
          is RunningStatus.Running, is RunningStatus.Starting, is RunningStatus.Stopping -> false
          else -> true
        }
      }

  val showErrorHintMessage = remember(state.wiDiStatus) { state.wiDiStatus is RunningStatus.Error }

  val group = state.group
  val ssid =
      remember(
          isEditable,
          group,
          state.ssid,
          canUseCustomConfig,
      ) {
        if (isEditable) {
          if (canUseCustomConfig) {
            state.ssid
          } else {
            "SYSTEM DEFINED SSID"
          }
        } else {
          group?.ssid ?: "NO SSID"
        }
      }
  val password =
      remember(
          isEditable,
          group,
          state.password,
          canUseCustomConfig,
      ) {
        if (isEditable) {
          if (canUseCustomConfig) {
            state.password
          } else {
            "SYSTEM DEFINED PASSWORD"
          }
        } else {
          group?.password ?: "NO PASSWORD"
        }
      }

  val ip = remember(state.ip) { state.ip.ifBlank { "NO IP ADDRESS" } }
  val port = remember(state.port) { if (state.port <= 0) "NO PORT" else "${state.port}" }
  val bandName = remember(state.band) { state.band?.name ?: "AUTO" }
  val showInstructions = remember(isEditable) { !isEditable }

  return remember(
      showErrorHintMessage,
      ssid,
      password,
      port,
      ip,
      bandName,
      onSsidChanged,
      onPasswordChanged,
      onPortChanged,
  ) {
    {
      item {
        NetworkInformation(
            modifier = Modifier.padding(MaterialTheme.keylines.content),
            isEditable = isEditable,
            canUseCustomConfig = canUseCustomConfig,
            showErrorHintMessage = showErrorHintMessage,
            ssid = ssid,
            password = password,
            port = port,
            ip = ip,
            bandName = bandName,
            onSsidChanged = onSsidChanged,
            onPasswordChanged = onPasswordChanged,
            onPortChanged = onPortChanged,
        )
      }

      item {
        BatteryInstructions(
            modifier = Modifier.padding(MaterialTheme.keylines.content),
            show = showInstructions,
            appName = appName,
            state = state,
            onOpenBatterySettings = onOpenBatterySettings,
        )
      }

      item {
        ConnectionInstructions(
            modifier = Modifier.padding(MaterialTheme.keylines.content),
            show = showInstructions,
            ssid = ssid,
            password = password,
            port = port,
            ip = ip,
        )
      }
    }
  }
}

@Composable
private fun BatteryInstructions(
    modifier: Modifier = Modifier,
    appName: String,
    show: Boolean,
    state: StatusViewState,
    onOpenBatterySettings: () -> Unit,
) {
  val isIgnored = state.isBatteryOptimizationsIgnored

  val (showing, setShowing) = rememberSaveable { mutableStateOf(false) }

  AnimatedVisibility(
      visible = show,
      modifier = modifier,
  ) {
    Column {
      OutlinedButton(
          onClick = { setShowing(!showing) },
      ) {
        Text(
            text = "How to Improve Performance",
            style = MaterialTheme.typography.h6,
        )
      }

      AnimatedVisibility(
          visible = showing,
          modifier =
              Modifier.padding(top = MaterialTheme.keylines.baseline)
                  .border(
                      width = 1.dp,
                      color = MaterialTheme.colors.onBackground,
                      shape = MaterialTheme.shapes.medium,
                  )
                  .padding(all = MaterialTheme.keylines.content),
      ) {
        Column {
          Text(
              text =
                  "You can disable Android Battery Optimizations to ensure that the $appName proxy server is running at full performance.",
              style = MaterialTheme.typography.body1,
          )

          if (isIgnored) {
            Row(
                modifier = Modifier.padding(top = MaterialTheme.keylines.content),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = "Battery Optimizations Ignored",
                  tint = Color.Green,
              )
              Text(
                  text = "Battery Optimizations Ignored.",
                  style = MaterialTheme.typography.body1,
              )
            }
          } else {
            Button(
                modifier = Modifier.padding(top = MaterialTheme.keylines.content),
                onClick = onOpenBatterySettings,
            ) {
              Text(
                  text = "Open Battery Settings",
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ConnectionInstructions(
    modifier: Modifier = Modifier,
    show: Boolean,
    ssid: String,
    password: String,
    port: String,
    ip: String,
) {
  val (showing, setShowing) = rememberSaveable { mutableStateOf(false) }

  AnimatedVisibility(
      visible = show,
      modifier = modifier,
  ) {
    Column {
      OutlinedButton(
          onClick = { setShowing(!showing) },
      ) {
        Text(
            text = "How to Connect",
            style = MaterialTheme.typography.h6,
        )
      }
      AnimatedVisibility(
          visible = showing,
          modifier =
              Modifier.padding(top = MaterialTheme.keylines.baseline)
                  .border(
                      width = 1.dp,
                      color = MaterialTheme.colors.onBackground,
                      shape = MaterialTheme.shapes.medium,
                  )
                  .padding(all = MaterialTheme.keylines.content),
      ) {
        Column {
          Text(
              text =
                  "First, make sure this device (Device 1) has an active connection to the Internet. You will be sharing this device's connection, so if this device cannot access the Internet, nothing can.",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content),
              text =
                  "On the device you want to connect (Device 2) to the Internet, go to the Wi-Fi settings.",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.typography),
              text = "This may be in a different place depending on your device.",
              style = MaterialTheme.typography.caption,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content * 2),
              text = "Connect Device 2 to the network:",
              style =
                  MaterialTheme.typography.body2.copy(
                      fontWeight = FontWeight.Bold,
                  ),
          )

          Text(
              text = "Name: $ssid",
              style = MaterialTheme.typography.body1,
          )

          Text(
              text = "Password: $password",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content * 2),
              text =
                  "You may get a message that Device 2 does not have Internet, but is connected to a network. You will need to now go to the Proxy Network settings page for Device 2.",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.typography),
              text = "This may be in a different place depending on your device.",
              style = MaterialTheme.typography.caption,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content * 2),
              text = "Set Proxy Network for Device 2:",
              style =
                  MaterialTheme.typography.body2.copy(
                      fontWeight = FontWeight.Bold,
                  ),
          )

          Text(
              text = "Proxy URL/Hostname: $ip",
              style = MaterialTheme.typography.body1,
          )

          Text(
              text = "Proxy Port: $port",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.typography),
              text = "Leave everything else blank!",
              style = MaterialTheme.typography.caption,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content * 2),
              text =
                  "Turn the Wi-Fi off on Device 2, and then back on again. It should automatically connect to the network shared by Device 1",
              style = MaterialTheme.typography.body1,
          )

          Text(
              modifier = Modifier.padding(top = MaterialTheme.keylines.content),
              text =
                  "You should now have an Internet connection on Device 2! You can go to the Activity or Error screens in this application to see any information about your network connections.",
              style = MaterialTheme.typography.body1,
          )
        }
      }
    }
  }
}

@Composable
private fun NetworkInformation(
    modifier: Modifier = Modifier,
    isEditable: Boolean,
    showErrorHintMessage: Boolean,
    canUseCustomConfig: Boolean,
    ssid: String,
    password: String,
    port: String,
    ip: String,
    bandName: String,
    onSsidChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
) {

  Crossfade(
      modifier = modifier,
      targetState = isEditable,
  ) { editable ->
    Column {
      AnimatedVisibility(
          visible = showErrorHintMessage,
      ) {
        Text(
            text = "Try toggling this device's Wi-Fi off and on, then try again.",
            style =
                MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.error,
                ),
        )
      }

      if (editable) {
        Editor(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            enabled = canUseCustomConfig,
            title = "NAME",
            value = ssid,
            onChange = onSsidChanged,
        )

        Editor(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            enabled = canUseCustomConfig,
            title = "PASSWORD",
            value = password,
            onChange = onPasswordChanged,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                ),
        )

        Editor(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            title = "PORT",
            value = port,
            onChange = onPortChanged,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
        )
      } else {
        Item(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            title = "NAME",
            value = ssid,
        )

        Item(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            title = "PASSWORD",
            value = password,
        )

        Item(
            modifier = Modifier.padding(top = MaterialTheme.keylines.content),
            title = "IP",
            value = ip,
        )

        Item(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            title = "PORT",
            value = port,
        )

        Item(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
            title = "BAND",
            value = bandName,
        )
      }
    }
  }
}

@Composable
private fun Editor(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String,
    value: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit,
) {
  Column(
      modifier = modifier,
  ) {
    TextField(
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        value = value,
        onValueChange = onChange,
        label = {
          Text(
              text = title,
          )
        },
    )
  }
}

@Composable
private fun DisplayStatus(
    modifier: Modifier = Modifier,
    title: String,
    status: RunningStatus,
) {
  val text =
      remember(status) {
        when (status) {
          is RunningStatus.Error -> "Error: ${status.message}"
          is RunningStatus.NotRunning -> "Not Running"
          is RunningStatus.Running -> "Running"
          is RunningStatus.Starting -> "Starting"
          is RunningStatus.Stopping -> "Stopping"
        }
      }

  val errorColor = MaterialTheme.colors.error
  val color =
      remember(status, errorColor) {
        when (status) {
          is RunningStatus.Error -> errorColor
          is RunningStatus.NotRunning -> Color.Unspecified
          is RunningStatus.Running -> Color.Green
          is RunningStatus.Starting -> Color.Cyan
          is RunningStatus.Stopping -> Color.Magenta
        }
      }

  Item(
      modifier = modifier,
      title = title,
      value = text,
      color = color,
  )
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color = Color.Unspecified,
) {
  Column(
      modifier = modifier,
  ) {
    Text(
        text = title,
        style =
            MaterialTheme.typography.caption.copy(
                fontWeight = FontWeight.Bold,
            ),
    )
    Text(
        text = value,
        style = MaterialTheme.typography.body1,
        color = color,
    )
  }
}