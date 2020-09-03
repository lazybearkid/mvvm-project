package com.example.mvvmapplication.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mvvmapplication.NetworkBoundResource
import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.api.auth.networkResponses.LoginResponse
import com.example.mvvmapplication.api.auth.networkResponses.RegistrationResponse
import com.example.mvvmapplication.models.AuthToken
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.persistence.AuthTokenDao
import com.example.mvvmapplication.session.SessionManager
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.Response
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.ui.auth.state.AuthStateEvent
import com.example.mvvmapplication.ui.auth.state.AuthViewState
import com.example.mvvmapplication.ui.auth.state.LoginFields
import com.example.mvvmapplication.ui.auth.state.RegistrationFields
import com.example.mvvmapplication.util.ApiEmptyResponse
import com.example.mvvmapplication.util.ApiErrorResponse
import com.example.mvvmapplication.util.ApiSuccessResponse
import com.example.mvvmapplication.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mvvmapplication.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.mvvmapplication.util.GenericApiResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val authApi: AuthApi,
    val sessionManager: SessionManager
) {
    private val TAG: String = "AuthRepository AppDebug"
    private var repositoryJob: Job? = null


    fun loginAttempt(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldsErrors = LoginFields(email, password).isLoginFieldsValid()
        if (loginFieldsErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldsErrors, ResponseType.Dialog())
        }
        return object :
            NetworkBoundResource<LoginResponse, AuthViewState>(isNetWorkAvailable = true) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response ")

                //incorrect login credential count as 200 OK
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                //correct login credential
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return authApi.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldErrors =
            RegistrationFields(email, username, password, confirmPassword).isRegistrationValid()
        if (registrationFieldErrors != RegistrationFields.RegistrationError.none()) {
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog())
        }
        return object :
            NetworkBoundResource<RegistrationResponse, AuthViewState>(isNetWorkAvailable = true) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: handling registration success request")
                //incorrect registration: such as existing account,.....
                if (response.body.response == GENERIC_AUTH_ERROR){
                    return onErrorReturn(response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }
                onCompleteJob(
                    dataState = DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return authApi.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }


    fun cancelActiveJobs() {
        Log.d(TAG, "cancelActiveJobs: cancelling all on-going jobs...")
        repositoryJob?.cancel()
    }

    private fun returnErrorResponse(
        errors: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                Log.d(TAG, "onActive: returning error $errors" )
                value = DataState.error(
                    response = Response(
                        message = errors,
                        responseType = responseType
                    )
                )
            }
        }
    }
}