package com.example.mvvmapplication.ui

data class DataState<T>(
    val error: Event<StateError>?, // wrapped into Event class -> data is only observed once
    val loading: Loading = Loading(false),
    var data: Data<T>? = null // all paramaters are wrapped into Event class -> data is only observed once
){
    companion object{
        // for different cases
        fun <T> error(response: Response): DataState<T>{
            return DataState(
                error = Event(
                    StateError(response)
                )
            )
        }

        fun<T> loading(isLoading: Boolean, cachedData: T? = null): DataState<T>{
            return DataState(
                error = null,
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(cachedData),
                    null
                )
            )
        }

        /**
         * params:  data: actual data
         *          response: message received
         */
        fun<T> data(data: T? = null, response: Response? = null): DataState<T>{
            return DataState(
                error = null,
                loading = Loading(false),
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response)
                )
            )
        }
    }
}