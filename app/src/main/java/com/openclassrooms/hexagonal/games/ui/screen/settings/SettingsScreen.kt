package com.openclassrooms.hexagonal.games.ui.screen.settings

import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImagePainter.State.Empty.painter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.math.LinearTransformation.vertical
import com.oliviermarteaux.localShared.openAppSettings
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.localShared.composables.SharedIcon
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
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
  val context = LocalContext.current
//  Column(
//    modifier = modifier.fillMaxSize(),
//    horizontalAlignment = Alignment.CenterHorizontally,
//    verticalArrangement = Arrangement.SpaceEvenly
//  ) {
//    SharedIcon(
//      modifier = Modifier
//        .size(200.dp)
//        .weight(33f),
//      painter = painterResource(R.drawable.hexagonal_games_logo),
//    )
//
//    Column(
//      horizontalAlignment = Alignment.CenterHorizontally,
//      verticalArrangement = Arrangement.SpaceEvenly,
//      modifier = Modifier.weight(66f)
//    ) {
  IconScaffold(modifier = modifier) {
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
//      }
//    }
//  }
}

@Composable
fun IconScaffold(
  modifier: Modifier = Modifier,
  verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
  content: @Composable () -> Unit
){
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
//    verticalArrangement = Arrangement.SpaceEvenly,
  ) {
    SharedIcon(
      modifier = Modifier
        .padding(vertical = 50.dp)
        .size(200.dp)
        /*.weight(33f)*/,
      painter = painterResource(R.drawable.hexagonal_games_logo),
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = verticalArrangement,
      modifier = Modifier.fillMaxSize()
    /*.weight(66f)*/
    ) { content() }
  }
}