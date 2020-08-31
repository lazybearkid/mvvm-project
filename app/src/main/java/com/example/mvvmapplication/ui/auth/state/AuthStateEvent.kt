package com.example.mvvmapplication.ui.auth.state

sealed class AuthStateEvent {
    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ): AuthStateEvent()

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val password2: String
    ): AuthStateEvent()

    //when the app is first open. run this function to check the previous user
    class CheckPreviousAuthEvent(): AuthStateEvent()
}