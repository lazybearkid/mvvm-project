package com.example.mvvmapplication.di.auth

import com.example.mvvmapplication.ui.auth.ForgotPasswordFragment
import com.example.mvvmapplication.ui.auth.LauncherFragment
import com.example.mvvmapplication.ui.auth.LoginFragment
import com.example.mvvmapplication.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuilderModule {
    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment
}