package com.stepa_0751.neco_gps_tracker.location

import org.osmdroid.util.GeoPoint

data class LocationModel(
    val velosity: Float = 0.0f,
    val distance: Float = 0.0f,
    val geoPointsList: ArrayList<GeoPoint>
): java.io.Serializable
