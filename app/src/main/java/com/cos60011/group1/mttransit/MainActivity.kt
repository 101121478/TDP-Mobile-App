package com.cos60011.group1.mttransit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cos60011.group1.mttransit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}