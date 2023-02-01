package com.stepa_0751.neco_gps_tracker.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.stepa_0751.neco_gps_tracker.R
import com.stepa_0751.neco_gps_tracker.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // !!!Нужно настроить библиотеку OSMAndroid до инициализации фрагмента(разметки)!!!
        settingsOsm()
        //  Собственно именно здесь загружается разметка, в Inflate!!!
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    // конфиг для осм библиотеки
    private fun settingsOsm(){
        Configuration.getInstance().load(
            activity as AppCompatActivity, //задаем доступ к оанным библиотеки только для нашего приложения
        activity?.getSharedPreferences("osm-pref", Context.MODE_PRIVATE))
        //конфигурируем юзер агента
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        }
    }
