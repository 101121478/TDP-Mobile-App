package com.cos60011.group1.mttransit.screens.dashboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.SharedViewModel
import com.cos60011.group1.mttransit.databinding.FragmentLocationBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private lateinit var currentStation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bind views you want to change here
        currentStation = binding.userLocation

        binding.setLocationButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_busBoardFragment_to_setStationFragment)
        }

        // read userLocation from disk and set it to location
        val sharedPref = requireActivity().getSharedPreferences(
            "com.cos60011.group1.mttransit.settings.${Firebase.auth.currentUser?.email.toString()}",
            Context.MODE_PRIVATE
        )
        val userLocation = sharedPref.getString("userLocation", "Unknown")
        viewModel.setLocation(userLocation.toString())

        currentStation.text = viewModel.userLocation.value
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}