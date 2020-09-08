package com.example.mvvmapplication.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.example.mvvmapplication.R
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.DataStateChangedListener
import com.example.mvvmapplication.ui.Response
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.ui.auth.ForgotPasswordFragment.WebAppInterface.OnWebInteractionCallback
import com.example.mvvmapplication.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ForgotPasswordFragment : BaseAuthFragment() {
    lateinit var webView: WebView
    lateinit var stateChangeChangedListener: DataStateChangedListener

    val webInteractionCallback: OnWebInteractionCallback = object: OnWebInteractionCallback{
        override fun onSuccess(email: String) {
            Log.d(TAG, "onSuccess: a reset link has been sent to  the email")
            onPasswordLinkSent()
        }

        override fun onError(errorMessage: String) {
            Log.e(TAG, "onError: when interact with webview")
            val dataStateError = DataState.error<Any>(
                response = Response(
                    message = errorMessage,
                    responseType = ResponseType.Dialog()
                )
            )
            stateChangeChangedListener.onDataStateChanged(dataStateError)
        }

        override fun onLoading(isLoading: Boolean) {
            val dataStateLoading = DataState.loading(
                isLoading = isLoading,
                cachedData = null
            )
            GlobalScope.launch(Main){
                stateChangeChangedListener.onDataStateChanged(dataStateLoading)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        Log.d(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")

        loadPasswordResetView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetView(){
        //show the loading progressbar waiting for the page to be fully loaded
        stateChangeChangedListener.onDataStateChanged(DataState.loading(isLoading = true, cachedData = null))

        webview.webViewClient = object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //hide the loading progressbar when the page is fully loaded
                stateChangeChangedListener.onDataStateChanged(DataState.loading(isLoading = false, cachedData = null))
            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        //add a javascript interface
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallback), "AndroidTextListener")
    }

    fun onPasswordLinkSent(){
        GlobalScope.launch(Main){
            parent_view.removeView(webView)
            webView.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )
            animation.duration = 500
            password_reset_done_container.animation = animation
            password_reset_done_container.visibility = View.VISIBLE
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeChangedListener = context as DataStateChangedListener
        } catch (e: ClassCastException){
            Log.e(TAG, "onAttach: activity must implement DataStateChangeListener" )
        }
    }

    class WebAppInterface
    constructor(
        val callback: OnWebInteractionCallback
    ) {
        private val TAG: String = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String){
            callback.onSuccess(email)
        }
        @JavascriptInterface
        fun onError(errorMessage: String){
            callback.onError(errorMessage)
        }
        @JavascriptInterface
        fun onLoading(isLoading: Boolean){
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallback {
            fun onSuccess(email: String)
            fun onError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }
    }
}