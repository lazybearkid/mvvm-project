package com.example.mvvmapplication.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mvvmapplication.NetworkBoundResource
import com.example.mvvmapplication.api.auth.AuthApi
import com.example.mvvmapplication.api.auth.networkResponses.LoginResponse
import com.example.mvvmapplication.api.auth.networkResponses.RegistrationResponse
import com.example.mvvmapplication.models.AccountPropertiesEntity
import com.example.mvvmapplication.models.AuthToken
import com.example.mvvmapplication.persistence.AccountPropertiesDao
import com.example.mvvmapplication.persistence.AuthTokenDao
import com.example.mvvmapplication.session.SessionManager
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.Response
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.ui.auth.state.AuthViewState
import com.example.mvvmapplication.ui.auth.state.LoginFields
import com.example.mvvmapplication.ui.auth.state.RegistrationFields
import com.example.mvvmapplication.util.*
import com.example.mvvmapplication.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.example.mvvmapplication.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val authApi: AuthApi,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {
    private val TAG: String = "AuthRepository AppDebug"
    private var repositoryJob: Job? = null


    fun loginAttempt(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldsErrors = LoginFields(email, password).isLoginFieldsValid()
        if (loginFieldsErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldsErrors, ResponseType.Dialog())
        }
        return object :
            NetworkBoundResource<LoginResponse, AuthViewState>(
                isNetWorkAvailable = true,
                isNetWorkRequest = true
            ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response ")

                //incorrect login credential count as 200 OK
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                accountPropertiesDao.insertAndIgnore(
                    AccountPropertiesEntity(
                        pk = response.body.pk,
                        email = response.body.email,
                        username = ""
                    )
                )
                val result = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                //if save auth Token failed
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            response = Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
                //if save succeed
                //save authenticated user email to prefs
                saveAuthenticatedUserPreferences(email)

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

            // not implemented in this scenario
            override suspend fun createCacheRequestAndReturn() {

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
            NetworkBoundResource<RegistrationResponse, AuthViewState>(
                isNetWorkAvailable = true,
                isNetWorkRequest = true
            ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: handling registration success request")
                //incorrect registration: such as existing account,.....
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(
                        response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }
                accountPropertiesDao.insertAndIgnore(
                    AccountPropertiesEntity(
                        pk = response.body.pk,
                        email = response.body.email,
                        username = ""
                    )
                )
                val result = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                //if save auth Token failed
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            response = Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
                //if save succeed
                //save authenticated user email to prefs
                saveAuthenticatedUserPreferences(email)

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

            // not implemented in this scenario
            override suspend fun createCacheRequestAndReturn() {

            }
        }.asLiveData()
    }

    fun attemptAutoAuthenticatePreviousUser(): LiveData<DataState<AuthViewState>> {
        //check last authenticated user email
        val lastAuthEmail = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (lastAuthEmail.isNullOrEmpty()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
            return returnNoTokenFound()
        } else {
            return object : NetworkBoundResource<Void, AuthViewState>(
                sessionManager.isConnectedToInternet(),
                false) {
                override suspend fun createCacheRequestAndReturn() {
                    accountPropertiesDao.searchByEmail(lastAuthEmail).let { accountProperties ->
                        Log.d(TAG, "createCacheRequestAndReturn: searching for token... account properties: $accountProperties")
                        accountProperties?.let { it ->
                            if (it.pk > -1) {
                                authTokenDao.searchByPk(it.pk).let { token ->
                                    if (token != null) {
                                        onCompleteJob(
                                            DataState.data(
                                                AuthViewState(authToken = token)
                                            )
                                        )
                                        return
                                    }
                                }
                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: no token found")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                message = SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                responseType = ResponseType.None()
                            )
                        )
                    )
                }

                //not implemented
                override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {

                }

                //not implemented
                override fun createCall(): LiveData<GenericApiResponse<Void>> {
                    return AbsentLiveData.create()
                }

                override fun setJob(job: Job) {
                    repositoryJob?.cancel()
                    repositoryJob = job
                }
            }.asLiveData()
        }
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(
                        message = SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                        responseType = ResponseType.None()
                    )
                )
            }
        }
    }

    private fun saveAuthenticatedUserPreferences(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
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
                Log.d(TAG, "onActive: returning error $errors")
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