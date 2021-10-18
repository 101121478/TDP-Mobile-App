package com.cos60011.group1.mttransit.screens.dashboard

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cos60011.group1.mttransit.Bus
import com.cos60011.group1.mttransit.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat


// Creates an adapter extending from FirestoreRecyclerAdapter
class BusCardAdapter(
    private val context: Context,
    private val options: FirestoreRecyclerOptions<Bus>,
    private val fragment: BusCardsFragment) :
    FirestoreRecyclerAdapter<Bus, BusCardAdapter.ViewHolder>(options) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int, bus: Bus) {
            holder.btnView.setOnClickListener { view : View ->

                //can add data to shared viewmodel here
                fragment.viewModel.setCurrentBus(holder.busIdView.text.toString())
                fragment.viewModel.setIsCurrentBus(true)

                view.findNavController().navigate(R.id.action_busBoardFragment_to_busStatusFragment)
            }

            val readableArrivalTime = SimpleDateFormat("HH:mm").format(bus.arrivalTime.toDate())

            // Set item views based on your views and data model
            holder.busIdView.text = bus.busId
            holder.routeView.text = bus.routeName
            holder.busArrivalView.text = context.resources.getString(R.string.arrival_time_text, readableArrivalTime)
            holder.passengers.text = "${bus.passengers}/${bus.capacity}"
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)

            //inflate the custom layout
            val busView = inflater.inflate(R.layout.item_bus_card, parent, false)

            //return a new holder instance
            return ViewHolder(busView)
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnView: MaterialCardView = itemView.findViewById(R.id.bus_card)
        val busIdView: TextView = itemView.findViewById(R.id.bus_card_title)
        val routeView: TextView = itemView.findViewById(R.id.current_bus_route)
        val busArrivalView: TextView = itemView.findViewById(R.id.current_bus_arrival)
        val passengers: TextView = itemView.findViewById((R.id.passenger_count))
    }

}