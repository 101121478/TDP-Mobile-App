package com.cos60011.group1.mttransit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    lateinit var buses : ArrayList<Bus>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lookup the recyclerview in activity layout
        val rvBusCards = findViewById<View>(R.id.bus_recycler) as RecyclerView
        val rvBusList = findViewById<View>(R.id.bus_list_recycler) as RecyclerView

        // Initialize buses
        buses = Bus.createTestBuses(10)

        // Create adapter passing in the sample user data
        val cardAdapter = BusCardAdapter(buses)
        val listAdapter = BusListAdapter(buses)

        // Attach the adapter to the recyclerview to populate items
        rvBusCards.adapter = cardAdapter
        rvBusList.adapter = listAdapter

        // Set layout manager to position the items
        rvBusCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvBusList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



    }
}