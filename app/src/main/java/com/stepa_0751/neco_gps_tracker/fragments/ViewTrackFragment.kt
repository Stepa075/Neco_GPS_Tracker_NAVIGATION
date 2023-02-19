package com.stepa_0751.neco_gps_tracker.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.stepa_0751.neco_gps_tracker.MainApp
import com.stepa_0751.neco_gps_tracker.MainViewModel
import com.stepa_0751.neco_gps_tracker.R
import com.stepa_0751.neco_gps_tracker.databinding.FragmentMainBinding
import com.stepa_0751.neco_gps_tracker.databinding.TracksBinding
import com.stepa_0751.neco_gps_tracker.databinding.ViewTrackBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig


class ViewTrackFragment : Fragment() {
    private lateinit var binding: ViewTrackBinding
    private val model: MainViewModel by activityViewModels{
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        binding = ViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrack()
    }

    private fun getTrack() = with(binding){
        model.currentTrack.observe(viewLifecycleOwner){
            val speed = "Average speed: ${it.velosity} km/h"
            val distance = "Distance: ${it.distanse} km"
            tvData.text = it.date
            tvTime.text = it.time
            tvAverege.text = speed
            tvDistanse.text = distance
        }
    }

    private fun settingsOsm(){
        Configuration.getInstance().load(
            activity as AppCompatActivity, //задаем доступ к данным библиотеки только для нашего приложения
            activity?.getSharedPreferences("osm-pref", Context.MODE_PRIVATE))
        //конфигурируем юзер агента
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()
        }
    }
