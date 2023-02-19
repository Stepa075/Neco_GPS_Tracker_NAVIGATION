package com.stepa_0751.neco_gps_tracker.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stepa_0751.neco_gps_tracker.R
import com.stepa_0751.neco_gps_tracker.databinding.TrackItemBinding

class TrackAdapter(private val listener: Listener) : ListAdapter<TrackItem, TrackAdapter.Holder>(Comparator()) {
    class Holder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view),View.OnClickListener {
        private val binding = TrackItemBinding.bind(view)
        private var trackTemp: TrackItem? = null
        init {
            binding.ibDelete.setOnClickListener(this) // Привязка слушателя к кнопке удалить
            binding.item.setOnClickListener(this)      // Привязка слушателя к всему CardView
        }
        fun bind(track: TrackItem) = with(binding){
            trackTemp = track
            val speed = "${track.velosity} km/h"
            val distance = "${track.distanse} km"
            tvDate.text = track.date
            tvSpeedee.text = speed
            tvTimee.text = track.time
            tvDistance.text = distance

        }

        override fun onClick(view: View?) {                  // Выбор по айди типа объекта, на который нажали и действия
            if (view != null) {                                         //Лишняя проверка на не null???
            val type = when(view.id){
                    R.id.ibDelete ->  ClickType.DELETE
                    R.id.item ->  ClickType.OPEN
                    else -> ClickType.OPEN
                }
                trackTemp?.let{listener.onClick(it, type)}
            }


        }

    }
    class Comparator : DiffUtil.ItemCallback<TrackItem>(){
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(track: TrackItem, type: ClickType)
    }
    enum class ClickType{       // Класс, через который передается?? или из которого берутся константы??
        DELETE,
        OPEN
    }
}