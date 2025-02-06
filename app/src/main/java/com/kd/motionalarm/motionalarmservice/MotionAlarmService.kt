package com.kd.motionalarm.motionalarmservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.kd.motionalarm.R

class MotionAlarmService : Service(), SensorEventListener {

    private final val TAG = "MotionAlarmService"

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var shouldShowText: Boolean = false
    private var isFirst5Seconds: Boolean = true

    // To store the initial values
    private var initialX: Float? = null
    private var initialY: Float? = null
    private var initialZ: Float? = null

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG,"OnCreate MotionAlarmService")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Toast.makeText(this,"Starting Service...", Toast.LENGTH_LONG).show()
        // Create the notification channel if necessary (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "motion_alarm_channel",
                "Motion Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Start foreground service
        val notification: Notification = NotificationCompat.Builder(this, "motion_alarm_channel")
            .setContentTitle("Motion Alarm Running")
            .setContentText("Monitoring phone movement.")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        startForeground(1, notification)
        Log.d("AfterStart","Log after startForeground()")


        // Register the listener for the accelerometer
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        Log.d("SensorData","after sensor manager Listener")

        // Start a 5-second timer to track the first 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            isFirst5Seconds = false
            Log.d("SensorData", "5 seconds passed, now monitoring changes.")
        }, 5000)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("onDestroy","Service onDestroy")
        accelerometer?.let {
            sensorManager.unregisterListener(this)
        }
        super.onDestroy()
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        Log.d("SensorData","Sensor changed called")

        if (p0 != null) {
            if (p0.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                var x = p0?.values?.get(0)
                var y = p0?.values?.get(1)
                var z = p0?.values?.get(2)

                if(isFirst5Seconds){
                    initialX = x
                    initialY = y
                    initialZ = z
                }

                // After 5 seconds, check for changes
                if (!isFirst5Seconds) {
                    // Get the integer parts of the x, y, and z values
                    val xInt = initialX?.toInt()
                    val yInt = initialY?.toInt()
                    val zInt = initialZ?.toInt()

                    // Check if the integer part has changed for any axis
                    if (x?.toInt() != xInt || y?.toInt() != yInt || z?.toInt() != zInt) {
                        shouldShowText = true
                        Toast.makeText(this,"Values Changed", Toast.LENGTH_SHORT).show()
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(this, R.raw.emergency_alert)
                            mediaPlayer?.isLooping = true  // Set the sound to loop continuously
                            mediaPlayer?.start()  // Start the sound
                        }
                        Log.d("SensorData", "Accelerometer X: $x, Y: $y, Z: $z")
                    } else {
                        shouldShowText = false
                    }
                }
                Log.d("SensorData", "Accelerometer X: $x, Y: $y, Z: $z")
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}