package com.openclassrooms.hexagonal.games.domain.model

data class NewUser(
    /**
     * New User's first name.
     */
    val firstname: String = "",

    /**
     * New User's last name.
     */
    val lastname: String = "",
    /**
     * New User's email address.
     */
    val email: String = "",
    /**
     * New User's password.
     */
    val password: String = ""
)
