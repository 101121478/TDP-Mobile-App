package com.cos60011.group1.mttransit.screens.busstatus

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class BusStatusViewModel(stationRef: String, busRef: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val busStatusQuery: Query
    private lateinit var busStatusPath: String
    private lateinit var stationId: String
    private lateinit var targetBusId: String
    private lateinit var routeId: String
    private lateinit var curStopNo: String
    private lateinit var prevStop: String
    private lateinit var curStop: String
    private lateinit var nextStop: String

    private val _busId = MutableLiveData<String>()
    val busId: LiveData<String>
        get() = _busId

    private val _busType = MutableLiveData<String>()
    val busType: LiveData<String>
        get() = _busType

    private val _busRoute = MutableLiveData<String>()
    val busRoute: LiveData<String>
        get() = _busRoute

    private val _passengerCapacity = MutableLiveData<String>()
    val passengerCapacity: LiveData<String>
        get() = _passengerCapacity

    private val _passengerOnBoard = MutableLiveData<String>()
    val passengerOnBoard: LiveData<String>
        get() = _passengerOnBoard

    private val _isUpdate = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean>
        get() = _isUpdate

    private val _isArrive = MutableLiveData<Boolean>()
    val isArrive: LiveData<Boolean>
        get() = _isArrive

    private val _isDeparture = MutableLiveData<Boolean>()
    val isDeparture: LiveData<Boolean>
        get() = _isDeparture

    init {
//        val now = Calendar.getInstance()
//        val year = now.get(Calendar.YEAR)
//        val month = now.get(Calendar.MONTH)
//        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
////        val dateDocument = "$year-${month+1}-$dayOfMonth"
//        val dateDocument = "2021-10-12"
//
////        busColRef = db.collection("BusOperationTest").document(dateDocument)
////            .collection("Routes").document(routeRef)
////            .collection(busRef)
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        stationId = "Flinders Station"
        targetBusId = "465"

        // TODO Get Date dynamically
        // TODO Get Station Collection Name
        // TODO Important - need unique identity to identify each bus
        //query to get bus status
        busStatusQuery = db.collection("StationOperation").document("2021-10-14")
            .collection(stationId).document("busArchive")
            .collection("busesAtStop")
            .whereEqualTo("active", true)
            .whereEqualTo("busId", targetBusId)

        getBusDocument()
    }

    fun getBusDocument() {
        val source = Source.SERVER
        busStatusQuery.get(source).addOnSuccessListener { document ->
            document.forEach { queryDocumentSnapshot ->
                run {
                    if (queryDocumentSnapshot.data["busId"].toString() == targetBusId) {
                        _busId.value = queryDocumentSnapshot.data["busId"].toString()
                        _busRoute.value = queryDocumentSnapshot.data["routeName"].toString()
                        _passengerCapacity.value = queryDocumentSnapshot.data["capacity"].toString()
                        _passengerOnBoard.value = queryDocumentSnapshot.data["passengers"].toString()
                        routeId = queryDocumentSnapshot.data["routeId"].toString()
                        curStopNo = queryDocumentSnapshot.data["currentStopNo"].toString()
                        prevStop = queryDocumentSnapshot.data["previousStop"].toString()
                        curStop = queryDocumentSnapshot.data["currentStop"].toString()
                        nextStop = queryDocumentSnapshot.data["nextStop"].toString()
                        busStatusPath = queryDocumentSnapshot.reference.path
                    }
                }
            }

        }.addOnFailureListener { e ->
            Log.w("Fail to get bus detail", e)
        }
//        busArriveQuery.get(source).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val document = task.result
//                _busType.value = document?.get("busType").toString()
//                _busRoute.value = document?.get("routeName").toString()
//                _passengerCapacity.value = document?.get("capacity").toString()
//                _passengerOnBoard.value = document?.get("passengers").toString()
////                _nextStop.value = document?.get("nextStop").toString()
//                _isUpdate.value = true
//            } else {
//                _isUpdate.value = false
//            }
//        }
    }

    fun markArrive() {
        val timeStamp = Timestamp.now()
        // Get Stop Details from RouteOpeartion Collection
        // TODO if the stop no is 1 Do not show mark as arrive button
        val source = Source.SERVER
        val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo + 1}")
        nextBusStopRef.get(source).addOnSuccessListener { document ->
            if (document != null) {
                // the arriving stop isn't the last stop of the route
                // updateArrivalTime, lastUpdated Time, previous stop, current stop, and next stop
                prevStop = curStop
                curStop = nextStop
                nextStop = document.get("stationName").toString()
                curStopNo += 1
                println(nextStop)
                // Update required fields in bus status
//                db.document(busStatusPath).update("arrivalTime", timeStamp,
//                    "lastUpdated", timeStamp,
//                "previousStop", prevStop, "currentStop", curStop, "nextStop", nextStop).addOnSuccessListener {  }
//                nextStop = document?.get("stationName").toString()

            } else {
                // the arriving stop is the last stop of the route
            }
//            if (document != null) {
//                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//            } else {
//                Log.d(TAG, "No such document")
//            }
        }
            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
                println("failed")
            }
