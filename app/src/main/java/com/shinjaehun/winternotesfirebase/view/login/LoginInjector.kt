package com.shinjaehun.winternotesfirebase.view.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.winternotesfirebase.model.FirebaseUserRepoImpl
import com.shinjaehun.winternotesfirebase.model.IUserRepository

class LoginInjector(application: Application): AndroidViewModel(application) {
    init {
        FirebaseApp.initializeApp(application)
    }

    private fun getUserRepository(): IUserRepository {
        return FirebaseUserRepoImpl()
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory =
        LoginViewModelFactory(getUserRepository())
}