package com.stepa_0751.neco_gps_tracker.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stepa_0751.neco_gps_tracker.R

fun Fragment.openFragment(f: Fragment){
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // установка анимации
        .replace(R.id.placeHolder, f).commit()   // при переключении фрагментов в теле place_Holder
}                                                // на activity_main.xml

fun AppCompatActivity.openFragment(f: Fragment){
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, f).commit()
}

fun Fragment.showToast(s: String){          //  функция вызова тостов в фрагменте
    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(s: String){      //  функция вызова тостов в активити
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}