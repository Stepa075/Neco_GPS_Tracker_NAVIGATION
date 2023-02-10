package com.stepa_0751.neco_gps_tracker

import android.app.Application
import com.stepa_0751.neco_gps_tracker.db.MainDb

class MainApp : Application() {
    val database by lazy { MainDb.getDatabase(this) }
}