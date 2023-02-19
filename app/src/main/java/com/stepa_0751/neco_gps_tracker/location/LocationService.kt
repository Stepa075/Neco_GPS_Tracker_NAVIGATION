package com.stepa_0751.neco_gps_tracker.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.stepa_0751.neco_gps_tracker.MainActivity
import com.stepa_0751.neco_gps_tracker.R
import org.osmdroid.util.GeoPoint

class LocationService : Service() {
    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequest: LocationRequest
    private var lastLocation: Location? = null
    private var distance = 0.0f
    private lateinit var geoPointsList: ArrayList<GeoPoint>


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        startLocationUpdates()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        locProvider.removeLocationUpdates(locCallback)
    }

    private val locCallback = object : LocationCallback() {

        override fun onLocationResult(lResult: LocationResult) {
            super.onLocationResult(lResult)
            val currentLocation = lResult.lastLocation
            //  Проверка на то что обе локации не null
            if (lastLocation != null && currentLocation != null) {
                // Проверка на то, что скорость не минимальная и нет погрешности
                // перемещений на маленькие расстояния
                Log.d("MyLog", "accuracy = ${currentLocation.accuracy}")
                Log.d("MyLog", "speed = ${(currentLocation.speed) *  3.6f}")
                if (currentLocation.speed > 0.5 && currentLocation.accuracy < 20.0 && currentLocation.speed < 40.0) {
                    distance += lastLocation?.distanceTo(
                        currentLocation
                    )!!
                    geoPointsList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                }
                val locModel = LocationModel(
                    currentLocation.speed,
                    distance,
                    geoPointsList
                )
                sendLocData(locModel)
            }
            lastLocation = currentLocation

            Log.d("MyLog", "Distance: $distance")
        }
    }
        // Отправка интента в фрагмент View
    private fun sendLocData(locModel: LocationModel){
        //  Для отправки через интент КЛАССА, он должен быть
        //  Serializable, т.е. разбираться на байты. Без
        //  этого можно отправлять только примитивные типы данных.
        val i = Intent(LOC_MODEL_INTENT)
        i.putExtra(LOC_MODEL_INTENT, locModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startNotification(){
        //  если версия андроида 8 или выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service", NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }
        //  если версия андроида ниже 8
        //  Отправка сообщения из статусной строки!!! И открытие главного активити
        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            //  контент
            this,
            //  номер нашего запроса для открытия активити
            10,
            nIntent,
            0
        )
        val notification = NotificationCompat.Builder(
            this, CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracker Running!")
            .setContentIntent(pIntent).build()
        // Перед запуском ОЬЯЗАТЕЛЬНО в пермиссионах выставить FOREGROUND_SERVICE!!!
        startForeground(99, notification)
    }
    //  Настройка клиента провайдера геолокации
    private fun initLocation(){
        val updateInterval = PreferenceManager.getDefaultSharedPreferences(
            this
        ).getString("update_time_key", "10000")?.toLong() ?: 3000
        locRequest = LocationRequest.create()
        locRequest.interval = updateInterval
        locRequest.fastestInterval = updateInterval
        locRequest.priority = PRIORITY_HIGH_ACCURACY
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)

    }

    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locProvider.requestLocationUpdates(
            locRequest,
            locCallback,
            Looper.myLooper()
        )
    }

    companion object{
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
        const val LOC_MODEL_INTENT = "loc_intent"
    }
}