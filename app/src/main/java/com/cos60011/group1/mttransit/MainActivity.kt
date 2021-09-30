package com.cos60011.group1.mttransit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.cos60011.group1.mttransit.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Link the NavController to the actionBar
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // disable back button on BusBoard Fragment
        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nc.currentDestination?.id == R.id.busBoardFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    // enable navigateUp in nav controller
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        return navController.navigateUp()
    }
}