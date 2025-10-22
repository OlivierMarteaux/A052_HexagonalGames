package com.openclassrooms.hexagonal.games.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oliviermarteaux.localShared.openAppSettings
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.utils.checkNotificationPermission
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
  onBackClick: () -> Unit
) {
  with (viewModel) {
    val context = LocalContext.current

    if (notifPermissionAlertDialog) {
      SharedAlertDialog(
        onConfirm = {
          showNotifPermissionAlertDialog(false);
          openAppSettings(context)
        },
        onDismiss = {
          showNotifPermissionAlertDialog(false)
        },
        modifier = modifier,
        title = "Notifications Permission Required",
        text = "You need to grant notifications permission to receive notifications from app. \nDo you want to go to Settings to grant this permission?",
        dismissText = "Cancel",
        confirmText = "OK"
      )
    }
    SharedScaffold(
      title = stringResource(id = R.string.action_settings),
      modifier = modifier,
      onBackClick = onBackClick,
    ) { contentPadding ->
      Box {
        Settings(
          modifier = Modifier.padding(contentPadding),
          disableNotification = {
            toggleNotifications(false)
            showNotifStateToast()
          },
          enableNotification = {
            toggleNotifications(true)
            showNotifStateToast()
          }
        )
        if (notifStateToast) { SharedToast("Notifications are $notifState") }
      }
    }
  }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Settings(
  modifier: Modifier = Modifier,
  enableNotification: () -> Unit,
  disableNotification: () -> Unit,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    Icon(
      modifier = Modifier.size(200.dp),
      painter = painterResource(id = R.drawable.ic_notifications),
      tint = MaterialTheme.colorScheme.onSurface,
      contentDescription = stringResource(id = R.string.contentDescription_notification_icon)
    )
    val context = LocalContext.current
    Button(
      onClick = {
        if (checkNotificationPermission(context)){
          Log.d("OM_TAG", "Settings: OnClick:  enableNotification() called")
          enableNotification()
        }
        else {
          Log.d("OM_TAG", "Settings: OnClick:  requestNotifPermission() called")
          viewModel.showNotifPermissionAlertDialog(true)
        }
      }
    ) {
      Text(text = stringResource(id = R.string.notification_enable))
    }
    Button(
      onClick = {
        Log.d("OM_TAG", "Settings: OnClick:  disableNotification() called")
        disableNotification()
      }
    ) {
      Text(text = stringResource(id = R.string.notification_disable))
    }
  }
}