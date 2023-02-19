package com.stepa_0751.neco_gps_tracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stepa_0751.neco_gps_tracker.MainApp
import com.stepa_0751.neco_gps_tracker.MainViewModel
import com.stepa_0751.neco_gps_tracker.R
import com.stepa_0751.neco_gps_tracker.databinding.FragmentMainBinding
import com.stepa_0751.neco_gps_tracker.databinding.TracksBinding
import com.stepa_0751.neco_gps_tracker.databinding.ViewTrackBinding
import com.stepa_0751.neco_gps_tracker.db.TrackAdapter
import com.stepa_0751.neco_gps_tracker.db.TrackItem
import com.stepa_0751.neco_gps_tracker.utils.openFragment


class TracksFragment : Fragment(), TrackAdapter.Listener{
    private lateinit var binding: TracksBinding
    private lateinit var adapter: TrackAdapter
    private val model: MainViewModel by activityViewModels{
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        getTracks()
    }

    private fun getTracks(){
        model.tracks.observe(viewLifecycleOwner){
            adapter.submitList(it)
            // Так убирается или устанавливается надпись Empty на TrackFragment
            binding.tvEmpty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE

        }
    }
    private fun initRcView() = with(binding){
        adapter = TrackAdapter(this@TracksFragment)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }



    companion object {
        @JvmStatic
        fun newInstance() = TracksFragment()
        }
                //Выбор действия в зависимости от того, на что нажал пользователь, на удалить или просто на маршрут
    override fun onClick(track: TrackItem, type: TrackAdapter.ClickType) {
        when(type){
            TrackAdapter.ClickType.DELETE -> model.deleteTrack(track)
            TrackAdapter.ClickType.OPEN -> {
                model.currentTrack.value = track
                openFragment(ViewTrackFragment.newInstance())
            }

        }

    }
}
