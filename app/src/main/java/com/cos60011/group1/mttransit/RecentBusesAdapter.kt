package com.cos60011.group1.mttransit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat

class RecentBusesAdapter (
    private val context: Context,
    private val options: FirestoreRecyclerOptions<Bus>,
    private val fragment: RecentBusesFragment
) :
    FirestoreRecyclerAdapter<Bus, RecentBusesAdapter.ViewHolder>(options) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int, bus: Bus) {

        holder.busListView.setOnClickListener { view: View ->

            //can add data to shared viewmodel here
            fragment.viewModel.setCurrentBus(holder.busIdView.text.toString())
            fragment.viewModel.setIsCurrentBus(false)
            fragment.viewModel.setRouteName(holder.busroute.text.toString())

            view.findNavController().navigate(R.id.action_recentBusesFragment_to_busStatusFragment)
        }

        val readableDepartureTime = SimpleDateFormat("HH:mm").format(bus.departureTime.toDate())

        // Set item views based on your views and data model
        holder.busIdView.text = bus.busId
        holder.busInfoView.text = context.resources.getString(R.string.departure_time_text, bus.previousStop, readableDepartureTime)
        holder.passengers.text = "${bus.passengers}/${bus.capacity}"
        holder.busroute.text = bus.routeName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //inflate the custom layout
        val busView = inflater.inflate(R.layout.item_recent_bus, parent, false)

        //return a new holder instance
        return ViewHolder(busView)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busIdView: TextView = itemView.findViewById(R.id.recent_bus_id)
        val busInfoView: TextView = itemView.findViewById(R.id.recent_bus_info)
        val busListView: MaterialCardView = itemView.findViewById(R.id.recent_bus_card)
        val passengers: TextView = itemView.findViewById((R.id.passenger_count))
        val busroute: TextView = itemView.findViewById(R.id.recent_bus_route)
    }

}