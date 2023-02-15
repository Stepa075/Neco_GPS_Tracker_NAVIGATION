package com.stepa_0751.neco_gps_tracker

import androidx.lifecycle.*
import com.stepa_0751.neco_gps_tracker.db.MainDb
import com.stepa_0751.neco_gps_tracker.db.TrackItem
import com.stepa_0751.neco_gps_tracker.location.LocationModel
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDb): ViewModel() {
    val dao = db.getDao()
    val locationUpdates = MutableLiveData<LocationModel>()
    //  через muttableLiveData безопасно обновлять єлементы  view - ничего не сломается
    val timeData = MutableLiveData<String>()

    val tracks = dao.getAllTracks().asLiveData()

           //Функция записи в БД
    fun insertTrack(trackItem: TrackItem) = viewModelScope.launch {
        dao.insertTrack(trackItem)
    }

    class ViewModelFactory( private val db: MainDb) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {

                return MainViewModel(db) as T
            }
            throw java.lang.IllegalArgumentException("Unknown ViewModel class")
        }
    }
}