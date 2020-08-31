package com.example.mvvmapplication.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmapplication.models.AuthToken
import com.example.mvvmapplication.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
){
    private val TAG: String = "AppDebug"
    private val _cachedToken = MutableLiveData<AuthToken?>()
    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    fun login(authToken: AuthToken){
        setValue(authToken)
    }

    fun logout(){
        Log.d(TAG, "logout: ")
        // execute on IO thread cuz there are some processing
        GlobalScope.launch(IO){
            var errorMessage: String? = null
            try {
                _cachedToken.value!!.account_pk?.let {
                    Log.d(TAG, "logout: nullifying $it")
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException){
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message
            } catch (e: Exception){
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.e(TAG, "error finally: $errorMessage" )
                }
                Log.d(TAG, "logout: finally.....")
                setValue(null)
            }
        }
    }

    fun setValue(authToken: AuthToken?) {
        //livedata value must be set on main thread
        GlobalScope.launch(Main) {
            if (_cachedToken.value != authToken){
                _cachedToken.value = authToken
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isConnectedToInternet(): Boolean {
        try {
            val cm: ConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkInfo = cm.activeNetwork ?: return false
                val actNw = cm.getNetworkCapabilities(networkInfo) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    else -> false
                }
            } else {
                val networkInfo = cm.activeNetworkInfo ?: return false
                return networkInfo.isConnected
            }
        } catch (e: Exception){
            Log.e(TAG, "isConnectedToInternet: ${e.message}" )
        }
        return false
    }
}