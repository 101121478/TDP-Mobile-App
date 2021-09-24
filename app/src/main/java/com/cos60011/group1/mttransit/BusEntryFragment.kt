package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.cos60011.group1.mttransit.databinding.FragmentBusEntryBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BusEntryFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        val binding = FragmentBusEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

}