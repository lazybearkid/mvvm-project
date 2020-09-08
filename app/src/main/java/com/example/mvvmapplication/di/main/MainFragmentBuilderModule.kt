package com.example.mvvmapplication.di.main

import com.example.mvvmapplication.ui.main.account.AccountFragment
import com.example.mvvmapplication.ui.main.account.ChangePasswordFragment
import com.example.mvvmapplication.ui.main.account.UpdateAccountFragment
import com.example.mvvmapplication.ui.main.blog.BlogFragment
import com.example.mvvmapplication.ui.main.blog.UpdateBlogFragment
import com.example.mvvmapplication.ui.main.blog.ViewBlogFragment
import com.example.mvvmapplication.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuilderModule {
    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment
}