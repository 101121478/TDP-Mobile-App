package com.cos60011.group1.mttransit.screens.busstatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class BusStatusViewModel(documentRef: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _busID = MutableLiveData<String>()
    val busID: LiveData<String>
        get() = _busID

    private val _busType = MutableLiveData<String>()
    val busType: LiveData<String>
        get() = _busType

    private val _busRoute = MutableLiveData<String>()
    val busRoute: LiveData<String>
        get() = _busRoute

    private val _passengerCapacity = MutableLiveData<String>()
    val passengerCapacity: LiveData<String>
        get() = _passengerCapacity

    private val _passengerOnBoard = MutableLiveData<String>()
    val passengerOnBoard: LiveData<String>
        get() = _passengerOnBoard

    init {
        getBusDocument(documentRef)
    }

    private fun getBusDocument(documentRef: String) {
        val docRef = db.collection("test_bus_status_view").document(documentRef)

        val source = Source.DEFAULT

        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                _busID.value = document?.get("busID").toString()
                _busType.value = document?.get("busType").toString()
                _busRoute.value = document?.get("busRoute").toString()
                _passengerCapacity.value = document?.get("capacity").toString()
                _passengerOnBoard.value = document?.get("passengerCount").toString()
                _busType.value = document?.get("busType").toString()
            } else {
                // TODO Handel Failure
            }
        }
    }
}