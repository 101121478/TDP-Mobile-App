package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.databinding.FragmentRecentBusesBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

// TODO: Add recent bus to navigation
// TODO: Create index for query

class RecentBusesFragment : Fragment() {
    private var _binding: FragmentRecentBusesBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var rvList: RecyclerView
    private lateinit var recentBusesAdapter: RecentBusesAdapter
    lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecentBusesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var currentLocation = viewModel.userLocation.value //add observer here?

        // Get all the buses whose next stop is equal to the user's current location
        val query = db.collection("StationOperation")
            .document("$today")
            .collection("$currentLocation")
            .document("busArchive")
            .collection("busesAtStop")
            .whereEqualTo("active", true)
            .whereEqualTo("previousStop", "$currentLocation")
            .whereNotEqualTo("departureTime", null)
            .orderBy("departureTime", Query.Direction.DESCENDING)

        val options =
            FirestoreRecyclerOptions.Builder<Bus>().setQuery(query, Bus::class.java).build()

        //bind views you want to change here
        rvList = binding.recentBusesRecycler

        // Create adapter passing in the FirestoreRecyclerOptions object and attaching it to recyclerview
        recentBusesAdapter = RecentBusesAdapter(requireContext(), options, this)
        rvList.adapter = recentBusesAdapter

        // Set layout manager to position the items
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvList.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        recentBusesAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (recentBusesAdapter != null) {
            recentBusesAdapter.stopListening()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}