package com.stepa_0751.neco_gps_tracker.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.stepa_0751.neco_gps_tracker.R

class SetFragment : PreferenceFragmentCompat() {
    private lateinit var timePref: Preference
    private lateinit var colorPref: Preference
    private lateinit var serverIp: Preference
    private lateinit var timeToSend: Preference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferense, rootKey)
        init()
    }
    private fun init(){
        timeToSend = findPreference("time_to_send_data_on_server") !!
        serverIp = findPreference("server_ip") !!
        timePref = findPreference("update_time_key")!!  //слушатель изменения
        colorPref = findPreference("color_key")!!  //слушатель изменения
        val changeListener = onChangeListener()             // его инициализатор
        timeToSend.onPreferenceChangeListener = changeListener
        serverIp.onPreferenceChangeListener = changeListener
        timePref.onPreferenceChangeListener = changeListener
        colorPref.onPreferenceChangeListener = changeListener
        initPrefs()
    }

    private fun onChangeListener(): OnPreferenceChangeListener{
        return Preference.OnPreferenceChangeListener{
            pref, value ->                               //  функция присвоения значения при изменении
            // выбор следующего действия по ключу из префа
            when(pref.key){
                    "update_time_key" -> onTimeChange(value.toString())
                    "server_ip" -> onTextChangeServer(value.toString())
                    "time_to_send_data_on_server" -> onTextChangeTimeToSent(value.toString())
                        // "функция" изменения цвета на титле при выборе цвета трека
                    "color_key" -> pref.icon?.setTint(Color.parseColor(value.toString()))
                }
            true
        }
    }

    private fun onTextChangeServer(value: String){
        serverIp.title = "Server IP: ${value}"
    }
    private fun onTextChangeTimeToSent(value: String){
        timeToSend.title = "Interval time sending data: ${value} sec"
    }


        //функция изменения текста в титле выбора скорости обновления
    private fun onTimeChange(value: String){
       // showToast("Value changed: $value")        //   выбора в списке настройки (3, 5, 10 секунд)
        // получаем наш титл из префа и обрезаем до двоеточия, чтобы не дописывались значения в титл
        val title = timePref.title.toString().substringBefore(":")
        //  объявляем в этой функции два массива, которые получаем из ресурсов
        val nameArray = resources.getStringArray(R.array.location_time_update_name)
        val valueArray = resources.getStringArray(R.array.location_time_update_value)
        //  подставляем в титл значение из массива названий по ключу из массива значений??????
        timePref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
    }

     // установка стартового значения в титле при запуске фрагмента
    private fun initPrefs(){
        val pref = timePref.preferenceManager.sharedPreferences
        val title = timePref.title
        //  объявляем в этой функции два массива, которые получаем из ресурсов
        val nameArray = resources.getStringArray(R.array.location_time_update_name)
        val valueArray = resources.getStringArray(R.array.location_time_update_value)
        //  подставляем в титл значение из массива названий по ключу из массива значений??????
        timePref.title = "$title: ${nameArray[valueArray.indexOf(pref?.getString("update_time_key", "3000"))]}"

        val trackColor = pref?.getString("color_key", "#FF041861")
         colorPref.icon?.setTint(Color.parseColor(trackColor))

         val setTitleServer = pref?.getString("server_ip", "Server IP")
         serverIp.title = "Server IP: $setTitleServer"

         val setTitleTimeToSent = pref?.getString("time_to_send_data_on_server", "60")
         timeToSend.title = "Interval time sending data: $setTitleTimeToSent sec."

    }
}