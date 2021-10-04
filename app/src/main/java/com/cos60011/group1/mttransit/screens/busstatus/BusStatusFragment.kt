package com.cos60011.group1.mttransit.screens.busstatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.databinding.FragmentBusStatusBinding

class BusStatusFragment : Fragment() {
    private lateinit var binding: FragmentBusStatusBinding
    private lateinit var viewModel: BusStatusViewModel
    private lateinit var viewModelFactory: BusStatusViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_bus_status, container, false)

        // Initialize viewModel
        viewModelFactory = BusStatusViewModelFactory("sample_bus")
        viewModel = ViewModelProvider(this, viewModelFactory).get(BusStatusViewModel::class.java)

        // pass it to xml
        binding.busStatusViewModel = viewModel

        // set lifecycle of the data binding to this
        binding.lifecycleOwner = this

        return binding.root
    }

}