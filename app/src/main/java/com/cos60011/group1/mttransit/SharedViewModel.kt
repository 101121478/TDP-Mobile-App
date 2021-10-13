package com.cos60011.group1.mttransit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String>
        get() = _userLocation

    init {
        _userLocation.value = "Unknown"
    }


    fun setLocation(newLocation: String) {
        _userLocation.value = newLocation

    }
    override fun onCleared() {
        super.onCleared()
        Log.i("SharedViewModel", "SharedViewModel destroyed")
    }


}