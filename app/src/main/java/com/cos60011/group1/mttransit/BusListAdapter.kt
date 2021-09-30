package com.cos60011.group1.mttransit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class BusListAdapter(
    private val context: Context,
    private val busList: List<Bus>) :
    RecyclerView.Adapter<BusListAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val busIdView: TextView = itemView.findViewById(R.id.bus_list_title)
        val busInfoView: TextView = itemView.findViewById(R.id.bus_list_info)
        val busListView: MaterialCardView = itemView.findViewById(R.id.bus_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //inflate the custom layout
        val busView = inflater.inflate(R.layout.item_bus_list, parent, false)

        //return a new holder instance
        return ViewHolder(busView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val bus: Bus = busList[position]

        holder.busListView.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_busBoardFragment_to_busStatusFragment)
        }
        // Set item views based on your views and data model
        holder.busIdView.text = bus.id
        holder.busInfoView.text = context.resources.getString(R.string.departure_time_text, bus.location, bus.departure)

    }

    override fun getItemCount(): Int {
        return busList.size

    }
}