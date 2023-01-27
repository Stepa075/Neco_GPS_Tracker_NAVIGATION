package com.stepa_0751.neco_gps_tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stepa_0751.neco_gps_tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
    }
}