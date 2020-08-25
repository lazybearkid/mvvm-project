package com.example.mvvmapplication

import com.example.mvvmapplication.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class BaseApplication: DaggerApplication(){
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

}