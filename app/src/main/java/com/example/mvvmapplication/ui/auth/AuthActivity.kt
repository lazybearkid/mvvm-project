package com.example.mvvmapplication.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mvvmapplication.R
import dagger.android.support.DaggerAppCompatActivity

class AuthActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}