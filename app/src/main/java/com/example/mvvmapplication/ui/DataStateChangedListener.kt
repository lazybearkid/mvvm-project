package com.example.mvvmapplication.ui

interface DataStateChangedListener {
    fun onDataStateChanged(dataState: DataState<*>?)
}