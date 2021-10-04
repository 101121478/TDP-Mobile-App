package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.cos60011.group1.mttransit.databinding.FragmentChooseStationBinding

class ChooseStationFragment : Fragment() {
    private var _binding: FragmentChooseStationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseStationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialSpinner: AutoCompleteTextView = binding.stationOptions

        // Create an ArrayAdapter using the string array and a default spinner layout
        val materialSpinnerAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.test_stations_array,
            android.R.layout.simple_spinner_item //might want to change this
        )
        materialSpinner.setAdapter(materialSpinnerAdapter)

    }
}

