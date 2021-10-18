package com.cos60011.group1.mttransit

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.cos60011.group1.mttransit.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize viewModel
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Link the NavController to the actionBar
        navController = this.findNavController(R.id.app_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // disable back button on BusBoard Fragment
        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nc.currentDestination?.id == R.id.busBoardFragment || nc.currentDestination?.id == R.id.setStationFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // check current fragment
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Initialize sharedPref
            sharedPreferences = getSharedPreferences("com.cos60011.group1.mttransit.settings.${currentUser.email.toString()}", Context.MODE_PRIVATE)

            val userLocation = sharedPreferences.getString("userLocation", "Unknown")
            try {
                if (userLocation.toString() == "Unknown") {
                    navController.navigate(R.id.action_loginFragment_to_setStationFragment)
                } else {
                    navController.navigate(R.id.action_loginFragment_to_busBoardFragment)
                }
            } catch (e: IllegalArgumentException) {
            }

        }
    }

    // enable navigateUp in nav controller
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        return navController.navigateUp()
    }
}