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
import com.cos60011.group1.mttransit.SharedViewModel
import com.cos60011.group1.mttransit.databinding.FragmentBusStatusBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class BusStatusFragment : Fragment() {
    private lateinit var binding: FragmentBusStatusBinding
    private lateinit var viewModel: BusStatusViewModel
    private lateinit var viewModelFactory: BusStatusViewModelFactory
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_bus_status, container, false)

        showProgressIndicator()

        // get routeID, busId and isCurrentBus
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val busIdRef = sharedViewModel.currentBus.value
        val userLocation = sharedViewModel.userLocation.value
        val isCurrentBus = sharedViewModel.isCurrentBus.value
        println(busIdRef)
        println(userLocation)
        println(isCurrentBus)

        // Initialize viewModel
        // TODO GET route document reference and bus document reference of selected bus from previous screen
        viewModelFactory = BusStatusViewModelFactory("frankstonToCBD","973")
        viewModel = ViewModelProvider(this, viewModelFactory).get(BusStatusViewModel::class.java)

        binding.busStatusViewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        // show UI after get data from server
        viewModel.busId.observe(viewLifecycleOwner, { busId ->
            if (busId != null) {
                hideProgressIndicator()
            }
        })

        // handle swipe refresh
        binding.busStatusSwipeRefresh.setOnRefreshListener {
            viewModel.getBusDocument()
            viewModel.isUpdate.observe(viewLifecycleOwner, {isUpdate ->
                if (isUpdate) {
                    binding.busStatusSwipeRefresh.isRefreshing = false
                    Snackbar.make(requireView(), "Success update", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(requireView(), "Fail to update, please try again", Snackbar.LENGTH_LONG).show()
                }
            })
        }

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
                viewModel.markDeparture()
            }
        }

        // handle mark as arrive success and failure
        viewModel.isArrive.observe(viewLifecycleOwner, { isArrive ->
            run {
                if (isArrive) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    val message = "The Bus ${viewModel.busId.value} was marked as arrived."
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } else {
                    val title = "Error"
                    val message = "Failure to mark bus ${viewModel.busId.value} as arrived,\n" +
                            "please try again."
                    showDialog(title, message)
                }
            }
        })

        // handle mark as departure success and failure
        viewModel.isDeparture.observe(viewLifecycleOwner, { isDeparture ->
            run {
                if (isDeparture) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    val message = "The Bus ${viewModel.busId.value} was marked as departure."
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } else {
                    val title = "Error"
                    val message = "Failure to mark bus ${viewModel.busId.value} as departure,\n" +
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
        binding.busStatusBusIdLabel.visibility = View.INVISIBLE
        binding.busStatusBusIdText.visibility = View.INVISIBLE
        binding.busStatusBusTypeLabel.visibility = View.INVISIBLE
        binding.busStatusBusTypeText.visibility = View.INVISIBLE
        binding.busStatusBusRouteLabel.visibility = View.INVISIBLE
        binding.busStatusBusRouteText.visibility = View.INVISIBLE
        binding.busStatusPassengerCapacityLabel.visibility = View.INVISIBLE
        binding.busStatusPassengerCapacityText.visibility = View.INVISIBLE
        binding.busStatusPassengerOnboardLabel.visibility = View.INVISIBLE
        binding.textInputBusStatusPassengerOnboard.visibility = View.INVISIBLE
        binding.buttonBusStatusArrive.visibility = View.INVISIBLE
        binding.buttonBusStatusDepart.visibility = View.INVISIBLE
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