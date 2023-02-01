package com.stepa_0751.neco_gps_tracker.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.stepa_0751.neco_gps_tracker.R

class SetFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferense, rootKey)
    }
}