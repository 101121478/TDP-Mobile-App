package com.cos60011.group1.mttransit

import com.google.firebase.Timestamp
import kotlin.collections.ArrayList

//Default values are set so Kotlin will automatically create an empty constructor, which is needed for Firestore data mapping
data class Bus(
    val name: String = "Mario Kart",
    val route: String = "Rainbow Road",
    val location: String = "Mario World",
    val lastUpdated: Timestamp = Timestamp.now(),
    val capacity: Int = 1,
    val passengers: Int = 1,
    val arrival: String = "0000",
    val departure: String = "9999"
) {

    //Generates test data for recycler view elements
    companion object {
        private var lastbusId = 100
        fun createTestBuses(numBuses: Int): ArrayList<Bus> {
            val buses = ArrayList<Bus>()
            for (i in 1..numBuses) {
                buses.add(
                    Bus(
                        "Bus " + ++lastbusId,
                        "Belgrave to Flinders",
                        "Richmond",
                        Timestamp.now(),
                        70,
                        32,
                        "11:51am",
                        "9pm"
                    )
                )
            }
            return buses
        }
    }
}