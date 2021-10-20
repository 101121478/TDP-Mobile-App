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
        val isRecentBus = sharedViewModel.isRecentBus.value
        val routeName = sharedViewModel.routeName.value

        // Initialize viewModel
        viewModelFactory = BusStatusViewModelFactory(userLocation.toString(), busIdRef.toString(), routeName.toString(), isCurrentBus!!, isRecentBus!!)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BusStatusViewModel::class.java)

        binding.busStatusViewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        // show UI after get data from server
        viewModel.passengerOnBoard.observe(viewLifecycleOwner, { passengerOnBoard ->
            if (passengerOnBoard != null) {
                hideProgressIndicator(isCurrentBus, isRecentBus)
            }
        })

        // handle swipe refresh
        binding.busStatusSwipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
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

        val passengerOffBoarding = binding.textInputBusStatusPassengerOffboard
        val passengerOnBoarding = binding.textInputBusStatusPassengerBoarding
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // handle mark as departed button
        binding.buttonBusStatusDepart.setOnClickListener {
            passengerOffBoarding.error = ""
            passengerOnBoarding.error = ""

            val offBoard = passengerOffBoarding.editText?.text!!.trim()
            val boarding = passengerOnBoarding.editText?.text!!.trim()

            if (offBoard.isEmpty()) {
                passengerOffBoarding.error = "The disembarking passenger field is required."
            } else if (boarding.isEmpty()) {
                passengerOnBoarding.error = "The boarding passengers field is required."
            } else {
                val capacity = viewModel.passengerCapacity.value.toString().toInt()
                val currentTotalPassengers = viewModel.passengerOnBoard.value.toString().toInt()
                val newTotalPassengers = currentTotalPassengers - offBoard.toString().toInt() + boarding.toString().toInt()

                if (offBoard.toString().toInt() > currentTotalPassengers) {
                    passengerOffBoarding.error = "The disembarking passengers $offBoard exceeds the number of passengers onboard $currentTotalPassengers."
                } else if (newTotalPassengers > capacity) {
                    passengerOnBoarding.error = "The number of passengers $newTotalPassengers exceeds the bus capacity $capacity."
                } else {
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                    viewModel.markDeparture(offBoard.toString(), boarding.toString())
                }
            }
        }

        // handle unmark from departed button
        binding.buttonBusStatusUnmarkFromDeparted.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Do you want to unmark bus ${viewModel.busId.value} from departed?")
                .setNegativeButton("CANCEL") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("UNMARK") { dialog, which ->
                    viewModel.unmarkFromDeparted()
                }
                .show()
        }

        // handle unmark from departed success and failure
        viewModel.isUnmarkDeparted.observe(viewLifecycleOwner, { isUnmarkDeparted ->
            if (isUnmarkDeparted) {
                view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                val message = "The Bus ${viewModel.busId.value} was unmarked from departed."
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
            } else {
                val title = "Error"
                val message = "Failure to unmark bus ${viewModel.busId.value} from departed,\n" +
                        "please try again."
                showDialog(title, message)
            }
        })

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

        // handle mark as departed success and failure
        viewModel.isDeparture.observe(viewLifecycleOwner, { isDeparture ->
            run {
                if (isDeparture) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                    val message = "The Bus ${viewModel.busId.value} was marked as departed."
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } else {
                    val title = "Error"
                    val message = "Failure to mark bus ${viewModel.busId.value} as departed,\n" +
                            "please try again."
                    showDialog(title, message)
                }
            }
        })

        // handle update passengers button
        binding.buttonBusStatusUpdatePassengersCount.setOnClickListener {

            passengerOffBoarding.error = ""
            passengerOnBoarding.error = ""

            val offBoard = passengerOffBoarding.editText?.text!!.trim()
            val boarding = passengerOnBoarding.editText?.text!!.trim()

            if (offBoard.isEmpty()) {
                passengerOffBoarding.error = "The disembarking passenger field is required."
            } else if (boarding.isEmpty()) {
                passengerOnBoarding.error = "The boarding passengers field is required."
            } else {
                val capacity = viewModel.passengerCapacity.value.toString().toInt()
                val currentTotalPassengers = viewModel.passengerOnBoard.value.toString().toInt()
                val newTotalPassengers = currentTotalPassengers - offBoard.toString().toInt() + boarding.toString().toInt()

                if (offBoard.toString().toInt() > currentTotalPassengers) {
                    passengerOffBoarding.error = "The disembarking passengers $offBoard exceeds the number of passengers onboard $currentTotalPassengers."
                } else if (newTotalPassengers > capacity) {
                    passengerOnBoarding.error = "The number of passengers $newTotalPassengers exceeds the bus capacity $capacity."
                } else {
                    imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Do you want to update passengers count on bus ${viewModel.busId.value}?")
                        .setNegativeButton("CANCEL") { dialog, which ->
                            dialog.cancel()
                        }
                        .setPositiveButton("UPDATE") { dialog, which ->
                            // TODO call unmark function
                            viewModel.updatePassengerCount(offBoard.toString(), boarding.toString())
                        }
                        .show()
                }
            }

        }

        // handle update passenger success and failure
        viewModel.isPassengerCountUpdated.observe(viewLifecycleOwner, { isPassengerCountUpdated ->
            run {
                if (isPassengerCountUpdated) {
                    view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_recentBusesFragment)
                    val message = "The Bus ${viewModel.busId.value} passengers was updated."
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } else {
                    val title = "Error"
                    val message = "Failure to update bus ${viewModel.busId.value} passengers,\n" +
                            "please try again."
                    showDialog(title, message)
                }
            }
        })

        // handle marking conflict by multiple users
        viewModel.isMarked.observe(viewLifecycleOwner, { isMarked ->
            if (isMarked) {
                view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                val title = "Error"
                val message = "The bus ${viewModel.busId.value} was marked by other staffs already."
                showDialog(title, message)
            }
        })

        // handle db error
        viewModel.isMarked.observe(viewLifecycleOwner, { isMarked ->
            if (isMarked) {
                view?.findNavController()?.navigate(R.id.action_busStatusFragment_to_busBoardFragment)
                val title = "Error"
                val message = "Something went wrong, please try again."
                showDialog(title, message)
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
        binding.busStatusBusRouteLabel.visibility = View.INVISIBLE
        binding.busStatusBusRouteText.visibility = View.INVISIBLE
        binding.busStatusPassengerCapacityLabel.visibility = View.INVISIBLE
        binding.busStatusPassengerCapacityText.visibility = View.INVISIBLE
        binding.busStatusPassengerOnboardLabel.visibility = View.INVISIBLE
        binding.busStatusPassengerOnboardText.visibility = View.INVISIBLE
        binding.busStatusPassengerOffboardLabel.visibility = View.INVISIBLE
        binding.textInputBusStatusPassengerOffboard.visibility = View.INVISIBLE
        binding.busStatusPassengerBoardingLabel.visibility = View.INVISIBLE
        binding.textInputBusStatusPassengerBoarding.visibility = View.INVISIBLE
        binding.buttonBusStatusArrive.visibility = View.INVISIBLE
        binding.buttonBusStatusDepart.visibility = View.INVISIBLE
        binding.buttonBusStatusUnmarkFromDeparted.visibility = View.INVISIBLE
        binding.buttonBusStatusUpdatePassengersCount.visibility = View.INVISIBLE
    }

    private fun hideProgressIndicator(isCurrentBus: Boolean, isRecentBus: Boolean) {
        binding.progressCircular.visibility = View.GONE
        binding.busStatusBusIdLabel.visibility = View.VISIBLE
        binding.busStatusBusIdText.visibility = View.VISIBLE
        binding.busStatusBusRouteLabel.visibility = View.VISIBLE
        binding.busStatusBusRouteText.visibility = View.VISIBLE
        binding.busStatusPassengerCapacityLabel.visibility = View.VISIBLE
        binding.busStatusPassengerCapacityText.visibility = View.VISIBLE
        binding.busStatusPassengerOnboardLabel.visibility = View.VISIBLE
        binding.busStatusPassengerOnboardText.visibility = View.VISIBLE

        if (isCurrentBus) {
            binding.buttonBusStatusArrive.visibility = View.GONE
            binding.buttonBusStatusUnmarkFromDeparted.visibility = View.GONE
            binding.buttonBusStatusUpdatePassengersCount.visibility = View.GONE
            binding.buttonBusStatusDepart.visibility = View.VISIBLE
            binding.busStatusPassengerOffboardLabel.visibility = View.VISIBLE
            binding.textInputBusStatusPassengerOffboard.visibility = View.VISIBLE
            binding.busStatusPassengerBoardingLabel.visibility = View.VISIBLE
            binding.textInputBusStatusPassengerBoarding.visibility = View.VISIBLE

        } else if (isRecentBus) {
            binding.buttonBusStatusArrive.visibility = View.GONE
            binding.buttonBusStatusDepart.visibility = View.GONE
            binding.buttonBusStatusUpdatePassengersCount.visibility = View.VISIBLE
            binding.buttonBusStatusUnmarkFromDeparted.visibility = View.VISIBLE
            binding.busStatusPassengerOffboardLabel.visibility = View.VISIBLE
            binding.textInputBusStatusPassengerOffboard.visibility = View.VISIBLE
            binding.busStatusPassengerBoardingLabel.visibility = View.VISIBLE
            binding.textInputBusStatusPassengerBoarding.visibility = View.VISIBLE

        } else {
            binding.buttonBusStatusArrive.visibility = View.VISIBLE
            binding.buttonBusStatusDepart.visibility = View.GONE
            binding.buttonBusStatusUnmarkFromDeparted.visibility = View.GONE
            binding.buttonBusStatusUpdatePassengersCount.visibility = View.GONE
            binding.busStatusPassengerOffboardLabel.visibility = View.GONE
            binding.textInputBusStatusPassengerOffboard.visibility = View.GONE
            binding.busStatusPassengerBoardingLabel.visibility = View.GONE
            binding.textInputBusStatusPassengerBoarding.visibility = View.GONE

        }
    }

}