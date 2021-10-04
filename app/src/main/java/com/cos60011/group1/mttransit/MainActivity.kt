package com.cos60011.group1.mttransit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Link the NavController to the actionBar
        navController = this.findNavController(R.id.app_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // disable back button on BusBoard Fragment
        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nc.currentDestination?.id == R.id.busBoardFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // check if user has logged in and navigate to bus dashboard accordingly
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate(R.id.busBoardFragment)
        }
    }

    // enable navigateUp in nav controller
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        return navController.navigateUp()
    }
}