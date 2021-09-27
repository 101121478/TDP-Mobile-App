package com.cos60011.group1.mttransit

data class Bus (
    val id: String,
    val route: String,
    val location: String,
    val capacity: Int,
    val passengers: Int,
    val arrival: String,
    val departure: String
    ) {

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