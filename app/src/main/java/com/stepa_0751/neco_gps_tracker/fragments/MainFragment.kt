package com.stepa_0751.neco_gps_tracker.fragments


import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.stepa_0751.neco_gps_tracker.MainViewModel
import com.stepa_0751.neco_gps_tracker.R
import com.stepa_0751.neco_gps_tracker.databinding.FragmentMainBinding
import com.stepa_0751.neco_gps_tracker.location.LocationModel
import com.stepa_0751.neco_gps_tracker.location.LocationService
import com.stepa_0751.neco_gps_tracker.utils.DialogManager
import com.stepa_0751.neco_gps_tracker.utils.TimeUtils
import com.stepa_0751.neco_gps_tracker.utils.checkPermission
import com.stepa_0751.neco_gps_tracker.utils.showToast
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MainFragment : Fragment() {
    private var pl: Polyline? = null
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainBinding
    private var isServiceRunning = false
    private var firstStart = true
    private var timer: Timer? = null
    private var startTime = 0L
    private val model: MainViewModel by activityViewModels()

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
        setOnClicks()
        checkServiceState()
        updateTime()
        registerLocReceiver()
        locationUpdates()


        //Запуск сервиса  геолокации вместе с приложением !
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
//            activity?.startForegroundService((Intent(activity, LocationService::class.java)))
//        }else{
//            activity?.startService(Intent(activity, LocationService::class.java))
//        }

    }

    private fun setOnClicks() = with(binding){
        val listener = onClicks()
        fStartStop.setOnClickListener(listener)

    }

    // Нужно присваивать всем кнопкам не этот слушатель(т.к. тогда создаются новые инстансы
    // этой функции, а нужно создавать переменную в которую записывать эту функцию и
    // передавать всем тем, кому нужен слушатель!

    private fun onClicks(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                R.id.fStartStop -> startStopService()
            }
        }
    }

    private fun locationUpdates() = with(binding){
        model.locationUpdates.observe(viewLifecycleOwner){
        val distance = "Distance: ${String.format("%.1f", it.distance)} m"    // Установка количества знаков после запятой
        val velosity = "Velosity: ${String.format("%.1f", 3.6f * it.velosity)} km/h" // перевод из
        val aVelosity = "Average velosity: ${getAverageSpeed(it.distance)} km/h"
        tvDistanse.text = distance                                         // метров в секунду в километры в час
        tvVelosity.text = velosity
        tvAverege.text = aVelosity
        updatePolyline(it.geoPointsList)
        }
    }

    private fun updateTime(){
        model.timeData.observe(viewLifecycleOwner){
            binding.tvTime.text = it
        }
    }

    private fun startTimer(){
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask(){override fun run() {
            activity?.runOnUiThread{model.timeData.value = getCurrentTime()}
        } }, 1000, 1000)
    }

    private fun getAverageSpeed(distance: Float): String{
        return String.format("%.1f", 3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000.0f)))
    }

    private fun getCurrentTime(): String{
        return "Time: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

        private fun startStopService(){
        if(!isServiceRunning){
            startLocService()
        }else{
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fStartStop.setImageResource(R.drawable.ic_play)
            timer?.cancel()
        }
        isServiceRunning = !isServiceRunning
    }

    private fun checkServiceState(){
        isServiceRunning = LocationService.isRunning
        if(isServiceRunning){
            binding.fStartStop.setImageResource(R.drawable.ic_stop)
            startTimer()
        }
    }

    private fun  startLocService(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            activity?.startForegroundService((Intent(activity, LocationService::class.java)))
        }else{
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.fStartStop.setImageResource(R.drawable.ic_stop)
        LocationService.startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onResume(){
        super.onResume()
        checkLockPermission()
    }

    override fun onPause(){
        super.onPause()

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
        pl = Polyline()
        pl?.outlinePaint?.color = Color.BLUE
        map.controller.setZoom(16.0)
        //создание оверлеев наложений на карту и подключение к map
        val mLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(mLocProvider, map)
        myLocOverlay.enableMyLocation()
        myLocOverlay.enableFollowLocation()
        myLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(myLocOverlay)
            map.overlays.add(pl)
        }
    }

    private fun registerPermissions(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts
            .RequestMultiplePermissions()){
            if(it[ACCESS_FINE_LOCATION] == true){                // здесь не добавлено Manifest.permission.
                initOSM()                                          //  будет ли работать????
                checkLocationEnabled()

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
            checkLocationEnabled()
        } else {
            pLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION))
        }
    }

    // проверка разрешений для андроид версии ниже 10
    private fun checkPermissionBefore10() {
        if (checkPermission(ACCESS_FINE_LOCATION)) {
            initOSM()
            checkLocationEnabled()
        } else {
            pLauncher.launch(arrayOf(ACCESS_FINE_LOCATION))
        }
    }

    private fun checkLocationEnabled(){
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled){
            // вызов диалог менеджера для запуска вручную пользователем GPS
            DialogManager.showLocEnableDialog(activity as AppCompatActivity,
            //Интерфейс слушателя
                object : DialogManager.Listener{
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                }
            )
        }else{
            showToast("GPS enabled")
        }

    }
    // Прием интента
    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, i: Intent?) {
            if(i?.action == LocationService.LOC_MODEL_INTENT){
                val locModel = i.getSerializableExtra(LocationService.LOC_MODEL_INTENT) as LocationModel
                model.locationUpdates.value = locModel
            }
        }

    }

    private fun registerLocReceiver(){
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locFilter)
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiver)
    }
          // добасвление одной точки на полилайн
    private fun addaPoint(list: List<GeoPoint>){
        pl?.addPoint(list[list.size - 1])

    }
    // добасвление всего списка при переходе из сервиса в фрагмент
    private fun fillPolyline(list: List<GeoPoint>){
        list.forEach{pl?.addPoint(it)
        }
    }
    //    сама функция выбора одной из двух верхних функций добавления точки
    private fun updatePolyline(list: List<GeoPoint>){
        if(list.size > 1 && firstStart){
            fillPolyline(list)
            firstStart = false
        }else{
            addaPoint(list)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        }
    }
