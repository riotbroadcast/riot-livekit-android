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
            "wss://demo-htzvtmdl.livekit.cloud",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTE5NTQ0MjIsImlzcyI6IkFQSWVkUWY3aUM3dHI0ayIsIm5iZiI6MTcxMTEzNDQyMiwic3ViIjoibGlzdGVuZXIiLCJ2aWRlbyI6eyJjYW5QdWJsaXNoIjp0cnVlLCJjYW5QdWJsaXNoRGF0YSI6dHJ1ZSwiY2FuU3Vic2NyaWJlIjp0cnVlLCJyb29tIjoiYmFzZW1lbnQiLCJyb29tSm9pbiI6dHJ1ZX19.-FBOlT0SV3XK2VdUFrREGnP6cC-ONvgIJE2XMfu1FXY",
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
        viewBinding.tvLocalParticipantInfo.text = "${room.localParticipant.identity}"
        viewBinding.tvRemoteParticipantInfo.text = "${room.remoteParticipants.values.first().identity}"

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

