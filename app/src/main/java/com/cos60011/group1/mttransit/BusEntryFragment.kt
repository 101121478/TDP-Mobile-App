package com.cos60011.group1.mttransit

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.cos60011.group1.mttransit.databinding.FragmentBusEntryBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


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
            val initArrivalTime = Timestamp(Date())

            val busRoutes = hashMapOf(
                "Sandringham To CBD" to "/routes/sandringhamToCBD",
                "Frankston From CBD" to "/routes/frankstonFromCBD",
                "Frankston To CBD" to "/routes/frankstonToCBD",
                "Sandringham From CBD" to "/routes/sandringhamFromCBD"
            )
            val routeRef: DocumentReference = projectFirestore.document(busRoutes.get(route)!!)     //Create a document reference of the bus route
            // Hashmap of bus info that will be stored in Firestore
            // TODO: Update hashMap to new data structure
            // TODO: Get the user selected station and store in the hashmap as initial bus location
            val bus = hashMapOf(
                "arrivalTime" to hashMapOf(
                    "time" to initArrivalTime,
                ),
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


            // Check if device has internet connection. If not then display alert informing user
            if(!checkInternetConnection()){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("No Internet Connection")
                builder.setMessage("Data will be stored when connection is re-established")
                builder.setPositiveButton("OK"){_,_ ->
                    view.findNavController().navigate(R.id.action_busEntryFragment_to_busBoardFragment)
                }
                builder.show()
            }

            //Store the new bus as a document in the buses collection in Firestore project
            //TODO: Update code to set the correct data based on the new database structure in Firestore
            projectFirestore.collection("buses").document(busID)
                .set(bus)
                .addOnSuccessListener {
                    println("DocumentSnapshot successfully written!")
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Successfully added bus $busID")
                    builder.setPositiveButton("OK"){_,_ ->
                        if(this.isVisible) {
                            view.findNavController().navigate(R.id.action_busEntryFragment_to_busBoardFragment)
                        }
                    }
                    builder.show()
                }
                .addOnFailureListener {
                        exception -> Log.w("Error writing document", exception)
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Could not add bus $busID")
                    builder.setMessage("Error writing document $exception")
                    builder.setPositiveButton("OK"){_,_ -> }
                    builder.show()
                }
        }
        return binding.root
    }

    // Function to check if the device has an internet conenction either over wifi or mobile data
    fun checkInternetConnection(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            var capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if(capabilities != null) {
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true
                }
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    return true
                }
            }
        }
        return false
    }

}