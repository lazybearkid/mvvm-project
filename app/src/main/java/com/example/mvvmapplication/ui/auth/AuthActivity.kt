package com.example.mvvmapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.mvvmapplication.R
import com.example.mvvmapplication.ui.BaseActivity
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.ui.main.MainActivity
import com.example.mvvmapplication.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : BaseActivity(),
    NavController.OnDestinationChangedListener{
    private val TAG: String = "AppDebug"
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)

        subscribeObservers()
    }

    private fun subscribeObservers() {
        authViewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let { data ->
                data.data?.let{event ->
                    event.getContentIfNotHandled()?.let {viewState ->
                        viewState.authToken?.let { token ->
                            authViewModel.setAuthToken(token)
                        }
                    }
                }
                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when(it.responseType){
                            is ResponseType.Dialog -> {
                                //show a dialog
                            }
                            is ResponseType.Toast -> {
                                //show a toast
                            }
                            is ResponseType.None -> {
                                Log.e(TAG, "Authentication, response: ${it.message}")
                            }
                        }
                    }
                }
            }
        })


        authViewModel.viewState.observe(this, Observer {
            it.authToken?.let{
                Log.d(TAG, "subscribeObservers: loggin in")
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer {authToken ->
            Log.d(TAG, "subscribeObservers: ")
            if ( authToken != null && authToken.account_pk != -1 && authToken.token != null){
                navMainActivity()
            }
        })
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }
}