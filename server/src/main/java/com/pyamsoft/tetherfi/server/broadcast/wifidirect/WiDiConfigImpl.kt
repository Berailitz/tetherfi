/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.tetherfi.server.broadcast.wifidirect

import android.net.wifi.p2p.WifiP2pConfig
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import com.pyamsoft.tetherfi.server.ConfigPreferences
import com.pyamsoft.tetherfi.server.ServerDefaults
import com.pyamsoft.tetherfi.server.ServerNetworkBand
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal class WiDiConfigImpl
@Inject
internal constructor(
    private val preferences: ConfigPreferences,
) : WiDiConfig {

  @CheckResult
  @RequiresApi(Build.VERSION_CODES.Q)
  private suspend fun getPreferredSsid(): String {
    return preferences.listenForSsidChanges().first()
  }

  @CheckResult
  @RequiresApi(Build.VERSION_CODES.Q)
  private suspend fun getPreferredPassword(): String {
    return preferences.listenForPasswordChanges().first()
  }

  @CheckResult
  @RequiresApi(Build.VERSION_CODES.Q)
  private suspend fun getPreferredChannel(): Int {
    return preferences.listenForChannelChanges().first()
  }

  override suspend fun getConfiguration(): WifiP2pConfig? =
      withContext(context = Dispatchers.Default) {
        if (!ServerDefaults.canUseCustomConfig()) {
          return@withContext null
        }

        val ssid = ServerDefaults.asSsid(getPreferredSsid())
        val passwd = getPreferredPassword()
        val channel = getPreferredChannel()

        return@withContext WifiP2pConfig.Builder()
            .setNetworkName(ssid)
            .setPassphrase(passwd)
            .setGroupOperatingFrequency(channelToFrequency(channel))
            .build()
      }
}

/**
 * Based on:
 * https://elixir.bootlin.com/linux/v5.12.8/source/net/wireless/util.c#L75
 * https://cs.android.com/android/platform/superproject/+/master:packages/modules/Wifi/framework/java/android/net/wifi/ScanResult.java;l=789;drc=71d758698c45984d3f8de981bf98e56902480f16
 */
private suspend fun channelToFrequency(chan: Int): Int {
    return if (chan in 182..196)
        4000 + chan * 5;
    else
        5000 + chan * 5;
}
