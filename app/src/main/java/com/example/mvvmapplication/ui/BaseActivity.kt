package com.example.mvvmapplication.ui

import android.util.Log
import com.example.mvvmapplication.session.SessionManager
import com.example.mvvmapplication.util.displayDialogError
import com.example.mvvmapplication.util.displayToast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity: DaggerAppCompatActivity(), DataStateChangedListener {
    val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChanged(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main){
                displayProgressbar(it.loading.isLoading)

                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                it.data?.let {
                    it.response?.let{ responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }
            }
        }
    }

    private fun handleStateResponse(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when(it.responseType){
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displayDialogError(message)
                    }
                }
                is ResponseType.None -> {
                    it.message?.let{message ->
                        Log.d(TAG, "handleStateResponse: $message")
                    }
                }
            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>){
        errorEvent.getContentIfNotHandled()?.let {
            when(it.response.responseType){
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayDialogError(message)
                    }
                }
                is ResponseType.None -> {
                    it.response.message?.let{message ->
                        Log.e(TAG, "handleStateError: $message")
                    }
                }
            }
        }
    }

    abstract fun displayProgressbar(loading: Boolean)
}