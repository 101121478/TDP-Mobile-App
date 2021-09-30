package com.cos60011.group1.mttransit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.cos60011.group1.mttransit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Link the NavController to the actionBar
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    // enable navigateUp in nav controller
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.app_nav_host_fragment)
        return navController.navigateUp()
    }
}