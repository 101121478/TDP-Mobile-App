package com.cos60011.group1.mttransit.screens.busstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BusStatusViewModelFactory(private val stationRef: String, private val busRef: String,
                                private val routeRef: String, private val isCurrentBus: Boolean,
                                private val isRecentBus: Boolean) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusStatusViewModel::class.java)) {
            // TODO Construct and return the ScoreViewModel
            return BusStatusViewModel(stationRef, busRef, routeRef, isCurrentBus, isRecentBus) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}