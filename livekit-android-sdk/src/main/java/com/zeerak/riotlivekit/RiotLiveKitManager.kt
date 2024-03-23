package com.zeerak.riotlivekit

import android.content.Intent
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.room.Room
import io.livekit.android.room.participant.RemoteParticipant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livekit.LivekitModels
import org.webrtc.audio.JavaAudioDeviceModule
import kotlin.jvm.Throws

class RiotLiveKitManager(private var mContext: android.content.Context) {

    private var url: String? = null
    private var token: String? = null
    private var mPreferencesHelper: PreferencesHelper = PreferencesHelper(mContext)
    private var mRoom : Room? = null

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
    fun checkForValidUrlAndToken() {
        when {
            url.isNullOrBlank() -> throw Exception("URL is null, please provide valid url in init method")
            url.isNullOrBlank() -> throw Exception("Token is null, please provide valid token in init method")
        }
    }

    fun disconnect() {
        if (mRoom?.state == Room.State.CONNECTED)
            mRoom?.disconnect()
    }

    fun getCurrentState(): Room.State? {
        return mRoom?.state
    }

    fun getLocalParticipantInfo(): LivekitModels.ParticipantInfo? {
        return mRoom?.localParticipant?.participantInfo
    }

    fun getRemoteParticipants(): Map<String, RemoteParticipant>? {
        return mRoom?.remoteParticipants
    }


    suspend fun connect(listener: RoomListener) {
        try {
            checkForValidUrlAndToken()
            val room = LiveKit.connect(
                mContext,
                url!!,
                token!!,
                ConnectOptions(),
                listener = object : io.livekit.android.room.RoomListener {
                    override fun onStateChanged(state: Room.State) {
                        CoroutineScope(Dispatchers.Main).launch {
                            listener.onStateChanged(state)
                        }
                    }

                    override fun onConnected(room: Room) {
                        CoroutineScope(Dispatchers.Main).launch {
                            listener.onConnected(room)
                        }
                    }

                    override fun onDisconnect(room: Room, error: Exception?) {
                        CoroutineScope(Dispatchers.Main).launch {
                            listener.onDisconnect(room, error)
                        }
                    }

                    override fun onParticipantConnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            listener.onParticipantConnected(room, participant)
                        }
                    }

                    override fun onParticipantDisconnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            listener.onParticipantDisconnected(room, participant)
                        }
                    }
                })

            val localParticipant = room.localParticipant
            val audioTrack = localParticipant.createAudioTrack()
            val videoTrack = localParticipant.createVideoTrack()


            //Broadcaster
            audioTrack.enabled = true
            videoTrack.enabled = false
            localParticipant.publishAudioTrack(audioTrack)
            mRoom = room
        } catch (e: Exception) {
            listener.onError(e)
        }
    }


    interface RoomListener {

        fun onError(exception : Exception)
        fun onStateChanged(state: Room.State)
        fun onConnected(room: Room)
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