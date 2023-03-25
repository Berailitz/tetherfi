/*
 * Copyright 2023 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.tetherfi.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.tetherfi.ui.icons.Devices
import com.pyamsoft.tetherfi.ui.icons.PhoneAndroid

internal fun LazyListScope.renderDeviceIdentifiers(
    itemModifier: Modifier = Modifier,
) {
  item {
    ThisInstruction(
        modifier = itemModifier,
        small = true,
    ) {
      Text(
          text = "This Device",
          style =
              MaterialTheme.typography.caption.copy(
                  color =
                      MaterialTheme.colors.onSurface.copy(
                          alpha = ContentAlpha.medium,
                      ),
              ),
      )
    }
  }

  item {
    OtherInstruction(
        modifier = itemModifier,
        small = true,
    ) {
      Text(
          text = "Other Device",
          style =
              MaterialTheme.typography.caption.copy(
                  color =
                      MaterialTheme.colors.onSurface.copy(
                          alpha = ContentAlpha.medium,
                      ),
              ),
      )
    }
  }
}

private val THIS_DEVICE_COLOR = Color(0xFF4CAF50)
private val OTHER_DEVICE_COLOR = Color(0xFF2196F3)

@Composable
private fun ThisDevice(
    modifier: Modifier = Modifier,
    small: Boolean,
) {
  val adjustment = MaterialTheme.keylines.typography
  val sizeAdjustment =
      remember(
          small,
          adjustment,
      ) {
        if (small) adjustment else ZeroSize
      }

  Icon(
      modifier =
          modifier
              .padding(start = if (small) 2.dp else ZeroSize)
              .size(ImageDefaults.IconSize - sizeAdjustment)
              .padding(end = if (small) 2.dp else ZeroSize),
      imageVector = Icons.Filled.PhoneAndroid,
      contentDescription = "This Device",
      tint = THIS_DEVICE_COLOR,
  )
}

@Composable
private fun OtherDevice(
    modifier: Modifier = Modifier,
    small: Boolean,
) {
  val adjustment = MaterialTheme.keylines.typography
  val sizeAdjustment =
      remember(
          small,
          adjustment,
      ) {
        if (small) adjustment else ZeroSize
      }

  Icon(
      modifier =
          modifier
              .padding(start = if (small) 2.dp else ZeroSize)
              .size(ImageDefaults.IconSize - sizeAdjustment)
              .padding(end = if (small) 2.dp else ZeroSize),
      imageVector = Icons.Filled.Devices,
      contentDescription = "Other Devices",
      tint = OTHER_DEVICE_COLOR,
  )
}

@Composable
internal fun ThisInstruction(
    modifier: Modifier = Modifier,
    small: Boolean = false,
    content: @Composable () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
      ThisDevice(small = small)
    }

    Box(
        modifier = Modifier.weight(1F).padding(start = MaterialTheme.keylines.content),
    ) {
      content()
    }
  }
}

@Composable
internal fun OtherInstruction(
    modifier: Modifier = Modifier,
    small: Boolean = false,
    content: @Composable () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
      OtherDevice(small = small)
    }

    Box(
        modifier = Modifier.weight(1F).padding(start = MaterialTheme.keylines.content),
    ) {
      content()
    }
  }
}

@Preview
@Composable
private fun PreviewDeviceIdentifiers() {
  LazyColumn { renderDeviceIdentifiers() }
}
