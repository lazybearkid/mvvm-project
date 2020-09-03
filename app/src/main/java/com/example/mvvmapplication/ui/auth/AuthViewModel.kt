package com.example.mvvmapplication.ui.auth

import androidx.lifecycle.LiveData
import com.example.mvvmapplication.models.AuthToken
import com.example.mvvmapplication.repository.auth.AuthRepository
import com.example.mvvmapplication.ui.BaseViewModel
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.auth.state.AuthStateEvent
import com.example.mvvmapplication.ui.auth.state.AuthViewState
import com.example.mvvmapplication.ui.auth.state.LoginFields
import com.example.mvvmapplication.ui.auth.state.RegistrationFields
import com.example.mvvmapplication.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>(){

    fun setRegistrationField(registrationFields: RegistrationFields){
        val update = getCurrentNewStateOrNew()
        if (update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentNewStateOrNew()
        if (update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentNewStateOrNew()
        if( update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            is AuthStateEvent.LoginAttemptEvent -> {
                return authRepository.loginAttempt(
                    stateEvent.email,
                    stateEvent.password
                )
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.password2
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}