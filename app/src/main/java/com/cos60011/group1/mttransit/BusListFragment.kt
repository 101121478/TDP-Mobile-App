package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.databinding.FragmentBusListBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.ZoneId

class BusListFragment : Fragment() {
    private var _binding: FragmentBusListBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: BusListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Get location from LocationFragment
        var testLocation = "Richmond Station"

        //Creates a Timestamp for midnight of today which will act as lower range of time query
        var startOfToday = LocalDate.now().atStartOfDay(ZoneId.of("Australia/Melbourne")).toOffsetDateTime().toEpochSecond()
        val startTimestamp = Timestamp(startOfToday, 0)

        //Creates a Timestamp for midnight of next day not-inclusive which will act as upper range of time query
        val endOfToday = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.of("Australia/Melbourne")).toOffsetDateTime().toEpochSecond()
        val endTimestamp = Timestamp(endOfToday, 0)

        //Query Firestore and get results
        var query = db.collection("testBuses").whereEqualTo("location", testLocation)
            .whereGreaterThanOrEqualTo("lastUpdated", startTimestamp)
            .whereLessThan("lastUpdated", endTimestamp).orderBy("lastUpdated", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Bus>().setQuery(query, Bus::class.java).build()

        //bind views you want to change here
        rvList = binding.busListRecycler

        // Create adapter passing in the FirestoreyRecyclerOPtions object and attaching it to recyclerview
        listAdapter = BusListAdapter(requireContext(), options)
        rvList.adapter = listAdapter

        // Set layout manager to position the items
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        listAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (listAdapter != null) {
            listAdapter.stopListening()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}