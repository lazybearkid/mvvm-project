package com.example.mvvmapplication.di.auth

import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.persistence.AuthTokenDao
import com.example.mvvmapplication.repository.auth.AuthRepository
import com.example.mvvmapplication.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideFakeApiService(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz/")
            .build()
            .create(AuthApi::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        authApi: AuthApi
    ): AuthRepository{
        return AuthRepository(authTokenDao, accountPropertiesDao, authApi, sessionManager)
    }
}