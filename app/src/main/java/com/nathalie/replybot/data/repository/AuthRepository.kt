package com.nathalie.replybot.data.repository

import com.nathalie.replybot.data.model.User

interface AuthRepository {
    suspend fun register(user: User)
    suspend fun login(email: String, password: String): Boolean
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    fun signOut()
    fun getUid(): String?
}