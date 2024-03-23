package com.zeerak.testlivekit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.zeerak.riotlivekit.RiotLiveKitManager
import com.zeerak.testlivekit.databinding.ActivityMainBinding
import io.livekit.android.room.Room
import io.livekit.android.room.RoomListener
import io.livekit.android.room.participant.RemoteParticipant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), RiotLiveKitManager.RoomListener {

    lateinit var viewBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        val riotManager = RiotLiveKitManager(this).init(
            "wss://stephen-dzasntmt.livekit.cloud",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDY0NTQ2NzksImlzcyI6IkFQSUF6Rm52aG5GenZFZSIsIm5iZiI6MTcwNjM2NDY3OSwic3ViIjoiZGV2aWNlIiwidmlkZW8iOnsiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuUHVibGlzaERhdGEiOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwicm9vbSI6ImR1bW15Iiwicm9vbUpvaW4iOnRydWV9fQ.r5kBBqvY8bxloIWTaIi6uRb2vTLpuPfKfkxXL7x3aAM",
            )


        viewBinding.btnConnect.setOnClickListener {
            //riotManager.launchRiotLiveKitCallScreenWith()

            lifecycleScope.launch {
                riotManager.connect( this@MainActivity)
            }
        }

        viewBinding.btnDisconnect.setOnClickListener {
            riotManager.disconnect()
        }

    }

    override fun onError(exception: Exception) {
        viewBinding.tvStatus.text = "${exception.message}"
    }

    override fun onStateChanged(state: Room.State) {
        viewBinding.tvStatus.text = state.name
    }

    override fun onConnected(room: Room) {
        viewBinding.tvRoomInfo.text = "${room.name}"
        viewBinding.tvLocalParticipantInfo.text = "${room.localParticipant.toString()}"
        viewBinding.tvRemoteParticipantInfo.text = "${room.remoteParticipants}"
    }

    override fun onDisconnect(room: Room, error: Exception?) {
        viewBinding.tvRoomInfo.text = "N/A"
        viewBinding.tvLocalParticipantInfo.text = "N/A"
        viewBinding.tvRemoteParticipantInfo.text = "N/A"
    }

    override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
        Log.e("","")
    }

    override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
        Log.e("","")
    }
}

