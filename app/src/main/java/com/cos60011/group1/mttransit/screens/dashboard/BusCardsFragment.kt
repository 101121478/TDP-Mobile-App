package com.cos60011.group1.mttransit.screens.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.Bus
import com.cos60011.group1.mttransit.databinding.FragmentBusCardsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.*

class BusCardsFragment : Fragment() {
    private var _binding: FragmentBusCardsBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var rvCards: RecyclerView
    private lateinit var cardAdapter: BusCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusCardsBinding.inflate(inflater, container, false)
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
        rvCards = binding.busRecycler

        // Create adapter passing in the FirestoreRecyclerOptions object and attach it to recyclerview
        cardAdapter = BusCardAdapter(requireContext(),options)
        rvCards.adapter = cardAdapter

        // Set layout manager to position the items
        rvCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onStart() {
        super.onStart()
        cardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (cardAdapter != null) {
            cardAdapter.stopListening()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "BUS CARDS FRAGMENT"
    }
}