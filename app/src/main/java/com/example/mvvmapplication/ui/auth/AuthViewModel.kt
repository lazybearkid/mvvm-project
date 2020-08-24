package com.example.mvvmapplication.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mvvmapplication.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
): ViewModel() {
}