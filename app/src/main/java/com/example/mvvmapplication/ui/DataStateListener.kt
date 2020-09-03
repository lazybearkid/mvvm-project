package com.example.mvvmapplication.ui

interface DataStateListener {
    fun onDataStateChanged(dataState: DataState<*>?)
}