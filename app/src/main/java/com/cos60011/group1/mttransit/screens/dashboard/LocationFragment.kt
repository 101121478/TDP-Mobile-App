package com.cos60011.group1.mttransit.screens.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.databinding.FragmentLocationBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    //Declare static references to views here
    private lateinit var currentStation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bind views you want to change here
        currentStation = binding.userLocation

        binding.setLocationButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_busBoardFragment_to_chooseStationFragment)
        }

        //sets user location
        currentStation.text = context?.resources?.getString(R.string.current_user_location_header)
        //TODO: Override default location with user selection. Figure out how to persist it throughout navigation since we do not have a User collection.

        /*
        Testing retrieval of data from Firestore with addOnSuccessListener.
        Doesn't need to be addSnapshotListener because we don't really need to get the location in real-time as it is set by the user

        db.collection("railwayStations").document("armadale").get().addOnSuccessListener {
                document ->
            if (document != null){
                currentStation.text = document.getString("name")
            }
        }
         */
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LOCATION FRAGMENT"
    }
}