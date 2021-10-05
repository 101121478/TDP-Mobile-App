package com.cos60011.group1.mttransit.screens.busstatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.databinding.FragmentBusStatusBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        showProgressIndicator()

        // Initialize viewModel
        viewModelFactory = BusStatusViewModelFactory("sample_bus")
        viewModel = ViewModelProvider(this, viewModelFactory).get(BusStatusViewModel::class.java)

        binding.busStatusViewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.busID.observe(viewLifecycleOwner, { busID ->
            if (busID != null) {
                hideProgressIndicator()
            }
        })

        // handle mark as arrive button
        binding.buttonBusStatusArrive.setOnClickListener { view: View ->
            viewModel.updateDocument()
        }

        // handle update success and failure
        viewModel.isUpdate.observe(viewLifecycleOwner, { isUpdate ->
            run {
                if (isUpdate == true) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Success update")
                        .setMessage("The Bus ${viewModel.busID.value} was marked arrived.")
                        .setPositiveButton("OK",) { dialog, which ->
                            dialog.dismiss()
                        }.show()
                } else {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Update error")
                        .setMessage("Failure to update bus ${viewModel.busID.value} as arrived,\nplease try again.")
                        .setPositiveButton("OK",) { dialog, which ->
                            dialog.dismiss()
                        }.show()
                }
            }
        })

        return binding.root
    }

    private fun showProgressIndicator() {
        binding.busStatusBusIdLabel.visibility = View.GONE
        binding.busStatusBusIdText.visibility = View.GONE
        binding.busStatusBusTypeLabel.visibility = View.GONE
        binding.busStatusBusTypeText.visibility = View.GONE
        binding.busStatusBusRouteLabel.visibility = View.GONE
        binding.busStatusBusRouteText.visibility = View.GONE
        binding.busStatusPassengerCapacityLabel.visibility = View.GONE
        binding.busStatusPassengerCapacityText.visibility = View.GONE
        binding.busStatusPassengerOnboardLabel.visibility = View.GONE
        binding.textInputBusStatusPassengerOnboard.visibility = View.GONE
        binding.buttonBusStatusArrive.visibility = View.GONE
        binding.buttonBusStatusDepart.visibility = View.GONE
    }

    private fun hideProgressIndicator() {
        binding.progressCircular.visibility = View.GONE
        binding.busStatusBusIdLabel.visibility = View.VISIBLE
        binding.busStatusBusIdText.visibility = View.VISIBLE
        binding.busStatusBusTypeLabel.visibility = View.VISIBLE
        binding.busStatusBusTypeText.visibility = View.VISIBLE
        binding.busStatusBusRouteLabel.visibility = View.VISIBLE
        binding.busStatusBusRouteText.visibility = View.VISIBLE
        binding.busStatusPassengerCapacityLabel.visibility = View.VISIBLE
        binding.busStatusPassengerCapacityText.visibility = View.VISIBLE
        binding.busStatusPassengerOnboardLabel.visibility = View.VISIBLE
        binding.textInputBusStatusPassengerOnboard.visibility = View.VISIBLE
        binding.buttonBusStatusArrive.visibility = View.VISIBLE
        binding.buttonBusStatusDepart.visibility = View.VISIBLE
    }

}