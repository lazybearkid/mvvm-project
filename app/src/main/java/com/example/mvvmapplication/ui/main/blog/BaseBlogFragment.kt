package com.example.mvvmapplication.ui.main.blog

import android.content.Context
import android.util.Log
import com.example.mvvmapplication.ui.DataStateChangedListener
import dagger.android.support.DaggerFragment
import java.lang.ClassCastException

abstract class BaseBlogFragment: DaggerFragment() {
    val TAG: String = "AppDebug"

    lateinit var dataStateChangedListener: DataStateChangedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateChangedListener = context as DataStateChangedListener
        } catch (e: ClassCastException){
            Log.e(TAG, "onAttach: $context class must implement DataStateChangedListener")
        }
    }
}