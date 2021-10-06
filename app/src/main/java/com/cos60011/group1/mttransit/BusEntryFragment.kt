package com.cos60011.group1.mttransit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.cos60011.group1.mttransit.databinding.FragmentBusEntryBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class BusEntryFragment : Fragment() {

    private val projectFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        val binding = FragmentBusEntryBinding.inflate(inflater, container, false)


        binding.submitButton.setOnClickListener { view: View ->
            val busID = binding.busIdInput.text.toString()
            val busType = binding.busTypeInput.text.toString()
            val route = binding.routeSelectionSpinner.selectedItem.toString()
            val busCapacity = Integer.parseInt(binding.passengerCapacityInput.text.toString())
            val passengersOnboard = Integer.parseInt(binding.passengersOnboardInput.text.toString())

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
                "busType" to busType,
                "capacity" to busCapacity,
                "departureTime" to emptyMap<String, String>(),
                "passengerCount" to emptyMap<String, Int>(),
                "routeID" to routeRef
            )

            println("BUS ID: " + busID)
            println("BUS Type: " + busType)
            println("BUS ROUTE: " + busRoutes.get(route))
            println("BUS CAPACITY: " + busCapacity)
            println("PASSENGERS ONBOARD: " + passengersOnboard)

            //Store the new bus as a document in the buses collection in Firestore project
            projectFirestore.collection("buses").document(busID)
                .set(bus)
                .addOnSuccessListener { println("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Error writing document", e) }
        }
        return binding.root
    }

}