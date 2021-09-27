package com.cos60011.group1.mttransit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class BusListAdapter(private val busList: List<Bus>) : RecyclerView.Adapter<BusListAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val busIdView = itemView.findViewById<TextView>(R.id.bus_list_title)
        val busInfoView = itemView.findViewById<TextView>(R.id.bus_list_info)

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
        val bus: Bus = busList.get(position)

        // Set item views based on your views and data model
        val busTitle = holder.busIdView
        val busInfo = holder.busInfoView

        busTitle.text = bus.id
        busInfo.text = "Departed ${bus.location} at ${bus.departure}"

    }

    override fun getItemCount(): Int {
        return busList.size

    }
}