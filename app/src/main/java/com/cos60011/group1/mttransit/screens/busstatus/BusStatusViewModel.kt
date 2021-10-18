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
        // TODO the date, stationId, targetBusId is hard code for testing purpose
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        stationId = stationRef
        targetBusId = busRef

        //query to get bus status
        busStatusQuery = db.collection("StationOperation").document(today)
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
    }

    fun markArrive() {
        val arrivalTimeStamp = Timestamp.now()

        /**
         * Mark as arrive
         * 1. get busStop information
         * 2. find current bus document -> update ArrivalTime, lastUpdated Time,
         * previous stop, current stop, current stop number, next stop
         * 3. If the arrival stop is final stop, update next stop to "LastStop"
         * 4. update OperationHistory
         * 5. update BusOperation collection
         */
        val source = Source.SERVER
        val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo.toInt() + 1}")
        nextBusStopRef.get(source).addOnSuccessListener { document ->
            if (document.data != null) {
                // if it is not last stop
                prevStop = curStop
                curStop = nextStop
                nextStop = document.get("stationName").toString()
                curStopNo = "${curStopNo.toInt() + 1}"
            } else {
                // if it is last stop
                prevStop = curStop
                curStop = nextStop
                nextStop = "LastStop"
                curStopNo = "${curStopNo.toInt() + 1}"
            }

            val busRef = db.document(busStatusPath)

            val arrivedBusHistoryRef = db.collection("StationOperation")
                .document("2021-10-14")
                .collection(curStop)
                .document("operationHistory")
                .collection("ArrivedBuses")
                .document("${routeId}_${_busId.value}")

            val arrivalTimeBusOperationRef = db.collection("BusOperation")
                .document("2021-10-14")
                .collection("sandringhamToCBD_${_busId.value}")
                .document("ArrivalTime")
                .collection("ArrivalTimeAtStations")
                .document(curStop)

            val arrivalTimeBusOperationData = createArrivalTimeData(arrivalTimeStamp)
            db.runBatch { batch ->
                // 2 & 3 update bus document
                batch.update(busRef,"arrivalTime", arrivalTimeStamp,
                    "previousStop", prevStop,
                "currentStop", curStop,
                "currentStopNo", curStopNo,
                "nextStop", nextStop,
                "lastUpdated", arrivalTimeStamp)

                // 4 update OperationHistory
                batch.set(arrivedBusHistoryRef, hashMapOf(
                    "arrivalTime" to arrivalTimeStamp
                ))

                // 5 write data to BusOperation
                batch.set(arrivalTimeBusOperationRef, arrivalTimeBusOperationData)
            }.addOnSuccessListener {
//                            _isArrive.value = true
            }.addOnFailureListener { e ->
                Log.w("Fail to mark bus as arrive", e)
            }

        }.addOnFailureListener { e ->
            Log.w("Fail to get last stop information when mark bus as arrive", e)
        }
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
            .collection(curStop)
            .document("operationHistory")

        /**
         * Check if the current stop is the last stop
         * A. If it is the last stop
         * 1. mark the current bus document as false
         * 2. update the previousStop with currentStop
         * 3. update the current stop with "none"
         * 4. update nextStop with "lastStop"
         *
         * B. If it is not the last stop
         * 1. update the previousStop with currentStop
         * 2. update the current stop with "none"
         * 3. update nextStop with next stop
         *
         * C. Both
         * 1. update departure and last update time stamp
         * 2. get the Disembarking passengers and Boarding passenger from text fields
         * 3. update ToPassengers number under totalPassengers with += Boarding passenger
         * 4. create new document at operationHistory
         * 5. create departure document and passenger document in BusOperation
         *
         * bus status document under busArchive
         * 1. construct the next stop document path if it is not exist
         * 2. if next stop document path is not exist, construct operationHistory path as well
         * 3. set active to false
         * 4. create new document at the new stop document (only if it is not last stop)
         * 5. create new document at operationHistory
         * 6. create departure document and passenger document in BusOperation
         */
        val source = Source.SERVER
        val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo.toInt() + 1}")

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

