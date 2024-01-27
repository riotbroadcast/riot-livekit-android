package com.zeerak.testlivekit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.zeerak.riotlivekit.RiotLiveKitManager
import io.livekit.android.room.RoomListener
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val riotManager = RiotLiveKitManager(this).init(
            "wss://stephen-dzasntmt.livekit.cloud",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDY0NTQ2NzksImlzcyI6IkFQSUF6Rm52aG5GenZFZSIsIm5iZiI6MTcwNjM2NDY3OSwic3ViIjoiZGV2aWNlIiwidmlkZW8iOnsiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuUHVibGlzaERhdGEiOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwicm9vbSI6ImR1bW15Iiwicm9vbUpvaW4iOnRydWV9fQ.r5kBBqvY8bxloIWTaIi6uRb2vTLpuPfKfkxXL7x3aAM",
        )


        findViewById<Button>(R.id.button).setOnClickListener {
            //riotManager.launchRiotLiveKitCallScreenWith()

            lifecycleScope.launch {
                riotManager.connect({}, object : RoomListener {})
            }
        }
    }
}