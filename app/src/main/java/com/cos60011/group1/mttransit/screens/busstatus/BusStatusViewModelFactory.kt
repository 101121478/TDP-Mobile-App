package com.cos60011.group1.mttransit.screens.busstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BusStatusViewModelFactory(private val stationRef: String, private val busRef: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusStatusViewModel::class.java)) {
            // TODO Construct and return the ScoreViewModel
            return BusStatusViewModel(stationRef, busRef) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}