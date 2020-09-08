package com.example.mvvmapplication.di

import com.example.mvvmapplication.di.auth.AuthFragmentBuilderModule
import com.example.mvvmapplication.di.auth.AuthModule
import com.example.mvvmapplication.di.auth.AuthScope
import com.example.mvvmapplication.di.auth.AuthViewModelModule
import com.example.mvvmapplication.di.main.MainFragmentBuilderModule
import com.example.mvvmapplication.di.main.MainModule
import com.example.mvvmapplication.di.main.MainScope
import com.example.mvvmapplication.di.main.MainViewModelModule
import com.example.mvvmapplication.ui.auth.AuthActivity
import com.example.mvvmapplication.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuilderModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuilderModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}