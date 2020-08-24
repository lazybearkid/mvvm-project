package com.example.mvvmapplication.repository.auth

import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.session.SessionManager

class AuthRepository
constructor(
    val accountPropertiesDao: AccountPropertiesDao,
    val authApi: AuthApi,
    val sessionManager: SessionManager
){
}