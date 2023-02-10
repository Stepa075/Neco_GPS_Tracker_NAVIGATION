package com.stepa_0751.neco_gps_tracker.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DAo {
    @Insert
    suspend fun insertTrack(trackItem: TrackItem)
}