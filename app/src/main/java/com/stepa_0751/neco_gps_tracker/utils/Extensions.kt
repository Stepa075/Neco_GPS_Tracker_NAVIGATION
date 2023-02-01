package com.stepa_0751.neco_gps_tracker.utils

import android.util.Log
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
    //  так мы проверяем и визуально отслеживаем какое имя фрагмента вызывается приложением
    Log.d("MyLog", "Frag name: ${f.javaClass}")
    //  в списке запущенных фрагментов у нас всегда только 0 при запуске или 1 при переходе от
    //  фрагмента к фрагменту. Если список фрагментов не пуст...
    if(supportFragmentManager.fragments.isNotEmpty()){
        // и если название фрагмента равно открытому фрагменту - ничего не делаем
        if(supportFragmentManager.fragments[0].javaClass == f.javaClass) return
    }
    // если список фрагментов пуст(старт программы) или не совпадает по названию с текущим
    // выполняем замещение фрагмента вызываемым
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