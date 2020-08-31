package com.example.mvvmapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.mvvmapplication.R
import com.example.mvvmapplication.ui.BaseActivity
import com.example.mvvmapplication.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: BaseActivity() {
    private val TAG: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupUI()
        subscribeObservers()
    }

    private fun setupUI() {
        tool_bar.setOnClickListener {
            sessionManager.logout()
        }
    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer {authToken ->
            Log.d(TAG, "subscribeObservers: subscribeObservers: AuthToken $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }


}