package com.cos60011.group1.mttransit

import com.google.firebase.Timestamp

//Default values are set so Kotlin will automatically create an empty constructor, which is needed for Firestore data mapping
data class Bus(
    val arrivalTime: Timestamp = Timestamp.now(), // For display in dashboard cards
    val busId: String = "Test Bus",
    val capacity: Int = 1,
    val currentStop: String = "STOP_UNKNOWN_CS",
    val currentStopNo: Int = 1,
    val departureTime: Timestamp = Timestamp.now(), // For display in dashboard cards
    val active: Boolean = true,
    val lastUpdated: Timestamp = Timestamp.now(), //Not sure if this is necessary anymore
    val nextStop: String = "STOP_UNKNOWN_NS", // Need this for incoming buses query
    val passengers: Int = 1,
    val previousStop: String = "STOP_UNKNOWN_PS", // Need this for eventual 'recently edited buses' query
    val routeId: String = "ID_UNKNOWN",
    val routeName: String = "ROUTE_UNKNOWN"
)