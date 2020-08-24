package com.example.mvvmapplication.session

import android.app.Application
import com.example.mvvmapplication.persistence.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
){
}