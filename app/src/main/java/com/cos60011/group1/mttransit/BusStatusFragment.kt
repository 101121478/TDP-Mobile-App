package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cos60011.group1.mttransit.databinding.FragmentBusStatusBinding
import com.cos60011.group1.mttransit.firestore.FirestoreClass

class BusStatusFragment : Fragment() {

    private val firestore = FirestoreClass()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBusStatusBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bus_status, container, false)

        binding.buttonBusStatusArrive.setOnClickListener { view: View ->

            firestore.setArrivalTime("5", "")
        }

        return binding.root
    }

}