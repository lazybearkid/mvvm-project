package com.example.mvvmapplication.di.main

import androidx.lifecycle.ViewModel
import com.example.mvvmapplication.di.ViewModelKey
import com.example.mvvmapplication.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract                                                                                                                                                                                        class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}