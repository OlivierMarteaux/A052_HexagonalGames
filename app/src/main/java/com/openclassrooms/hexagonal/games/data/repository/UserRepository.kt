package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi) {

    fun getCurrentUser(): User? = userApi.getCurrentUser()?.toUser()
    fun signOut() = userApi.signOut()
    fun deleteAccount() = userApi.deleteAccount()
    suspend fun createAccount(newUser: NewUser) = userApi.createAccount(newUser)
    suspend fun checkEmail(email: String) = userApi.checkEmail(email)
    suspend fun signIn(email: String, password: String): Result<Unit> = userApi.signIn(email, password)
}