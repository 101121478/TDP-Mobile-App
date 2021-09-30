package com.cos60011.group1.mttransit.firestore


class testFirestore {


    fun main(args: Array<String>){

        FirestoreClass().setPassengerCount("frankstonToCBD_B2", "armadale", 25 )

        val test1 = FirestoreClass().getBusDocument("frankstonToCBD_B2")
        print(test1)

    }


}