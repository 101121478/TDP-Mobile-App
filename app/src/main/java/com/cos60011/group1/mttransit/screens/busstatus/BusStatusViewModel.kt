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


class BusStatusViewModel(stationRef: String, busRef: String, routeRef: String, isCurrentBus: Boolean, isRecentBus: Boolean) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var busStatusQuery: Query
    private lateinit var busStatusPath: String
    private lateinit var stationId: String
    private lateinit var selectedBusId: String
    private lateinit var selectedRouteId: String
    private lateinit var today: String
    private lateinit var routeId: String
    private lateinit var curStopNo: String
    private lateinit var prevStop: String
    private lateinit var curStop: String
    private lateinit var nextStop: String

    private val _busId = MutableLiveData<String>()
    val busId: LiveData<String>
        get() = _busId

    private val _busRoute = MutableLiveData<String>()
    val busRoute: LiveData<String>
        get() = _busRoute

    private val _passengerCapacity = MutableLiveData<String>()
    val passengerCapacity: LiveData<String>
        get() = _passengerCapacity

    private val _passengerOnBoard = MutableLiveData<String>()
    val passengerOnBoard: LiveData<String>
        get() = _passengerOnBoard

    private val _passengerBoarding = MutableLiveData<String>()
    val passengerBoarding: LiveData<String>
        get() = _passengerBoarding

    private val _passengerDisembarking = MutableLiveData<String>()
    val passengerDisembarking: LiveData<String>
        get() = _passengerDisembarking

    private val _isUpdate = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean>
        get() = _isUpdate

    private val _isArrive = MutableLiveData<Boolean>()
    val isArrive: LiveData<Boolean>
        get() = _isArrive

    private val _isDeparture = MutableLiveData<Boolean>()
    val isDeparture: LiveData<Boolean>
        get() = _isDeparture

    private val _isUnmarkDeparted = MutableLiveData<Boolean>()
    val isUnmarkDeparted: LiveData<Boolean>
        get() = _isUnmarkDeparted

    private val _isMarked = MutableLiveData<Boolean>()
    val isMarked: LiveData<Boolean>
        get() = _isMarked

    private val _isDbError = MutableLiveData<Boolean>()
    val isDbError: LiveData<Boolean>
        get() = _isDbError

    init {
        _passengerDisembarking.value = ""
        _passengerBoarding.value =""

        today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        selectedBusId = busRef
        selectedRouteId = convertRouteNameToID(routeRef)

        getBusDocument(isCurrentBus, isRecentBus, stationRef)
    }

    private fun convertRouteNameToID(routeName: String): String {
        return routeName.filter { !it.isWhitespace() }.replaceFirstChar { it.lowercase() }
    }

    private fun getBusDocument(isCurrentBus: Boolean, isRecentBus: Boolean, currentStationRef: String) {
        stationId = currentStationRef
        println("RouteID: $selectedRouteId")
        if (isCurrentBus || isRecentBus) {
            //query to get bus status
            busStatusQuery = db.collection("StationOperation").document(today)
                .collection(stationId).document("busArchive")
                .collection("busesAtStop")
                .whereEqualTo("active", true)
                .whereEqualTo("busId", selectedBusId)

            getBusDocument()
        } else {
            val source = Source.SERVER
            val prevBusStopQuery = db.collection("RouteOperation")
                .document(selectedRouteId)
                .collection("Stops")
                .whereEqualTo("stationName", stationId)

            prevBusStopQuery.get(source).addOnSuccessListener { document ->
                document.forEach { queryDocumentSnapshot ->
                    if (queryDocumentSnapshot.data["stationName"]  == stationId) {
                        val documentID = queryDocumentSnapshot.data["stationNo"]
                        println("Document ID $documentID")
                        db.document("RouteOperation/${selectedRouteId}/Stops/${documentID.toString().toInt().minus(1)}")
                            .get(source)
                            .addOnSuccessListener { document ->
                                if (document.data != null) {
                                    val prevBusStopName = document.get("stationName").toString()
                                    println("Previous bus stop name $prevBusStopName")
                                    busStatusQuery = db.collection("StationOperation").document(today)
                                        .collection(prevBusStopName).document("busArchive")
                                        .collection("busesAtStop")
                                        .whereEqualTo("active", true)
                                        .whereEqualTo("busId", selectedBusId)

                                    getBusDocument()
                                }
                            }
                            .addOnFailureListener{ e ->
                                _isDbError.value = true
                                Log.w("Fail to get previous stop document", e)
                            }
                    }
                }
            }.addOnFailureListener{ e ->
                _isDbError.value = true
                Log.w("Fail to get previous stop name ", e)
            }
        }
    }

    fun getBusDocument() {
        val source = Source.SERVER
        busStatusQuery.get(source).addOnSuccessListener { document ->
            document.forEach { queryDocumentSnapshot ->
                run {
                    if (queryDocumentSnapshot.data["busId"].toString() == selectedBusId) {
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
                        _isUpdate.value = true
                    }
                }
            }
        }.addOnFailureListener { e ->
            _isDbError.value = true
            Log.w("Fail to get bus detail", e)
        }
    }

    /**
     * 1. create new station collection with next stop name
     * 2. create busArchive document -> busesAtStop collection -> new bus document copy
     * 3. create a new bus document copy and update currentStop
     * with next Stop, nextStop with none, stopNo += 1, arrivalTime, lastUpdatedTime
     * 4. create OperationHistory document, create ArrivedBuses with busID document
     * 5. create document under busOperation
     * 6. set previous document to false
     */

    fun markArrive() {
        val arrivalTimeStamp = Timestamp.now()
        val source = Source.SERVER
        // check if whether the bus has been marked by other staff
        db.document(busStatusPath).get(source).addOnSuccessListener { document ->
            if (document.data != null) {
                val active = document.get("active") as Boolean
                if (!active) {
                    _isMarked.value = true
                } else {
                    val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo.toInt() + 1}")
                    nextBusStopRef.get(source).addOnSuccessListener { document ->
                        if (document.data != null) {
                            val busRef = db.document(busStatusPath)

                            val busArchiveRef = db.collection("StationOperation")
                                .document(today)
                                .collection(nextStop)
                                .document("busArchive")

                            val operationHistoryRef = db.collection("StationOperation")
                                .document(today)
                                .collection(nextStop)
                                .document("operationHistory")

                            val busRoutesBusIDRef = db.collection("BusOperation")
                                .document("dates")
                                .collection(today)
                                .document("${routeId}_${_busId.value}")

                            val arrivalTimeBusOperationRef = busRoutesBusIDRef
                                .collection("ArrivalTime")
                                .document(stationId)

                            db.runBatch { batch ->
                                // create new station archive
                                batch.set(busArchiveRef, HashMap<String, Any>())

                                // create new bus document
                                batch.set(busArchiveRef.collection("busesAtStop").document(),
                                    createArrivingStopBusData(arrivalTimeStamp))

                                // create new station operationHistory
                                batch.set(operationHistoryRef, HashMap<String, Any>())
                                batch.set(operationHistoryRef.collection("ArrivedBuses")
                                    .document("${routeId}_${selectedBusId}"),
                                    hashMapOf("arrivalTime" to arrivalTimeStamp))

                                // update busOperation
                                batch.set(busRoutesBusIDRef, HashMap<String, Any>())
                                batch.set(arrivalTimeBusOperationRef, createArrivalTimeData(arrivalTimeStamp))

                                // set to false
                                batch.update(busRef, "active", false)
                            }.addOnSuccessListener {
                                _isArrive.value = true
                            }.addOnFailureListener { e ->
                                _isDbError.value = true
                                Log.w("Fail to mark bus as arrive", e)
                            }
                        } else {
                        }
                    }.addOnFailureListener { e ->
                        _isDbError.value = true
                        Log.w("Fail to get last stop information when mark bus as arrive", e)
                    }
                }
            }
        }.addOnFailureListener{ e ->
            _isDbError.value = true
            Log.w("Fail to get selected bus document", e)
        }
    }

    /**
     * Check if the current stop is the last stop
     * A. If it is the last stop
     * 1. mark the current bus document as false
     * 2. update the previousStop with currentStop
     * 3. update the current stop with "none"
     * 4. update nextStop with "none"
     *
     * B. If it is not the last stop
     * 1. update the previousStop with currentStop
     * 2. update the current stop with "none"
     * 3. update nextStop with next stop
     *
     * C. Both
     * 1. update departure and last update time stamp, passengers
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
    fun markDeparture(offBoard: String, boarding: String) {
        val source = Source.SERVER
        // check if whether the bus has been marked by other staff
        db.document(busStatusPath).get(source).addOnSuccessListener { document ->
            if (document.data != null) {
                val currentStop = document.get("currentStop").toString()

                if (currentStop == "none") {
                    _isMarked.value = true
                } else {
                    var isLastStop = false
                    val departureTimeStamp = Timestamp.now()

                    _passengerDisembarking.value = offBoard
                    _passengerBoarding.value = boarding
                    val newPassengerOnBoard =  _passengerOnBoard.value!!.toInt() - _passengerDisembarking.value!!.toInt() + _passengerBoarding.value!!.toInt()

                    val nextBusStopRef = db.document("RouteOperation/$routeId/Stops/${curStopNo.toInt() + 1}")

                    nextBusStopRef.get(source).addOnSuccessListener { document ->
                        if (document.data != null) {
                            // if it is not last stop
                            prevStop = curStop
                            curStop = "none"
                            nextStop = document.get("stationName").toString()
                        } else {
                            // if it is last stop
                            isLastStop = true
                            prevStop = curStop
                            curStop = "none"
                            nextStop = "none"
                        }

                        // path to update StationOperation
                        val busRef = db.document(busStatusPath)

                        val operationHistoryRef = db.collection("StationOperation")
                            .document(today)
                            .collection(prevStop)
                            .document("operationHistory")

                        val departureHistoryRef = db.collection("StationOperation")
                            .document(today)
                            .collection(prevStop)
                            .document("operationHistory")
                            .collection("DepartedBuses")
                            .document("${routeId}_${selectedBusId}")

                        // path to update BusOperation collection

                        val busRoutesBusIDRef = db.collection("BusOperation")
                            .document("dates")
                            .collection(today)
                            .document("${routeId}_${_busId.value}")


                        val departureTimeRef = busRoutesBusIDRef
                            .collection("DepartureTime")
                            .document(stationId)

                        val passengerCountRef = busRoutesBusIDRef
                            .collection("PassengerCount")
                            .document(stationId)

                        // path to update DataVisualisation
                        val totalPassengersRef = db.collection("DataVisualisation")
                            .document("TotalPassengers")

                        val totalRunningBusesForToday = db.collection("DataVisualisation")
                            .document("TotalRunningBusesForToday")


                        db.runBatch { batch ->
                            // update busArchive
                            if (isLastStop) {
                                batch.update(busRef, "active", false,
                                    "previousStop", prevStop,
                                    "currentStop", curStop,
                                    "nextStop", nextStop,
                                    "departureTime", departureTimeStamp,
                                    "lastUpdated", departureTimeStamp,
                                    "passengers", newPassengerOnBoard)

                                batch.update(totalRunningBusesForToday, "lastUpdated", departureTimeStamp,
                                    "count", FieldValue.increment(-1))
                            } else {
                                batch.update(busRef, "previousStop", prevStop,
                                    "currentStop", curStop,
                                    "nextStop", nextStop,
                                    "departureTime", departureTimeStamp,
                                    "lastUpdated", departureTimeStamp,
                                    "passengers", newPassengerOnBoard)
                            }

                            // update OperationHistory
                            batch.set(operationHistoryRef, HashMap<String, Any>())
                            batch.set(departureHistoryRef, hashMapOf("departureTime" to departureTimeStamp))

                            // update BusOperation
                            batch.set(busRoutesBusIDRef, HashMap<String, Any>())
                            batch.set(departureTimeRef, createDepartureTimeData(departureTimeStamp))
                            batch.set(passengerCountRef, createPassengerCountData(newPassengerOnBoard))

                            // update DataVisualisation
                            batch.update(totalPassengersRef, "count", FieldValue.increment(_passengerBoarding.value!!.toLong()))

                        }
                    }.addOnSuccessListener {
                        _isDeparture.value = true
                    }.addOnFailureListener { e ->
                        _isDbError.value = true
                        Log.w("Fail to mark bus as departure", e)
                    }
                }
            }
        }.addOnFailureListener { e ->
            _isDbError.value = true
            Log.w("Fail to get selected bus document", e)
        }
    }

    /**
     * 1. update departureTime back to null
     * 2. update lastUpdated
     * if the currentStopNo is 1
     * 1. currentStop = previousStop
     * 2. previousStop = none
     * 3. nextStop = none
     * if it is not
     * 1. currentStop = previousStop
     * 2. previousStop = currentUserLocation
     * 3. nextStop = none
     */

    // TODO passenger update on busDocument and Data collection
    fun unmarkFromDeparted() {
        val source = Source.SERVER
        // check if whether the bus has been marked by other staff
        if (curStopNo == "1") {
            db.document(busStatusPath)
                .update("currentStop", prevStop,
                "previousStop", "none",
                "nextStop", "none",
                "departureTime", null,
                "lastUpdated", Timestamp.now())
                .addOnSuccessListener {
                    _isUnmarkDeparted.value = true
                }
                .addOnFailureListener{ e ->
                    _isDbError.value = true
                    Log.w("Fail to get selected bus document", e)
                }
        } else {
            db.document(busStatusPath)
                .update(
                    "currentStop", prevStop,
                    "previousStop", stationId,
                    "nextStop", "none",
                    "departureTime", null,
                    "lastUpdated", Timestamp.now()
                )
                .addOnSuccessListener {
                    _isUnmarkDeparted.value = true
                }
                .addOnFailureListener { e ->
                    _isDbError.value = true
                    Log.w("Fail to get selected bus document", e)
                }
        }
    }

    private fun createArrivalTimeData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to nextStop,
            "stopNum" to curStopNo.toInt().plus(1),
            "departureTime" to timestamp,
        )
    }

    private fun createArrivingStopBusData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        // TODO arrivalTime null issue when marked bus as departure
        return hashMapOf(
            "active" to true,
            "arrivalTime" to timestamp,
            "busId" to _busId.value,
            "capacity" to _passengerCapacity.value!!.toInt(),
            "currentStop" to nextStop,
            "currentStopNo" to curStopNo.toInt().plus(1),
            "departureTime" to null,
            "lastUpdated" to timestamp,
            "nextStop" to "none",
            "passengers" to _passengerOnBoard.value!!.toInt(),
            "previousStop" to prevStop,
            "routeId" to routeId,
            "routeName" to _busRoute.value,
        )
    }

    private fun createDepartureTimeData(timestamp: Timestamp): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to prevStop,
            "stopNum" to curStopNo.toInt(),
            "departureTime" to timestamp,
        )
    }

    private fun createPassengerCountData(passengerCount: Int): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "stationName" to prevStop,
            "stopNum" to curStopNo.toInt(),
            "passengerCount" to passengerCount,
        )
    }

}

