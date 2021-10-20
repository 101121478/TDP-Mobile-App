package com.cos60011.group1.mttransit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String>
        get() = _userLocation

    private var _currentBus = MutableLiveData<String>()
    val currentBus: LiveData<String>
        get() = _currentBus

    private var _routeName = MutableLiveData<String>()
    val routeName: LiveData<String>
        get() = _routeName

    private var _isCurrentBus = MutableLiveData<Boolean>()
    val isCurrentBus: LiveData<Boolean>
        get() = _isCurrentBus

    private var _isRecentBus = MutableLiveData<Boolean>()
    val isRecentBus: LiveData<Boolean>
        get() = _isRecentBus

    init {
        _userLocation.value = "Unknown"
        _currentBus.value = "Testing adapter viewmodel access"
    }

    fun setRouteName(routeName: String) {
        _routeName.value = routeName
    }

    fun setIsCurrentBus(isCurrentBus: Boolean) {
        _isCurrentBus.value = isCurrentBus
    }

    fun setIsRecentBus(isRecentBus: Boolean) {
        _isRecentBus.value = isRecentBus
    }

    fun setLocation(newLocation: String) {
        _userLocation.value = newLocation
    }

    fun setCurrentBus(currentBusId: String) {
        _currentBus.value = currentBusId
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("SharedViewModel", "SharedViewModel destroyed")
    }


}