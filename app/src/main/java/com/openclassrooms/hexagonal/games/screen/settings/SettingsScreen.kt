package com.openclassrooms.hexagonal.games.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oliviermarteaux.localShared.openAppSettings
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.utils.checkNotificationPermission
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
  onBackClick: () -> Unit
) {

  val notifPermissionAlertDialog: Boolean = viewModel.notifPermissionAlertDialog
  val notifStateToast: Boolean = viewModel.notifStateToast
  val notifState: String = viewModel.notifState
  val context = LocalContext.current
//  val notificationPermissionState: Boolean = checkNotificationPermission(context)

  if (notifPermissionAlertDialog) {
    SharedAlertDialog(
      onConfirm = {
        viewModel.showNotifPermissionAlertDialog(false);
        openAppSettings(context)
                  },
      onDismiss = {
        viewModel.showNotifPermissionAlertDialog(false)
                  },
      modifier = modifier,
      title = "Notifications Permission Required",
      text = "You need to grant notifications permission to receive notifications from app. \nDo you want to go to Settings to grant this permission?",
      dismissText = "Cancel",
      confirmText = "OK"
    )
  }

//  if (notifPermissionAlertDialog) {
//    RequestNotificationPermission()
//  }
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(id = R.string.action_settings))
        },
        navigationIcon = {
          IconButton(onClick = {
            onBackClick()
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.contentDescription_go_back)
            )
          }
        }
      )
    }
  ) { contentPadding ->
    Box {
      Settings(
        modifier = Modifier.padding(contentPadding),
        disableNotification = {
          viewModel.toggleNotifications(false)
          viewModel.showNotifStateToast()
        },
        enableNotification = {
          viewModel.toggleNotifications(true)
          viewModel.showNotifStateToast()
        }
      )
      if (notifStateToast) {
        SharedToast(
          text = "Notifications are $notifState",
          durationMillis = TOAST_DURATION
        )
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
//  val notifPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//    rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
//  } else { null }
  
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
//        viewModel.checkNotifPermission(
//          notifPermissionState = notifPermissionState,
//          onNotifPermissionGranted = enableNotification
//        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//          if (notifPermissionState?.status?.isGranted == false) {
//            notifPermissionState.launchPermissionRequest()
//          } else { enableNotification() }
//        }
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

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun SettingsPreview() {
  HexagonalGamesTheme {
    Settings(
      enableNotification = { },
      disableNotification = { }
    )
  }
}