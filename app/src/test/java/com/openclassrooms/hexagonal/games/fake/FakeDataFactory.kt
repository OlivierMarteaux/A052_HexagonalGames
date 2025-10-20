package com.openclassrooms.hexagonal.games.fake

import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User

object FakeDataFactory {

    // -------------------------
    // 👤 Fake Users
    // -------------------------
    val fakeUsers = listOf(
        User(
            id = "user_1",
            firstname = "Alice",
            lastname = "Anderson",
            email = "alice.anderson@example.com"
        ),
        User(
            id = "user_2",
            firstname = "Bob",
            lastname = "Brown",
            email = "bob.brown@example.com"
        ),
        User(
            id = "user_3",
            firstname = "Charlie",
            lastname = "Clark",
            email = "charlie.clark@example.com"
        )
    )

    // -------------------------
    // 💬 Fake Comments
    // -------------------------
    val fakeComments = listOf(
        Comment(
            author = fakeUsers[0],
            content = "Great post! Thanks for sharing.",
            timestamp = 1729450000000
        ),
        Comment(
            author = fakeUsers[1],
            content = "Interesting perspective.",
            timestamp = 1729453600000
        ),
        Comment(
            author = fakeUsers[2],
            content = "Can you elaborate more on this topic?",
            timestamp = 1729457200000
        )
    )

    // -------------------------
    // 📝 Fake Posts
    // -------------------------
    val fakePosts = listOf(
        Post(
            id = "post_1",
            title = "Exploring Kotlin Coroutines",
            description = "A deep dive into structured concurrency and Flow.",
            photoUrl = "https://example.com/images/kotlin-coroutines.jpg",
            timestamp = 1729440000000,
            author = fakeUsers[0],
            comments = listOf(fakeComments[0], fakeComments[1])
        ),
        Post(
            id = "post_2",
            title = "Understanding Dependency Injection with Dagger/Hilt",
            description = "A practical guide to clean architecture in Android.",
            photoUrl = "https://example.com/images/hilt-guide.jpg",
            timestamp = 1729500000000,
            author = fakeUsers[1],
            comments = listOf(fakeComments[1], fakeComments[2])
        ),
        Post(
            id = "post_3",
            title = "Jetpack Compose Tips & Tricks",
            description = "10 patterns to improve UI performance and maintainability.",
            photoUrl = "https://example.com/images/compose-tips.jpg",
            timestamp = 1729600000000,
            author = fakeUsers[2],
            comments = listOf(fakeComments[0])
        )
    )

    val fakePost = fakePosts[0]
    val fakeComment = fakeComments[0]
    val fakeUser = fakeUsers[0]
    val fakeNewUser = NewUser(
        firstname = "John",
        lastname = "Doe",
        email = "john.doe@example.com"
    )
}
