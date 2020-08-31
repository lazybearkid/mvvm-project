package com.example.mvvmapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<StateEvent, ViewState>: ViewModel() {
    //StateEvent: when trigger an event
    //ViewState: update view
    //DataState: used to observe event to set values to viewState

    val TAG: String = "AppDebug"

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val stateEvent: LiveData<StateEvent>
        get() = _stateEvent
    val viewState: LiveData<ViewState>
        get() = _viewState

    protected val _dataState: LiveData<DataState<ViewState>> = Transformations.switchMap(_stateEvent){stateEvent ->
        stateEvent?.let {
            handleStateEvent(stateEvent)
        }
    }

    //events fired by users or automatically
    fun setStateEvent(stateEvent: StateEvent){
        _stateEvent.value = stateEvent
    }



    fun getCurrentNewStateOrNew(): ViewState{
        val value = _viewState.value?.let {
            it
        }?: initNewViewState()
        return value
    }

    abstract fun initNewViewState(): ViewState

    //different viewmodel has different stateEvent and viewstate
    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>
}