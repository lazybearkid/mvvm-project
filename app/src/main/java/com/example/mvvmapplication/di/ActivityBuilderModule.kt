package com.example.mvvmapplication.di

import com.example.mvvmapplication.di.auth.AuthFragmentBuilderModule
import com.example.mvvmapplication.di.auth.AuthModule
import com.example.mvvmapplication.di.auth.AuthScope
import com.example.mvvmapplication.di.auth.AuthViewModelModule
import com.example.mvvmapplication.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuilderModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}