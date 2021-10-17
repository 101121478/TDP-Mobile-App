package com.cos60011.group1.mttransit.screens.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.Bus
import com.cos60011.group1.mttransit.SharedViewModel
import com.cos60011.group1.mttransit.databinding.FragmentBusListBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BusListFragment : Fragment() {
    private var _binding: FragmentBusListBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var rvList: RecyclerView
    private lateinit var listAdapter: BusListAdapter
    lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBusListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentLocation = viewModel.userLocation.value //add observer here?



        // Get all the buses whose next stop is equal to the user's current location
        var query = db.collectionGroup("busesAtStop")
            .whereEqualTo("nextStop", "$currentLocation")
            .whereEqualTo("active", true)
            .orderBy("departureTime", Query.Direction.DESCENDING)

        val options =
            FirestoreRecyclerOptions.Builder<Bus>().setQuery(query, Bus::class.java).build()

        //bind views you want to change here
        rvList = binding.busListRecycler

        // Create adapter passing in the FirestoreyRecyclerOptions object and attaching it to recyclerview
        listAdapter = BusListAdapter(requireContext(), options, this)
        rvList.adapter = listAdapter

        // Set layout manager to position the items
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvList.itemAnimator = null
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