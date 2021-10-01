package com.cos60011.group1.mttransit.firestore

import android.util.Log
import android.util.TimeUtils
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import java.lang.Exception



class FirestoreClass {

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

    fun createNewBus(busID: String, busType: String, route: String, capacity: Int, passengerCount: Int){
        val busRoutes = hashMapOf(
            "Sandringham To CBD" to "/routes/sandringhamToCBD",
            "Frankston From CBD" to "/routes/frankstonFromCBD",
            "Frankston To CBD" to "/routes/frankstonToCBD",
            "Sandringham From CBD" to "/routes/sandringhamFromCBD"
        )
        val routeRef: DocumentReference = projectFirestore.document(busRoutes.get(route)!!)     //Create a document reference of the bus route
        // Hashmap of bus info that will be stored in Firestore
        val bus = hashMapOf(
            "arrivalTime" to emptyMap<String, String>(),
            "busLocation" to emptyMap<String, String>(),
            "capacity" to capacity,
            "departureTime" to emptyMap<String, String>(),
            "passengerCount" to emptyMap<String, Int>(),
            "routeID" to routeRef
        )

        println("BUS ID: " + busID)
        println("BUS Type: " + busType)
        println("BUS ROUTE: " + busRoutes.get(route))
        println("BUS CAPACITY: " + capacity)
        println("PASSENGERS ONBOARD: " + passengerCount)

        //Store the new bus as a document in the buses collection in Firestore project
        projectFirestore.collection("buses").document(busID)
            .set(bus)
            .addOnSuccessListener { println("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Error writing document", e) }
    }


    companion object {
        private const val TAG = "MyActivity"
    }


}