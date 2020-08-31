package com.example.mvvmapplication.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.models.AuthToken
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.persistence.AuthTokenDao
import com.example.mvvmapplication.session.SessionManager
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.Response
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.ui.auth.state.AuthViewState
import com.example.mvvmapplication.util.ApiEmptyResponse
import com.example.mvvmapplication.util.ApiErrorResponse
import com.example.mvvmapplication.util.ApiSuccessResponse
import com.example.mvvmapplication.util.ErrorHandling.Companion.ERROR_UNKNOWN
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val authApi: AuthApi,
    val sessionManager: SessionManager
){
    fun loginAttempt(email: String, password: String): LiveData<DataState<AuthViewState>>{
        return authApi.login(email, password).switchMap { response ->
            object: LiveData<DataState<AuthViewState>>(){
                override fun onActive() {
                    super.onActive()
                    when(response){
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                data = AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk, response.body.token
                                    )
                                ),
                                response = null
                            )
                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun attemptRegistration(email: String, username: String, password: String, confirmPassword: String): LiveData<DataState<AuthViewState>>{
        return authApi.register(email, username, password, confirmPassword).switchMap { response ->
            object: LiveData<DataState<AuthViewState>>(){
                override fun onActive() {
                    super.onActive()
                    when(response){
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                data = AuthViewState(
                                    authToken = AuthToken(response.body.pk, response.body.token)
                                ),
                                response = null
                            )
                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }



}