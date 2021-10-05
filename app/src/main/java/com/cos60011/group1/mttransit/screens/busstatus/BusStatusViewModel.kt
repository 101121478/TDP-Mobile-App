package com.cos60011.group1.mttransit.screens.busstatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class BusStatusViewModel(document: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val docRef: DocumentReference

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

    private val _isUpdate = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean>
        get() = _isUpdate

    init {
        docRef = db.collection("test_bus_status_view").document(document)
        getBusDocument()
    }

    private fun getBusDocument() {
        val source = Source.SERVER

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

    fun updateDocument() {
        docRef
            .update("arriveTime", Timestamp.now())
            .addOnSuccessListener { _isUpdate.value = true }
            .addOnFailureListener { _isUpdate.value = false }
    }
}