package com.cos60011.group1.mttransit.firestore

import android.util.Log
import android.util.TimeUtils
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

abstract class FirestoreClass {

    private val projectFirestore = FirebaseFirestore.getInstance()
    private lateinit var exception: Exception



    fun getBusDocument(busDocumentId: String): String {
        val busID = busDocumentId
        val busDocument = projectFirestore.collection("buses").document(busID)

        busDocument.get()
            .addOnSuccessListener { busDocument ->
                if (busDocument != null) {
                    Log.d(Companion.TAG, "DocumentSnapshot data: ${busDocument.data}")
                } else {
                    Log.d(Companion.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with, ", exception)

            }

        return busDocument.toString()
    }

    fun getStation(stationDocumentId: String): String {
        val stationID = stationDocumentId
        val stationDocument = projectFirestore.collection("railwayStations").document(stationID)

        stationDocument.get()
            .addOnSuccessListener { busDocument ->
                if (stationDocument != null) {
                    Log.d(Companion.TAG, "DocumentSnapshot data: ${busDocument.data}")
                } else {
                    Log.d(Companion.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with, ", exception)

            }

        return stationDocument.toString()
    }

    fun getRouteDocument(routeDocumentId: String): String {
        val routeID = routeDocumentId
        val routeDocument = projectFirestore.collection("routes").document(routeID)

        routeDocument.get()
            .addOnSuccessListener { busDocument ->
                if (busDocument != null) {
                    Log.d(Companion.TAG, "DocumentSnapshot data: ${busDocument.data}")
                } else {
                    Log.d(Companion.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with, ", exception)

            }

        return routeDocument.toString()
    }

   fun setArrivalTime(busID: String, stationID: String, arrivalTime: TimeUtils) {

       val busID = busID
       val stationID = stationID
       val arrivalTime = arrivalTime

       projectFirestore.collection("buses").document(busID)
           .update(mapOf(
               "arrivalTime.station" to stationID,
               "arrivalTime.time" to arrivalTime
           ))
   }

    fun setDepartureTime(busID: String, stationID: String, departureTime: TimeUtils) {

        val busID = busID
        val stationID = stationID
        val departureTime = departureTime

        projectFirestore.collection("buses").document(busID)
            .update(mapOf(
                "departureTime.station" to stationID,
                "departureTime.time" to  departureTime
            ))
    }

    fun setPassengerCount(busID: String, stationID: String, passengerFinalCount: Int){
        val busID = busID
        val stationID = stationID
        val passengerFinalCount = passengerFinalCount

        projectFirestore.collection("buses").document(busID)
            .update(mapOf(
                "count.numberOfPassenger" to passengerFinalCount,
                "count.station" to stationID
            ))
    }


    companion object {
        private const val TAG = "MyActivity"
    }


}