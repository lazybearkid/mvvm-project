package com.example.mvvmapplication.repository.auth

import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.persistence.AuthTokenDao
import com.example.mvvmapplication.session.SessionManager
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val authApi: AuthApi,
    val sessionManager: SessionManager
){
}