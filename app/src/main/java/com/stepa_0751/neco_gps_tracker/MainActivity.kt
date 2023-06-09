package com.stepa_0751.neco_gps_tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stepa_0751.neco_gps_tracker.databinding.ActivityMainBinding
import com.stepa_0751.neco_gps_tracker.fragments.MainFragment
import com.stepa_0751.neco_gps_tracker.fragments.SetFragment
import com.stepa_0751.neco_gps_tracker.fragments.TracksFragment
import com.stepa_0751.neco_gps_tracker.utils.openFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) //Привязка всех элементов в хмл
        setContentView(binding.root)                         // чтобы мы могли к ним обращаться
        onButtomNavClicks()
        openFragment(MainFragment.newInstance()) // собственно вызов функции переключения из Extentions.kt
    }                                              //при запуске приложения
    private fun onButtomNavClicks(){
        binding.bNav.setOnItemSelectedListener {  //  слушатель кнопок меню
            when(it.itemId){    // переключение фрагментов
                R.id.id_home -> openFragment(MainFragment.newInstance())
                R.id.id_tracks ->openFragment(TracksFragment.newInstance())
                R.id.id_settings ->openFragment(SetFragment())
            }
            true
        }
    }
}