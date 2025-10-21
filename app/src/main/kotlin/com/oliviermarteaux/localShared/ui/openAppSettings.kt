package com.oliviermarteaux.localShared

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        Log.d("OM_TAG", "shared library: openAppSettings: $data")
    }
    context.startActivity(intent)
}