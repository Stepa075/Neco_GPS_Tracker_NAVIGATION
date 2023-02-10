package com.stepa_0751.neco_gps_tracker.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "track")
data class TrackItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "distance")
    val distanse: String,
    @ColumnInfo(name = "velocity")
    val velosity: String,
    @ColumnInfo(name = "geo_points")
    val geoPoints: String,
)