//        val arrivalTime = Timestamp.now()
//        db.document(busStatusPath).update("currentStopNo", 2).addOnSuccessListener {  }


//        busColRef.document("Status")
//            .update("arrival", arrivalTime)
//            .addOnSuccessListener {  }
//            .addOnFailureListener { _isArrive.value = false }
//
//        // get next stop information from RouteOperation collection
//        db.collection("RouteOpeartionTest").document(_route.value.toString())
//            .collection("Stops")
//            .document(_nextStop.value.toString())
//            .get().addOnSuccessListener { document ->
//                if (document != null) {
//                    val stopNo = document.get("stopNo").toString()
//
//                    // update arrivalTime collection
//                    val arrivalTime = hashMapOf(
//                        "stationName" to _nextStop.value.toString(),
//                        "stopNo" to stopNo,
//                        "arrivalTime" to arrivalTime
//                    )
//                    busColRef.document("ArrivalTime/Stops/$stopNo").set(arrivalTime)
//
//                    _isArrive.value = true
//
//                } else {
//                    _isArrive.value = false
//                }
//            }
//            .addOnFailureListener { exception ->
//                _isArrive.value = false
//            }
//
//
////        busColRef.document("Stops")
////            .update("arrival", Timestamp.now())
////            .addOnSuccessListener { _isArrive.value = true }
////            .addOnFailureListener { _isArrive.value = false }
    }

    fun markDeparture() {
        val departureTimeStamp = Timestamp.now()
        // TODO: Need change to correct variable: route collection ID and date path is for testing
        // path for writing data to BusOperation
        val departRef = db.collection("BusOperation")
            .document("2021-10-14")
            .collection("sandringhamToCBD_${_busId.value}")
            .document("DepartureTime")
            .collection("DepartureTimeAtStations")
            .document(curStop)

        val passengerRef = db.collection("BusOperation")
            .document("2021-10-14")
            .collection("sandringhamToCBD_${_busId.value}")
            .document("PassengerCount")
            .collection("PassengerCountAtStations")
            .document(curStop)

        val operationHistoryRef = db.collection("StationOperation")
            .document("2021-10-14")
            .collection(nextStop)
            .document("operationHistory")


        /**
         * Check if the current stop is the last stop, if it is the last stop, do not need to create new
         * bus status document under busArchive
         * 1. construct the next stop document path if it is not exist
         * 2. if next stop document path is not exist, construct operationHistory path as well
         * 3. set active to false
         * 4. create new document at the new stop document (only if it is not last stop)
         * 5. create new document at operationHistory
         * 6. create departure document and passenger document in BusOperation
         */
        val source = Source.SERVER
        val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo + 1}")

        nextBusStopRef.get(source).addOnSuccessListener { document ->
            if (document.data != null) {
                // if it is not last stop
                // TODO date path is hard code
                val busArchiveRef = db.collection("StationOperation")
                    .document("2021-10-14")
                    .collection(nextStop)
                    .document("busArchive")

                /**
                 * If busArchive Path is not exist
                 * 1. update active to false
                 * 2. add busArchive path
                 * 3. create new bus status document
                 * 4. add operationHistory path
                 * 5. add document to operationHistory under current station
                 * 6. add data to BusOperation
                 */
                /**
                 * If busArchive Path is not exist
                 */
                busArchiveRef.get().addOnSuccessListener { document ->
                    if (document.data == null) {
                        // path to set bus active to false
                        val busRef = db.document(busStatusPath)
                        // new bus document path of busesAtStop
                        val newBusRef = busArchiveRef.collection("busesAtStop").document()
                        // new bus document path of DepartedBuses
                        val departedBusRef = operationHistoryRef.collection("DepartedBuses")
                            .document("${routeId}_${_busId.value}")

                        // create new bus document
                        val newBusData = createNextStopBusData(departureTimeStamp)
                        // create BusOperation data
                        val departData = createDepartureTimeData(departureTimeStamp)
                        val passengersData = createPassengerCountData()

                        db.runBatch { batch ->
                            // StationOperation
                            // 1. set bus active to false
                            batch.update(busRef, "active", false)
                            // 2. create next stop path if it is not exist
                            batch.set(busArchiveRef, HashMap<String, Any>())
                            // 3. add new bus document
                            batch.set(newBusRef, newBusData)
                            // 4. create operation history if it is not exist
                            batch.set(operationHistoryRef, HashMap<String, Any>())
                            // 5. add new departedBusDocument
                            batch.set(departedBusRef, hashMapOf(
                                "departureTime" to departureTimeStamp
                            ))
                            // BusOperation
                            // 6. add departure time document
                            batch.set(departRef, departData)
                            // 6. add Passenger count document
                            batch.set(passengerRef, passengersData)

                        }.addOnSuccessListener {
//                            _isDeparture.value = true
                        }.addOnFailureListener { e ->
                            Log.w("Fail to mark bus as departure", e)
                        }
                    } else {
                        /**
                         * If busArchive Path is exist, do not need to create busArchive Path and OperationHistory Path
                         * 1. update active to false
                         * 2. create new bus status document
                         * 3. add document to operationHistory under current station
                         * 4. add data to BusOperation
                         */
                        val busRef = db.document(busStatusPath)
                        val newBusRef = busArchiveRef.collection("busesAtStop").document()
                        // new bus document path of DepartedBuses
                        val departedBusRef = operationHistoryRef.collection("DepartedBuses")
                            .document("${routeId}_${_busId.value}")
                        // create new bus document
                        val newBusData = createNextStopBusData(departureTimeStamp)
                        // create BusOperation data
                        val departData = createDepartureTimeData(departureTimeStamp)
                        val passengersData = createPassengerCountData()

                        db.runBatch { batch ->
                            // 1. set bus active to false
                            batch.update(busRef, "active", false)
                            // 2. add new bus document
                            batch.set(newBusRef, newBusData)
                            // 3. add new departedBusDocument
                            batch.set(departedBusRef, hashMapOf(
                                "departureTime" to departureTimeStamp
                            ))
                            // 4. add departure time document
                            batch.set(departRef, departData)
                            // 4. add Passenger count document
                            batch.set(passengerRef, passengersData)

                        }.addOnSuccessListener {
//                            _isDeparture.value = true
                        }.addOnFailureListener { e ->
                            Log.w("Fail to mark bus as departure", e)
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.w("Fail to get last stop information when mark bus as departure", e)
                }
            } else {
                /**
                 * If it is last stop
                 * 1. update active to false
                 * 2. add document to operationHistory under current station
                 * 3. add data to BusOperation
                 */
                // bus status ref
                val busRef = db.document(busStatusPath)
                val departedBusRef = operationHistoryRef.collection("DepartedBuses")
                    .document("${routeId}_${_busId.value}")
                // create BusOperation data
                val departData = createDepartureTimeData(departureTimeStamp)
                val passengersData = createPassengerCountData()
                db.runBatch { batch ->
                    // set bus active to false
                    batch.update(busRef, "active", false)

                    // add new departedBusDocument
                    batch.set(departedBusRef, hashMapOf(
                        "departureTime" to departureTimeStamp
                    ))
                    // add departure time document
                    batch.set(departRef, departData)
                    // add Passenger count document
                    batch.set(passengerRef, passengersData)

                }.addOnSuccessListener {
//                            _isDeparture.value = true
                }.addOnFailureListener { e ->
                    Log.w("Fail to mark bus as departure", e)
                }
            }
        }
    }

    private fun createNextStopBusData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        // TODO arrivalTime null issue when marked bus as departure
        return hashMapOf(
            "active" to true,
            "arrivalTime" to timestamp,
            "busId" to _busId.value,
            "capacity" to _passengerCapacity.value!!.toInt(),
            "currentStop" to curStop,
            "currentStopNo" to curStopNo.toInt(),
            "departureTime" to timestamp,
            "lastUpdated" to timestamp,
            "nextStop" to nextStop,
            "passengers" to _passengerOnBoard.value!!.toInt(),
            "previousStop" to prevStop,
            "routeId" to routeId,
            "routeName" to _busRoute.value,
        )
    }

    private fun createDepartureTimeData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to curStop,
            "stopNum" to curStopNo,
            "departureTime" to timestamp,
        )
    }

    private fun createPassengerCountData(): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to curStop,
            "stopNum" to curStopNo,
            "passengerCount" to _passengerOnBoard.value!!.toInt(),
        )
    }

}

