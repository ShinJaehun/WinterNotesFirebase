package com.shinjaehun.winternotesfirebase.model

import com.shinjaehun.winternotesfirebase.common.Result

interface IUserRepository {
    suspend fun getCurrentUser(): Result<Exception, User?>
    suspend fun signOutCurrentUser(): Result<Exception, Unit>
    suspend fun signInUser(email: String, password: String): Result<Exception, Unit>
}