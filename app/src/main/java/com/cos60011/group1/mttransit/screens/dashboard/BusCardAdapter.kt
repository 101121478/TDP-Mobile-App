package com.cos60011.group1.mttransit.screens.dashboard

import android.content.Context
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

// Creates an adapter extending from FirestoreRecyclerAdapter
class BusCardAdapter(
    private val context: Context,
    private val options: FirestoreRecyclerOptions<Bus>) :
    FirestoreRecyclerAdapter<Bus, BusCardAdapter.ViewHolder>(options) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int, bus: Bus) {
            holder.btnView.setOnClickListener { view : View ->
                view.findNavController().navigate(R.id.action_busBoardFragment_to_busStatusFragment)
            }

            // Set item views based on your views and data model
            //TODO: Update arrival string once data structure finalised
            holder.busIdView.text = bus.name
            holder.routeView.text = bus.route
            holder.busArrivalView.text = context.resources.getString(R.string.arrival_time_text, bus.arrival)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)

            //inflate the custom layout
            val busView = inflater.inflate(R.layout.item_bus_card, parent, false)

            //return a new holder instance
            return ViewHolder(busView)
        }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val btnView: MaterialCardView = itemView.findViewById(R.id.bus_card)
        val busIdView: TextView = itemView.findViewById(R.id.bus_card_title)
        val routeView: TextView = itemView.findViewById(R.id.current_bus_route)
        val busArrivalView: TextView = itemView.findViewById(R.id.current_bus_arrival)
    }

}