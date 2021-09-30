package com.cos60011.group1.mttransit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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

        // enable menu on action bar
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }
}