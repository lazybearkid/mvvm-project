package com.example.mvvmapplication.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mvvmapplication.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): ViewModel()