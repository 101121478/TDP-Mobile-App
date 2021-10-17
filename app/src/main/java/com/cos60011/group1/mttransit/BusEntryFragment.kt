package com.cos60011.group1.mttransit

import com.cos60011.group1.mttransit.databinding.FragmentBusEntryBinding
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap



class BusEntryFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var viewModel: SharedViewModel


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View {
        val binding = FragmentBusEntryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        println("USER LOCATION: " + viewModel.userLocation.value)

        binding.submitButton.setOnClickListener { view: View ->
            val busIDInput = binding.busIdInput
            val busCapacityInput = binding.passengerCapacityInput
            val passengersOnboardInput = binding.passengersOnboardInput

            val busID = binding.busIdInput.text.toString()
            val busType = binding.busTypeSpinner.selectedItem.toString()
            val route = binding.routeSpinner.selectedItem.toString()
            val busCapacity = binding.passengerCapacityInput.text.toString()
            val passengersOnboard = binding.passengersOnboardInput.text.toString()

            //Get date as string in format yyyy-mm-dd for use in creating date document in BusOperation
            val dateCollection = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


            // TODO: More Data Validation?
            // Data Validation
            if(busID.isNotEmpty()){
                if(busCapacity.isNotEmpty()){
                    val capacity = Integer.parseInt(busCapacity)
                    if(capacity > 0) {
                        if(passengersOnboard.isNotEmpty()){
                            val passengers = Integer.parseInt(passengersOnboard)
                            if(passengers <= capacity){
                                println("ALL INPUTS VALID")

                                // Check if device has internet connection. If not then display alert informing user
                                if(!checkInternetConnection()){
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("No Internet Connection")
                                    builder.setMessage("Data will be stored when connection is re-established. Do not close the app otherwise data will be lost.")
                                    builder.setPositiveButton("OK"){_,_ ->
                                        view.findNavController().navigate(R.id.action_busEntryFragment_to_busBoardFragment)
                                    }
                                    builder.show()
                                }

                                // TODO: Check TODOs in function
                                // TODO: Double check that all directories are made correctly and that data stored is also in correct structure
                                storeBusData(busID, busType, route, capacity, passengers, dateCollection, view)

                                //storeBusData(busID, busType, route, capacity, passengers, view)
                            } else {
                                passengersOnboardInput.error = "The number of passengers cannot exceed the bus capacity"
                            }
                        } else {
                            passengersOnboardInput.error = "Passengers onboard not specified"
                        }
                    } else {
                        busCapacityInput.error = "Bus capacity must be greater than 0"
                    }
                } else {
                    busCapacityInput.error = "Bus capacity not specified"
                }
            } else {
                busIDInput.error = "No Bus ID entered"
            }
        }
        return binding.root
    }

    private fun storeBusData(busID: String, busType: String, route: String, busCapacity: Int, passengersOnboard: Int, dateCollection: String, view: View) {
        // TODO: Improve logic of confirmation message for bus being added to database

        val selectedStation = viewModel.userLocation.value  // Get the station that was selected by the user in the SetStationFragment through SharedViewModel

        // TODO: Get actual data and replace placeholders on database structure is finished
        var nextStop = ""
        //val placeholderPreviousStop = ""

        // TODO: Update routeID, stationID and stationName?
        // Hash map of bus routes to bus route IDs
        val busRoutes = hashMapOf(
            "Sandringham To CBD" to "sandringhamToCBD",
            "Frankston From CBD" to "frankstonFromCBD",
            "Frankston To CBD" to "frankstonToCBD",
            "Sandringham From CBD" to "sandringhamFromCBD"
        )
        val routeId = busRoutes[route]  // Use the selected route to get the associated bus route ID from bus routes map


        // Maps of data for the documents that have multiple fields. To be stored in appropriate documents
        val arrivalTimeMap = hashMapOf(
            "stationName" to "$selectedStation",
            "stopNum" to 1,
            "arrivalTime" to Timestamp(Date())
        )

        val passengerCountMap = hashMapOf(
            "stationID" to selectedStation,
            "stationName" to selectedStation?.split(" ")?.get(0),
            "stopNum" to 1,
            "count" to passengersOnboard
        )

        val busOperationDataFields = hashMapOf(
            "BusID" to busID,
            "BusType" to busType,
            "Capacity" to busCapacity,
            "routeId" to "$routeId"
        )

        // Data Store Operation 1
        // Store bus data in BusOperations collection
        // Will also create the directory step by step if it or parts of it does not exist
        db.collection("BusOperation").document("dates")
            .collection(dateCollection).document("${busRoutes[route]}_${busID}")
            .set(HashMap<String, Any>())
            .addOnSuccessListener {
                println("BusOperation Date document successfully written!")
                // CREATE ArrivalTime COLLECTION
                db.collection("BusOperation").document("dates")
                    .collection(dateCollection).document("${busRoutes[route]}_${busID}")
                    .collection("ArrivalTime").document("$routeId")
                    .set(arrivalTimeMap)
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE DepartureTime DOCUMENT
                db.collection("BusOperation").document("dates")
                    .collection(dateCollection).document("${busRoutes[route]}_${busID}")
                    .collection("DepartureTime").document("$routeId")
                    .set(HashMap<String, Any>())
                    .addOnSuccessListener {
                        println("DepartureTime document successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing DepartureTime document", exception)
                    }
                // CREATE PassengerCount DOCUMENT
                db.collection("BusOperation").document("dates")
                    .collection(dateCollection).document("${busRoutes[route]}_${busID}")
                    .collection("PassengerCount").document("$selectedStation")
                    .set(passengerCountMap)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // Add remaining bus data fields
                db.collection("BusOperation").document("dates")
                    .collection(dateCollection).document("${busRoutes[route]}_${busID}")
                    .set(busOperationDataFields)
                    .addOnSuccessListener {
                        println("Capacity document successfully written!")
                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing Capacity document", exception)
                    }
            }
            .addOnFailureListener {
                    exception -> Log.w("Error writing BusOperation Date document", exception)
            }

        // Data Store Operation 2
        // Store bus data in busArchive document in StationOperation
        // Will also create the directory step by step if it or parts of it does not exist
        // CREATE initial date document in StationOperation if not created
        db.collection("StationOperation").document(dateCollection)
            .set(HashMap<String, Any>())
            .addOnSuccessListener {

                println("StationOperation Date document successfully written!")
                // CREATE station collection for the station under the date document and create busArchive document in the station collection
                db.collection("StationOperation").document(dateCollection)
                    .collection("$selectedStation").document("busArchive")
                    .set(HashMap<String, Any>())
                    .addOnSuccessListener {
                        println("busArchive document successfully written!")

                        // Peform a small get operation to get the name of the next station in the buses route
                        db.collection("RouteOperation").document("$routeId")
                            .collection("Stops").document("2")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    nextStop = document?.get("stationName").toString()

                                    // Create data class of initial bus data to store in busArchive
                                    val bus = Bus(busId = busID, capacity = busCapacity, currentStop = "$selectedStation", nextStop = nextStop, passengers = passengersOnboard,routeId = "$routeId", routeName = route)

                                    // Store the bus data in the busArchive document
                                    db.collection("StationOperation").document(dateCollection)
                                        .collection("$selectedStation").document("busArchive")
                                        .collection("busesAtStop").document()
                                        .set(bus)
                                        .addOnSuccessListener {
                                            println("Bus data successfully written!")
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
                                                exception -> Log.w("Error writing Bus data", exception)
                                        }
                                }
                            }
                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing busArchive document", exception)
                    }
            }
            .addOnFailureListener {
                    exception -> Log.w("Error writing StationOperation Date document", exception)
            }

        // Perform small get operation to get the total buses
        db.collection("DataVisualisation").document("TotalBusesCount")
            .get()
            .addOnCompleteListener { task ->
                val document = task.result
                var count = document?.getLong("count")?.toInt()
                println("COUNT: " + count)

                if(count != null) {
                    count += 1

                    // Add plus 1 to the total buses count for the data visualisation statistics
                    db.collection("DataVisualisation").document("TotalBusesCount")
                        .set(hashMapOf(
                            "count" to count
                        ))
                }


            }
    }

    // Function to check if the device has an internet connection either over wifi or mobile data
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if(capabilities != null) {
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                return true
            }
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                return true
            }
        }
        return false
    }
}