package com.cos60011.group1.mttransit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class BusCardAdapter(private val busList: List<Bus>) : RecyclerView.Adapter<BusCardAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val busIdView: TextView = itemView.findViewById<TextView>(R.id.bus_card_title)
        val routeView: TextView = itemView.findViewById<TextView>(R.id.current_bus_route)
        val busArrivalView: TextView = itemView.findViewById<TextView>(R.id.current_bus_arrival)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //inflate the custom layout
        val busView = inflater.inflate(R.layout.item_bus_card, parent, false)

        //return a new holder instance
        return ViewHolder(busView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val bus: Bus = busList.get(position)

        // Set item views based on your views and data model
        holder.busIdView.text = bus.id
        holder.routeView.text = bus.route
        holder.busArrivalView.text = "Arrived at ${bus.arrival}"
    }

    override fun getItemCount(): Int {
        return busList.size

    }
}