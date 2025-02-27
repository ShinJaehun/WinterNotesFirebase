package com.shinjaehun.winternotesfirebase.view.login

sealed class LoginEvent {
    data class OnLoginButtonClick(val email: String, val password: String): LoginEvent()
    object OnStart: LoginEvent()
}