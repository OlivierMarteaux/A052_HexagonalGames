package com.oliviermarteaux.shared.firebase.firebaseCloudMessaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

fun getDeviceToken(){
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("OM_TAG", "FCM: Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("OM_TAG", "FCM: token: $token")
            // TODO: send token to Firestore or backend
        }
}