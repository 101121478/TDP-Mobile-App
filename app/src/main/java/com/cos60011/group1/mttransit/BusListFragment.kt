package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.databinding.FragmentBusListBinding

class BusListFragment : Fragment() {
    private var _binding: FragmentBusListBinding? = null
    private val binding get() = _binding!!

    //Declare static references to views here
    private lateinit var rvList: RecyclerView
    private lateinit var buses: ArrayList<Bus>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBusListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bind views you want to change here
        rvList = binding.busListRecycler

        // Initialize buses
        buses = Bus.createTestBuses(10)

        // Create adapter passing in the sample user data
        val listAdapter = BusListAdapter(requireContext(), buses)

        // Attach the adapter to the recyclerview to populate items
        rvList.adapter = listAdapter

        // Set layout manager to position the items
        rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}