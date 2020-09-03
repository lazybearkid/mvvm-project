package com.example.mvvmapplication.util

class ErrorHandling{
    companion object {
        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val ERROR_UNKNOWN = "Unknown error"
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection"
        const val UNABLE_TODO_OPERATION_WITHOUT_INTERNET = "Unable to do operation without internet"
        const val GENERIC_AUTH_ERROR = "Error"
        const val ERROR_SAVE_AUTH_TOKEN = "Error saving authentication token.\nTry restarting the app."
        const val ERROR_SAVE_ACCOUNT_PROPERTIES = "Error saving account properties.\nTry restarting the app."

        fun isNetworkError(msg: String): Boolean {
            return when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> true
                else -> false
            }
        }
    }
}