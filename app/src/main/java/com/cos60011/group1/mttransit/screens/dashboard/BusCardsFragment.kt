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
import com.cos60011.group1.mttransit.databinding.FragmentBusCardsBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class BusCardsFragment : Fragment() {
    private var _binding: FragmentBusCardsBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var rvCards: RecyclerView
    private lateinit var cardAdapter: BusCardAdapter
    lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusCardsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentLocation = viewModel.userLocation.value //add observer here?

        val query = db.collection("StationOperation")
            .document("$today")
            .collection("$currentLocation")
            .document("busArchive")
            .collection("busesAtStop")
            .whereEqualTo("active", true)
            .whereEqualTo("currentStop", "$currentLocation")
            .orderBy("arrivalTime", Query.Direction.DESCENDING)

        val options =
            FirestoreRecyclerOptions.Builder<Bus>().setQuery(query, Bus::class.java).build()

        //bind views you want to change here
        rvCards = binding.busRecycler

        // Create adapter passing in the FirestoreRecyclerOptions object and attach it to recyclerview
        cardAdapter = BusCardAdapter(requireContext(), options, this)
        rvCards.adapter = cardAdapter

        // Set layout manager to position the items
        rvCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvCards.itemAnimator = null
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
}