package com.cos60011.group1.mttransit.screens.setlocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.SharedViewModel
import com.cos60011.group1.mttransit.databinding.FragmentSetStationBinding
import com.google.android.material.snackbar.Snackbar

class SetStationFragment : Fragment() {
    private var _binding: FragmentSetStationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSetStationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val updateLocationButton: Button = binding.spinnerButton
        val materialSpinner: AutoCompleteTextView = binding.stationOptions //rename this!
        //val feedbackMessage: TextView = binding.setStationFeedback

        // Create a test ArrayAdapter using the string array and a default spinner layout
        val materialSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.test_stations_array,
            android.R.layout.simple_spinner_item)

        materialSpinner.setAdapter(materialSpinnerAdapter)
        updateLocationButton.setOnClickListener {
            val selectedLocation = materialSpinner.text.toString()

            if (selectedLocation.isBlank()) {
                //feedbackMessage.text = viewModel.currentBus.value.toString()
                Snackbar.make(requireView(), "Please select a station.", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.setLocation(selectedLocation)
                view.findNavController()
                    .navigate(R.id.action_setStationFragment_to_busBoardFragment)
            }
        }

        //TODO: Create adapter to get list of stations for Set Station dropdown from Firestore

    }
}
