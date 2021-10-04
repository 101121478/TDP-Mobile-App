package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.databinding.DataBindingUtil
import com.cos60011.group1.mttransit.databinding.FragmentBusStatusBinding
import com.google.firebase.database.collection.LLRBNode

class BusStatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBusStatusBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bus_status, container, false)
        return binding.root
    }

}