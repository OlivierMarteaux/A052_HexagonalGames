package com.oliviermarteaux.shared.firebase.firebaseCloudMessaging

//import android.util.Log
//import com.google.firebase.messaging.FirebaseMessaging
//
//fun subscribeToFcmNotificationTopic(
//    notifTopic: String = "allUsers"
//) {
//    FirebaseMessaging.getInstance().subscribeToTopic(notifTopic)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("FCM", "Subscribed to $notifTopic topic")
//            }
//        }
//}