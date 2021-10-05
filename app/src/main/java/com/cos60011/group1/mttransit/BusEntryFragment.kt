package com.cos60011.group1.mttransit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.cos60011.group1.mttransit.databinding.FragmentBusEntryBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import com.cos60011.group1.mttransit.firestore.FirestoreClass



class BusEntryFragment : Fragment() {

    private val projectFirestore = FirebaseFirestore.getInstance()
    private lateinit var exception: Exception
    private val firestore = FirestoreClass()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        val binding = FragmentBusEntryBinding.inflate(inflater, container, false)


        binding.submitButton.setOnClickListener { view: View ->
            val busID = binding.busIdInput.text.toString()
            val busType = binding.busTypeInput.text.toString()
            val route = binding.routeSelectionSpinner.selectedItem.toString()
            val busCapacity = Integer.parseInt(binding.passengerCapacityInput.text.toString())
            val passengersOnboard = Integer.parseInt(binding.passengersOnboardInput.text.toString())

            val testTextView = binding.testTextView


            firestore.createNewBus(busID, busType, route, busCapacity, passengersOnboard)
            firestore.getBusDocument(busID)
        }
        return binding.root
    }

}