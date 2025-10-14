package com.oliviermarteaux.shared.composables.startup

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    Log.d("OM_TAG", "RequestNotificationPermission: defining launcher")
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Log.d("FCM", "Notification permission granted: $isGranted")
        }
    )

    Log.d("OM_TAG", "RequestNotificationPermission: LaunchedEffect")
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("OM_TAG", "RequestNotificationPermission: requesting permission because Android>TIRAMISU")
            val shouldShowRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,
                    Manifest.permission.POST_NOTIFICATIONS)
            Log.d("OM_TAG", "RequestNotificationPermission: shouldShowRationale=$shouldShowRationale")
            if (!shouldShowRationale) {
                Log.d("OM_TAG", "RequestNotificationPermission: custom request needed")
            } else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}