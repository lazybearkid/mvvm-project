package com.example.mvvmapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mvvmapplication.ui.DataState
import com.example.mvvmapplication.ui.Response
import com.example.mvvmapplication.ui.ResponseType
import com.example.mvvmapplication.util.*
import com.example.mvvmapplication.util.Constants.Companion.NETWORK_TIMEOUT
import com.example.mvvmapplication.util.Constants.Companion.TEST_CACHE_DELAY
import com.example.mvvmapplication.util.Constants.Companion.TEST_NETWORK_DELAY
import com.example.mvvmapplication.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.example.mvvmapplication.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mvvmapplication.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WITHOUT_INTERNET
import com.example.mvvmapplication.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.math.log

@OptIn(InternalCoroutinesApi::class)
abstract class NetworkBoundResource<ResponseObject, ViewStateType>(
    isNetWorkAvailable: Boolean,
    isNetWorkRequest: Boolean
) {
    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (isNetWorkRequest){

            if (isNetWorkAvailable){
                coroutineScope.launch {
                    delay(TEST_NETWORK_DELAY)
                    withContext(Main){
                        //make network call
                        val apiResponse = createCall()
                        result.addSource(apiResponse){ response ->
                            result.removeSource(apiResponse)
                            coroutineScope.launch {
                                handleNetworkCall(response)
                            }
                        }
                    }
                }
                GlobalScope.launch(IO){
                    delay(NETWORK_TIMEOUT)
                    if (!job.isCompleted){
                        Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT: ")
                        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                    }
                }
            } else {
                onErrorReturn(UNABLE_TODO_OPERATION_WITHOUT_INTERNET, shouldUseDialog = true, shouldUseToast = false)
            }
        } else { //if the request does not need network
            coroutineScope.launch {
                delay(TEST_CACHE_DELAY) //fake delay for cache testing

                //view data from cache only and return
                createCacheRequestAndReturn()
            }
        }
    }

    abstract suspend fun createCacheRequestAndReturn()

    private suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when(response){
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, "handleNetworkCall: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "handleNetworkCall: request returned nothing 204")
                onErrorReturn("204. Return nothing", true, false)
            }

        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main){
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (msg == null){
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)){
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }

        if (shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if (shouldUseDialog){
            responseType = ResponseType.Dialog()
        }

        //complete job and emit dataState
        onCompleteJob(DataState.error(
            response = Response(
                message = msg,
                responseType = responseType
            )
        ))

    }

    private fun initNewJob(): Job{
        Log.d(TAG, "initNewJob: .....")
        job = Job()
        // this function is called when job is canceled or completed
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object: CompletionHandler{
            override fun invoke(cause: Throwable?) {
                if (job.isCancelled){
                    Log.e(TAG, "invoke: networkBoundResource: job has been canceled")
                    cause?.let {
                        Log.e(TAG, "invoke: because of ", it)
                        onErrorReturn(it.message, shouldUseDialog = false, shouldUseToast = true)
                    }?: onErrorReturn(ERROR_UNKNOWN, shouldUseDialog = false, shouldUseToast = true)
                } else if(job.isCompleted){
                    Log.e(TAG, "invoke: NetworkBoundResource: job has been complete")
                    //Do nothing
                }
            }

        })
        coroutineScope = CoroutineScope(IO + job) //make a specific coroutine scope inside background thread so that job can be canceled separately

        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job: Job)
}