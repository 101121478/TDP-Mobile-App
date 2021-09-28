package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.databinding.FragmentBusCardsBinding

class BusCardsFragment : Fragment() {
    private var _binding: FragmentBusCardsBinding? = null
    private val binding get() = _binding!!

    //Declare static references to views here
    private lateinit var rvCards: RecyclerView
    private lateinit var buses: ArrayList<Bus>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBusCardsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bind views you want to change here
        rvCards = binding.busRecycler

        // Initialize buses
        buses = Bus.createTestBuses(10)

        // Create adapter passing in the sample user data
        val cardAdapter = BusCardAdapter(buses)

        // Attach the adapter to the recyclerview to populate items
        rvCards.adapter = cardAdapter

        // Set layout manager to position the items
        rvCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}