//    fun markDeparture() {
//        val departureTimeStamp = Timestamp.now()
//        // TODO: Need change to correct variable: route collection ID and date path is for testing
//        // path for writing data to BusOperation
//        val departRef = db.collection("BusOperation")
//            .document("2021-10-14")
//            .collection("sandringhamToCBD_${_busId.value}")
//            .document("DepartureTime")
//            .collection("DepartureTimeAtStations")
//            .document(curStop)
//
//        val passengerRef = db.collection("BusOperation")
//            .document("2021-10-14")
//            .collection("sandringhamToCBD_${_busId.value}")
//            .document("PassengerCount")
//            .collection("PassengerCountAtStations")
//            .document(curStop)
//
//        val operationHistoryRef = db.collection("StationOperation")
//            .document("2021-10-14")
//            .collection(curStop)
//            .document("operationHistory")
//
//
//        /**
//         * Check if the current stop is the last stop
//         * A. If it is the last stop
//         * 1. mark the current bus document as false
//         * 2. update the previousStop with currentStop
//         * 3. update the current stop with "none"
//         * 4. update nextStop with "lastStop"
//         *
//         * B. If it is not the last stop
//         * 1. update the previousStop with currentStop
//         * 2. update the current stop with "none"
//         * 3. update nextStop with next stop
//         *
//         * C. Both
//         * 1. get the Disembarking passengers and Boarding passenger from text fields
//         * 2. update ToPassengers number under totalPassengers with += Boarding passenger
//         * 3. create new document at operationHistory
//         * 4. create departure document and passenger document in BusOperation
//         *
//         * bus status document under busArchive
//         * 1. construct the next stop document path if it is not exist
//         * 2. if next stop document path is not exist, construct operationHistory path as well
//         * 3. set active to false
//         * 4. create new document at the new stop document (only if it is not last stop)
//         * 5. create new document at operationHistory
//         * 6. create departure document and passenger document in BusOperation
//         */
//        val source = Source.SERVER
//        val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo.toInt() + 1}")
//
//        nextBusStopRef.get(source).addOnSuccessListener { document ->
//            if (document.data != null) {
//                // if it is not last stop
//                // TODO date path is hard code
//                val busArchiveRef = db.collection("StationOperation")
//                    .document("2021-10-14")
//                    .collection(nextStop)
//                    .document("busArchive")
//
//                /**
//                 * If busArchive Path is not exist
//                 * 1. update active to false
//                 * 2. add busArchive path
//                 * 3. create new bus status document
//                 * 4. add operationHistory path
//                 * 5. add document to operationHistory under current station
//                 * 6. add data to BusOperation
//                 */
//                /**
//                 * If busArchive Path is not exist
//                 */
//                busArchiveRef.get().addOnSuccessListener { document ->
//                    if (document.data == null) {
//                        // path to set bus active to false
//                        val busRef = db.document(busStatusPath)
//                        // new bus document path of busesAtStop
//                        val newBusRef = busArchiveRef.collection("busesAtStop").document()
//                        // new bus document path of DepartedBuses
//                        val departedBusRef = operationHistoryRef.collection("DepartedBuses")
//                            .document("${routeId}_${_busId.value}")
//
//                        // create new bus document
//                        val newBusData = createNextStopBusData(departureTimeStamp)
//                        // create BusOperation data
//                        val departData = createDepartureTimeData(departureTimeStamp)
//                        val passengersData = createPassengerCountData()
//
//                        db.runBatch { batch ->
//                            // StationOperation
//                            // 1. set bus active to false
//                            batch.update(busRef, "active", false)
//                            // 2. create next stop path if it is not exist
//                            batch.set(busArchiveRef, HashMap<String, Any>())
//                            // 3. add new bus document
//                            batch.set(newBusRef, newBusData)
//                            // 4. create operation history if it is not exist
//                            batch.set(operationHistoryRef, HashMap<String, Any>())
//                            // 5. add new departedBusDocument
//                            batch.set(departedBusRef, hashMapOf(
//                                "departureTime" to departureTimeStamp
//                            ))
//                            // BusOperation
//                            // 6. add departure time document
//                            batch.set(departRef, departData)
//                            // 6. add Passenger count document
//                            batch.set(passengerRef, passengersData)
//
//                        }.addOnSuccessListener {
////                            _isDeparture.value = true
//                        }.addOnFailureListener { e ->
//                            Log.w("Fail to mark bus as departure", e)
//                        }
//                    } else {
//                        /**
//                         * If busArchive Path is exist, do not need to create busArchive Path and OperationHistory Path
//                         * 1. update active to false
//                         * 2. create new bus status document
//                         * 3. add document to operationHistory under current station
//                         * 4. add data to BusOperation
//                         */
//                        val busRef = db.document(busStatusPath)
//                        val newBusRef = busArchiveRef.collection("busesAtStop").document()
//                        // new bus document path of DepartedBuses
//                        val departedBusRef = operationHistoryRef.collection("DepartedBuses")
//                            .document("${routeId}_${_busId.value}")
//                        // create new bus document
//                        val newBusData = createNextStopBusData(departureTimeStamp)
//                        // create BusOperation data
//                        val departData = createDepartureTimeData(departureTimeStamp)
//                        val passengersData = createPassengerCountData()
//
//                        db.runBatch { batch ->
//                            // 1. set bus active to false
//                            batch.update(busRef, "active", false)
//                            // 2. add new bus document
//                            batch.set(newBusRef, newBusData)
//                            // 3. add new departedBusDocument
//                            batch.set(departedBusRef, hashMapOf(
//                                "departureTime" to departureTimeStamp
//                            ))
//                            // 4. add departure time document
//                            batch.set(departRef, departData)
//                            // 4. add Passenger count document
//                            batch.set(passengerRef, passengersData)
//
//                        }.addOnSuccessListener {
////                            _isDeparture.value = true
//                        }.addOnFailureListener { e ->
//                            Log.w("Fail to mark bus as departure", e)
//                        }
//                    }
//                }.addOnFailureListener { e ->
//                    Log.w("Fail to get last stop information when mark bus as departure", e)
//                }
//            } else {
//                /**
//                 * If it is last stop
//                 * 1. update active to false
//                 * 2. add document to operationHistory under current station
//                 * 3. add data to BusOperation
//                 */
//                // bus status ref
//                val busRef = db.document(busStatusPath)
//                val departedBusRef = operationHistoryRef.collection("DepartedBuses")
//                    .document("${routeId}_${_busId.value}")
//                // create BusOperation data
//                val departData = createDepartureTimeData(departureTimeStamp)
//                val passengersData = createPassengerCountData()
//                db.runBatch { batch ->
//                    // set bus active to false
//                    batch.update(busRef, "active", false)
//
//                    // add new departedBusDocument
//                    batch.set(departedBusRef, hashMapOf(
//                        "departureTime" to departureTimeStamp
//                    ))
//                    // add departure time document
//                    batch.set(departRef, departData)
//                    // add Passenger count document
//                    batch.set(passengerRef, passengersData)
//
//                }.addOnSuccessListener {
////                            _isDeparture.value = true
//                }.addOnFailureListener { e ->
//                    Log.w("Fail to mark bus as departure", e)
//                }
//            }
//        }
//    }

    private fun createArrivalTimeData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to curStop,
            "stopNum" to curStopNo.toInt(),
            "departureTime" to timestamp,
        )
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
            "stopNum" to curStopNo.toInt(),
            "departureTime" to timestamp,
        )
    }

    private fun createPassengerCountData(): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to curStop,
            "stopNum" to curStopNo.toInt(),
            "passengerCount" to _passengerOnBoard.value!!.toInt(),
        )
    }

}

