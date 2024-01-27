package com.zeerak.riotlivekit

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.room.Room
import io.livekit.android.room.RoomListener
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import org.webrtc.EglBase.Context
import org.webrtc.audio.JavaAudioDeviceModule
import kotlin.jvm.Throws

class RiotLiveKitManager(private var mContext: android.content.Context) {

    private var url: String? = null
    private var token: String? = null
    private var mPreferencesHelper: PreferencesHelper = PreferencesHelper(mContext)
    private val mutableRoom = MutableLiveData<Room>()
    private val mutableRemoteParticipants = MutableLiveData<List<RemoteParticipant>>()

    val room: LiveData<Room> = mutableRoom
    val remoteParticipants: LiveData<List<RemoteParticipant>> = mutableRemoteParticipants

    fun init(url: String, token: String): RiotLiveKitManager {
        this.url = url
        this.token = token
        return this
    }

    fun setDelay(delay: Long) {
        mPreferencesHelper.saveAudioLatencyMS((delay))
        mPreferencesHelper.cacheAudioLatencyMS((delay))
        JavaAudioDeviceModule.delayDirty = delay != 0L
    }

    fun launchRiotLiveKitCallScreenWith() {
        checkForValidUrlAndToken()
        Constants.enableVideoBroadcast = false
        val intent = Intent(mContext, CallActivity::class.java).apply {
            putExtra(
                CallActivity.KEY_ARGS,
                CallActivity.BundleArgs(
                    url!!,
                    token!!
                )
            )
        }
        mContext.startActivity(intent)
    }

    @Throws
    fun checkForValidUrlAndToken(){
        when {
            url.isNullOrBlank() -> throw Exception("URL is null, please provide valid url in init Method")
            url.isNullOrBlank() -> throw Exception("Token is null, please provide valid token in init Method")
        }
    }

    suspend fun connect(error: (exception: Exception) -> Unit, listener: RoomListener) {
        checkForValidUrlAndToken()

        try {
            val room = LiveKit.connect(
                mContext,
                url!!,
                token!!,
                ConnectOptions(),
                Listener(object : CustomRoomListener {
                    override fun onDisconnect(room: Room, error: Exception?) {
                        mutableRoom.value?.disconnect()
                        listener.onDisconnect(room, error)
                    }

                    override fun onParticipantConnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                        updateParticipants(room)
                        listener.onParticipantConnected(room, participant)
                    }

                    override fun onParticipantDisconnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                        updateParticipants(room)
                        listener.onParticipantDisconnected(room, participant)
                    }

                })
            )

            val localParticipant = room.localParticipant
            val audioTrack = localParticipant.createAudioTrack()
            val videoTrack = localParticipant.createVideoTrack()


            //Broadcaster
            audioTrack.enabled = true
            videoTrack.enabled = false
            localParticipant.publishAudioTrack(audioTrack)

            updateParticipants(room)
            mutableRoom.value = room
        } catch (e: Exception) {
            error.invoke(e)
        }
    }

    fun updateParticipants(room: Room) {
        mutableRemoteParticipants.postValue(
            room.remoteParticipants
                .keys
                .sortedBy { it }
                .mapNotNull { room.remoteParticipants[it] }
        )
    }

    class Listener(private var customListener: CustomRoomListener) : RoomListener {
        override fun onDisconnect(room: Room, error: Exception?) {
            customListener.onDisconnect(room, error)
            Log.i("LIVEKIT", error?.message.toString())

        }

        override fun onParticipantConnected(
            room: Room,
            participant: RemoteParticipant
        ) {
            customListener.onParticipantConnected(room, participant)

        }

        override fun onParticipantDisconnected(
            room: Room,
            participant: RemoteParticipant
        ) {
            customListener.onParticipantDisconnected(room, participant)
        }

        override fun onFailedToConnect(room: Room, error: Exception) {
            Log.i("LIVEKIT", error?.message.toString())
        }

        override fun onActiveSpeakersChanged(speakers: List<Participant>, room: Room) {
            Log.i("LIVEKIT", "active speakers changed ${speakers.count()}")
        }

        override fun onMetadataChanged(
            participant: Participant,
            prevMetadata: String?,
            room: Room
        ) {
            Log.i("LIVEKIT", "Participant metadata changed: ${participant.identity}")
        }
    }
    /* */

    interface CustomRoomListener {
        fun onDisconnect(room: Room, error: Exception?)
        fun onParticipantConnected(
            room: Room,
            participant: RemoteParticipant
        )

        fun onParticipantDisconnected(
            room: Room,
            participant: RemoteParticipant
        )

    }
}