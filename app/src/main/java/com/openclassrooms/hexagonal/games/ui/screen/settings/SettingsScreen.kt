package com.openclassrooms.hexagonal.games.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oliviermarteaux.localShared.openAppSettings
import com.oliviermarteaux.shared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.IconScaffold
import com.oliviermarteaux.shared.composables.IconSource
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.utils.checkNotificationPermission
import com.openclassrooms.hexagonal.games.R

/**
 * A screen for managing the application's settings.
 *
 * @param modifier The modifier to apply to this screen.
 * @param viewModel The view model for this screen.
 * @param onBackClick A function to call when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
  onBackClick: () -> Unit
) {
  with(viewModel) {
    val context = LocalContext.current

    if (notifPermissionAlertDialog) {
      SharedAlertDialog(
        onConfirm = {
          showNotifPermissionAlertDialog(false)
          openAppSettings(context)
        },
        onDismiss = {
          showNotifPermissionAlertDialog(false)
        },
        modifier = modifier,
        title = stringResource(R.string.settings_screen_alert_dialog_title),
        text = stringResource(R.string.settings_screen_alert_dialog_text),
        dismissText = stringResource(R.string.application_cancel),
        confirmText = stringResource(R.string.application_ok)
      )
    }
    SharedScaffold(
      title = stringResource(id = R.string.action_settings),
      modifier = modifier,
      onBackClick = onBackClick,
    ) { contentPadding ->
      Box {
        Settings(
          modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = SharedPadding.xl)
            .fillMaxSize(),
          disableNotification = {
            toggleNotifications(false)
            showNotifStateToast()
          },
          enableNotification = {
            toggleNotifications(true)
            showNotifStateToast()
          }
        )
        if (notifStateToast) {
          val stateAsString: String =
            if (notifState) stringResource(R.string.settings_screen_enabled) else stringResource(R.string.settings_screen_disabled)
          SharedToast(stringResource(R.string.settings_screen_toast_notif_state, stateAsString))
        }
      }
    }
  }
}

/**
 * A composable for the body of the settings screen.
 *
 * @param modifier The modifier to apply to this composable.
 * @param enableNotification A function to call to enable notifications.
 * @param disableNotification A function to call to disable notifications.
 * @param viewModel The view model for this screen.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Settings(
  modifier: Modifier = Modifier,
  enableNotification: () -> Unit,
  disableNotification: () -> Unit,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  IconScaffold(
    icon = IconSource.PainterIcon(painterResource(R.drawable.hexagonal_games_logo)),
    modifier = modifier
  ) {
    SharedButton(stringResource(id = R.string.notification_enable)) {
      if (checkNotificationPermission(context)) {
        Log.d("OM_TAG", "Settings: OnClick:  enableNotification() called")
        enableNotification()
      } else {
        Log.d("OM_TAG", "Settings: OnClick:  requestNotifPermission() called")
        viewModel.showNotifPermissionAlertDialog(true)
      }
    }
    SharedButton(stringResource(id = R.string.notification_disable)) {
      Log.d("OM_TAG", "Settings: OnClick:  disableNotification() called")
      disableNotification()
    }
  }
}