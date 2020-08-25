package com.example.mvvmapplication.di

import android.app.Application
import com.example.mvvmapplication.BaseApplication
import com.example.mvvmapplication.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        ActivityBuilderModule::class
    ]
)
interface AppComponent: AndroidInjector<BaseApplication> {
    val sessionManager: SessionManager // this session manager must be added here b/c it is injected into abstract class AuthDatabase. This can be injected into everywhere

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}