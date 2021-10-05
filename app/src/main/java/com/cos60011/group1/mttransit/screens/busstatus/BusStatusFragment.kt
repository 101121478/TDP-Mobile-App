package com.cos60011.group1.mttransit.screens.busstatus

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
        // TODO GET bus document reference of selected bus from previous screen
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
        binding.buttonBusStatusArrive.setOnClickListener {
            viewModel.markArrive()
        }

        val passengerOnboard = binding.textInputBusStatusPassengerOnboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // handle mark as departure button
        binding.buttonBusStatusDepart.setOnClickListener {
            passengerOnboard.error = ""
            val onboard = passengerOnboard.editText!!.text

            if (onboard.isEmpty()) {
                passengerOnboard.error = "The passenger onboard field is required."
            } else {
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                viewModel.markDeparture(onboard.toString())
            }
        }

        // handle mark as arrive success and failure
        viewModel.isArrive.observe(viewLifecycleOwner, { isArrive ->
            run {
                if (isArrive == true) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    val title = "Success"
                    val message = "The Bus ${viewModel.busID.value} was marked arrived."
                    showDialog(title, message)
                } else {
                    val title = "Error"
                    val message = "Failure to mark bus ${viewModel.busID.value} as arrived,\n" +
                            "please try again."
                    showDialog(title, message)
                }
            }
        })

        // handle mark as departure success and failure
        viewModel.isDeparture.observe(viewLifecycleOwner, { isDeparture ->
            run {
                if (isDeparture == true) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    val title = "Success"
                    val message = "The Bus ${viewModel.busID.value} was marked as departure."
                    showDialog(title, message)
                } else {
                    val title = "Error"
                    val message = "Failure to mark bus ${viewModel.busID.value} as departure,\n" +
                            "please try again."
                    showDialog(title, message)
                }
            }
        })

        return binding.root
    }

    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK",) { dialog, which ->
                dialog.dismiss()
            }.show()
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