package io.livekit.android.room.track

data class LocalTrackPublicationOptions(val placeholder: Unit)

enum class DataPublishReliability {
    RELIABLE,
    LOSSY,
}