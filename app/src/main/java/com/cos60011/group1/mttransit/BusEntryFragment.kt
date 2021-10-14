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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap



class BusEntryFragment : Fragment() {

    private val projectFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View {
        val binding = FragmentBusEntryBinding.inflate(inflater, container, false)

        binding.submitButton.setOnClickListener { view: View ->
            // TODO: Data Validation
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


            // Data Validation
            if(busID.isNotEmpty()){
                if(busCapacity.isNotEmpty()){
                    val capacity = Integer.parseInt(busCapacity)
                    if(capacity > 0) {
                        if(passengersOnboard.isNotEmpty()){
                            val passengers = Integer.parseInt(passengersOnboard)
                            if(passengers <= capacity){
                                println("ALL INPUTS VALID")
                                // TODO: Check TODOs in function
                                // TODO: Create directories and store required data in StationOperation
                                createBusOperationDirectoriesAndStoreBus(busID, busType, route, capacity, passengers, dateCollection)
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

    private fun createBusOperationDirectoriesAndStoreBus(busID: String, busType: String, route: String, busCapacity: Int, passengersOnboard: Int, dateCollection: String) {
        // TODO: Update stationID and stationName?
        // TODO: Re-implement confirmation message for bus being added to database
        // TODO: Get the user selected station and store in the hashmap as initial bus location under ArrivalTime
        val placeholderStation = "Malvern"

        val busRoutes = hashMapOf(
            "Sandringham To CBD" to "sandringhamToCBD",
            "Frankston From CBD" to "frankstonFromCBD",
            "Frankston To CBD" to "frankstonToCBD",
            "Sandringham From CBD" to "sandringhamFromCBD"
        )

        // Maps of data for the documents that have multiple fields. To be stored in appropriate documents
        val arrivalTimeMap = hashMapOf(
            "stationName" to placeholderStation,
            "stopNum" to 1,
            "arrivalTime" to Timestamp(Date())
        )

        val passengerCountMap = hashMapOf(
            "stationID" to "$placeholderStation Station",
            "stationName" to placeholderStation,
            "stopNum" to 1,
            "count" to passengersOnboard
        )


        projectFirestore.collection("BusOperation").document(dateCollection)
            .set(HashMap<String, Any>())
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
                // CREATE BusType DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("BusType")
                    .set(hashMapOf(
                        "busType" to busType
                    ))
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE BusID DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("BusID")
                    .set(hashMapOf(
                        "busID" to busID
                    ))
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE ArrivalTime DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("ArrivalTime")
                    .set(arrivalTimeMap)
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")
                        // CREATE initial station arrival under station collection under ArrivalTime Document
                        projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("ArrivalTime")
                            .collection(busRoutes[route]!!).document("$placeholderStation Station")
                            .set(arrivalTimeMap)
                            .addOnSuccessListener {
                                println("DocumentSnapshot successfully written!")


                            }
                            .addOnFailureListener {
                                    exception -> Log.w("Error writing document", exception)
                            }

                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE DepartureTime DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("DepartureTime")
                    .set(HashMap<String, Any>())
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE PassengerCount DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("PassengerCount")
                    .set(passengerCountMap)
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")
                        // CREATE initial passenger count collection
                        projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("PassengerCount")
                            .collection("PassengerCountAtStations").document("$placeholderStation Station")
                            .set(passengerCountMap)
                            .addOnSuccessListener {
                                println("DocumentSnapshot successfully written!")


                            }
                            .addOnFailureListener {
                                    exception -> Log.w("Error writing document", exception)
                            }

                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE Capacity DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("Capacity")
                    .set(hashMapOf(
                        "capacity" to busCapacity
                    ))
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
                // CREATE OperatedRoute DOCUMENT
                projectFirestore.collection("BusOperation").document(dateCollection).collection("${busRoutes[route]}_${busID}").document("OperatedRoutes")
                    .set(hashMapOf(
                        "routeID" to busRoutes[route]
                    ))
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")


                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
            }
            .addOnFailureListener {
                    exception -> Log.w("Error writing document", exception)
            }
    }

    private fun createBusArchiveDirectoriesAndStoreBus(busID: String, busType: String, route: String, busCapacity: Int, passengersOnboard: Int, dateCollection: String) {
        val placeholderStation = "Malvern"
        // CREATE initial date document in StationOperation if not created
        projectFirestore.collection("StationOperation").document(dateCollection)
            .set(HashMap<String, Any>())
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")
                // CREATE initial date document in StationOperation if not created
                projectFirestore.collection("StationOperation").document(dateCollection)
                    .collection("$placeholderStation Station").document()
                    .set(HashMap<String, Any>())
                    .addOnSuccessListener {
                        println("DocumentSnapshot successfully written!")



                    }
                    .addOnFailureListener {
                            exception -> Log.w("Error writing document", exception)
                    }
            }
            .addOnFailureListener {
                    exception -> Log.w("Error writing document", exception)
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

    // Old Data Store Function
    private fun storeBusData(busID: String, busType: String, route: String, busCapacity: Int, passengersOnboard: Int, dateCollection: String, view: View) {

        val initArrivalTime = Timestamp(Date())

        val busRoutes = hashMapOf(
            "Sandringham To CBD" to "sandringhamToCBD",
            "Frankston From CBD" to "frankstonFromCBD",
            "Frankston To CBD" to "frankstonToCBD",
            "Sandringham From CBD" to "sandringhamFromCBD"
        )

        // TODO: Get the user selected station and store in the hashmap as initial bus location under ArrivalTime
        val placeholderStation = "Malvern"

        // TODO: Update hashMap to new data structure
        //Firestore Data
        // Hashmap of bus info that will be stored in Firestore
        val bus = hashMapOf(
            "ArrivalTime" to hashMapOf(
                "ArrivalTimeAtStations" to hashMapOf(
                    "ArrivalTimeAt$placeholderStation" to hashMapOf(
                        "arrivalTime" to initArrivalTime,
                        "stationID" to placeholderStation.lowercase(),
                        "stationName" to "$placeholderStation Station",
                        "stopNum" to 1
                    ),
                ),
            ),
            "BusID" to hashMapOf(
                "Bus ID" to busID
            ),
            "BusType" to hashMapOf(
                "Bus Type" to busType.lowercase()
            ),
            "Capacity" to hashMapOf(
                "capacity" to busCapacity
            ),
            "DepartureTime" to hashMapOf(
                "DepartureTimeAtStations" to emptyMap<String, String>()
            ),
            "OperatedRoute" to hashMapOf(
                "routeID" to busRoutes[route]
            ),
            "PassengerCount" to hashMapOf(
                "PassengerCountAtStations" to hashMapOf(
                    "PassengerCountAt$placeholderStation" to passengersOnboard
                )
            ),
        )


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

        // Create the Date document first using today's date if it does not exist
        projectFirestore.collection("BusOperation").document(dateCollection)
            .set(HashMap<String, Any>())
            .addOnSuccessListener {
                println("DocumentSnapshot successfully written!")

                //TODO: Check if bus already exists in Firestore
                //TODO: Update code to set the correct data based on the new database structure in Firestore
                //Store the new bus as a document in the BusOperation collection, under the document with the day's date
                projectFirestore.collection("BusOperation").document(dateCollection).collection("buses").document(busID)
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
            .addOnFailureListener {
                    exception -> Log.w("Error writing document", exception)
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Could not add bus $busID")
                builder.setMessage("Error writing document $exception")
                builder.setPositiveButton("OK"){_,_ -> }
                builder.show()
            }
    }

}