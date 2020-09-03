package com.example.mvvmapplication.ui.auth.state

import com.example.mvvmapplication.models.AuthToken

data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
)


data class RegistrationFields(
    var registration_email: String? = null,
    var registration_username: String? = null,
    var registration_password: String? = null,
    var registration_confirm_password: String? = null
){
    class RegistrationError{
        companion object {
            fun mustFillAllFields(): String {
                return "All fields must be filled"
            }

            fun passwordNotMatch(): String {
                return "Passwords do not match"
            }

            fun none():String {
                return "None"
            }
        }
    }

    //function validate registration
    fun isRegistrationValid(): String {
        if(registration_email.isNullOrEmpty()
            || registration_username.isNullOrEmpty()
            || registration_password.isNullOrEmpty()
            || registration_confirm_password.isNullOrEmpty()){
            return RegistrationError.mustFillAllFields()
        }

        if (!registration_password.equals(registration_confirm_password)){
            return RegistrationError.passwordNotMatch()
        }

        return RegistrationError.none()
    }
}

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
){
    class LoginError(){
        companion object {
            fun mustFillAllFields(): String {
                return "All fields must be filled"
            }

            fun none(): String {
                return "None"
            }
        }
    }

    fun isLoginFieldsValid(): String {
        if (login_email.isNullOrEmpty() || login_password.isNullOrEmpty()){
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}