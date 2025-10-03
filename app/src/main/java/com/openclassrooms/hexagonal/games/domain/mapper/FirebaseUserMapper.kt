package com.openclassrooms.hexagonal.games.domain.mapper

import android.R.attr.password
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.User

fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        firstname = displayName?.substringBefore(" ") ?: "",
        lastname = displayName?.substringAfter(" ") ?: "",
        email = email ?: ""
    )
}