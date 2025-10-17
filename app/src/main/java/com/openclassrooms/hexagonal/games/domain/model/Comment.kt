package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

data class Comment(
    val author: User = User(),
    val content: String = "",
    val timestamp: Long = 0L,
) : Serializable
