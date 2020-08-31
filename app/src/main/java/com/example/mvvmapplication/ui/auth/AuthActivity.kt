package com.example.mvvmapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmapplication.R
import com.example.mvvmapplication.ui.BaseActivity
import com.example.mvvmapplication.ui.main.MainActivity
import com.example.mvvmapplication.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    private val TAG: String = "AppDebug"
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authViewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        subscribeObservers()
    }

    private fun subscribeObservers() {
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
}