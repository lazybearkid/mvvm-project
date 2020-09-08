package com.example.mvvmapplication.ui.main.create_blog

import android.content.Context
import android.util.Log
import com.example.mvvmapplication.ui.DataStateChangedListener
import dagger.android.support.DaggerFragment
import kotlin.ClassCastException

abstract class BaseCreateBlogFragment: DaggerFragment() {
    val TAG: String = "AppDebug"

    lateinit var dataStateChangedListener: DataStateChangedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateChangedListener = context as DataStateChangedListener
        } catch (e: java.lang.ClassCastException){
            Log.e(TAG, "onAttach: $context class must implement DataStateChangedListener")
        }
    }

}