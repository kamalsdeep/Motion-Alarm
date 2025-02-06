package com.kd.motionalarm

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kd.motionalarm.motionalarmservice.MotionAlarmService

class MainActivity : ComponentActivity() {

    private lateinit var serviceIntent : Intent
    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenBackground()
        }

        getNotificationPermission()

        context = applicationContext
        serviceIntent = Intent(context, MotionAlarmService::class.java)

    }


    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    8
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Preview(showBackground = true)
    @Composable
    private fun HomeScreenBackground() {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.home_screen_background),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            StartStopButton()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun StartStopButton() {

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(23.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(
                    onClick = { startForegroundService(serviceIntent) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x80FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Start", fontSize = 23.sp)
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = { context.stopService(serviceIntent) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Stop", fontSize = 23.sp)
                }
            }
        }
    }
}