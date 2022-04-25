package com.pyamsoft.widefi.server

import androidx.annotation.CheckResult

interface ServerPreferences {

  @CheckResult suspend fun getSsid(): String

  suspend fun setSsid(ssid: String)

  @CheckResult suspend fun getPassword(): String

  suspend fun setPassword(password: String)

  @CheckResult suspend fun getPort(): Int

  suspend fun setPort(port: Int)

  @CheckResult suspend fun getNetworkBand(): NetworkBand

  suspend fun setNetworkBand(band: NetworkBand)

  enum class NetworkBand {
    AUTO,
    LEGACY,
    MODERN
  }
}
