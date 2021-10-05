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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

        //Query Firestore and get results
        //TODO: Develop proper query for incoming buses - needs finalised data structure
        var testLocation = "Richmond Station"
        var query = db.collection("testBuses").whereEqualTo("location", testLocation)
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