package com.kd.motionalarm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var shouldShowText: Boolean = false
    private var isFirst5Seconds: Boolean = true

    // To store the initial values
    private var initialX: Float? = null
    private var initialY: Float? = null
    private var initialZ: Float? = null

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreenBackground()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        Log.d("SensorData","after sensor manager")

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
                        Toast.makeText(this,"Values Changed ....",Toast.LENGTH_LONG).show()
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

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Re-register sensor listener when activity is resumed
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        mediaPlayer?.stop()
    }

}



    @Preview(showBackground = true)
@Composable
fun HomeScreenBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.home_screen_background),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        StartStopButton()
    }
}

@Composable
fun StartStopButton() {

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(23.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x80FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start", fontSize = 23.sp)
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Stop", fontSize = 23.sp)
            }
        }
    }
}