package com.stepa_0751.neco_gps_tracker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stepa_0751.neco_gps_tracker.location.LocationModel

class MainViewModel : ViewModel() {
    val locationUpdates = MutableLiveData<LocationModel>()
    //  через muttableLiveData безопасно обновлять єлементы  view - ничего не сломается
    val timeData = MutableLiveData<String>()
}