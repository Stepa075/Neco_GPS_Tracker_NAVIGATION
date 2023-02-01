package com.stepa_0751.neco_gps_tracker.fragments


import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stepa_0751.neco_gps_tracker.databinding.FragmentMainBinding
import com.stepa_0751.neco_gps_tracker.utils.checkPermission
import com.stepa_0751.neco_gps_tracker.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // !!!Нужно настроить библиотеку OSMAndroid до инициализации фрагмента(разметки)!!!
        settingsOsm()
        //  Собственно именно здесь загружается разметка, в Inflate!!!
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        checkLockPermission()
    }


    // конфиг для осм библиотеки
    private fun settingsOsm(){
        Configuration.getInstance().load(
            activity as AppCompatActivity, //задаем доступ к данным библиотеки только для нашего приложения
        activity?.getSharedPreferences("osm-pref", Context.MODE_PRIVATE))
        //конфигурируем юзер агента
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOSM() = with(binding){
        map.controller.setZoom(16.0)
        //создание оверлеев наложений на карту и подключение к map
        val mLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(mLocProvider, map)
        myLocOverlay.enableMyLocation()
        myLocOverlay.enableFollowLocation()
        myLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(myLocOverlay)
        }
    }

    private fun registerPermissions(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts
            .RequestMultiplePermissions()){
            if(it[ACCESS_FINE_LOCATION] == true){                // здесь не добавлено Manifest.permission.
                initOSM()                                        //  будет ли работать????

            }else{
                showToast("Не дано разрешение на использование местоположения!!!")
            }
        }
    }
    // проверка версии андроида
    private fun checkLockPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            checkPermissionAfter10()
        }else{
            checkPermissionBefore10()
        }
    }
    // проверка разрешений для андроид версии 10 и выше
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAfter10() {
        if (checkPermission(ACCESS_FINE_LOCATION) && checkPermission(ACCESS_BACKGROUND_LOCATION)) {
            initOSM()
        } else {
            pLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION))
        }
    }

    // проверка разрешений для андроид версии ниже 10
    private fun checkPermissionBefore10() {
        if (checkPermission(ACCESS_FINE_LOCATION)) {
            initOSM()
        } else {
            pLauncher.launch(arrayOf(ACCESS_FINE_LOCATION))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        }
    }
