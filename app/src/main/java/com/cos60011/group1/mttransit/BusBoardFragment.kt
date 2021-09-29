package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.databinding.FragmentBusBoardBinding

class BusBoardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBusBoardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bus_board, container, false)

        binding.addBusBtn.setOnClickListener{ view: View ->
            view.findNavController().navigate(R.id.action_busBoardFragment_to_busEntryFragment)
        }
        return binding.root
    }

}