## Installation

LiveKit for Android is available as a Maven package.

```groovy title="build.gradle"

dependencies {
  implementation 'com.github.riotbroadcast:riot-livekit-android:1.0.0'
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

## Usage


Riot LiveKit uses WebRTC-provided `org.webrtc.SurfaceViewRenderer` to render video tracks. Subscribed audio tracks are automatically played.
### Launch Default Call Screen

```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RiotLiveKitManager(this)
                .launchRiotLiveKitCallScreenWith(
                    "wss://yourserver.livekit.cloud",
                    "token"
                )
}
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.launchRiotLiveKitCallScreenWith()
    }
```

### OR By Using Custom Connect Method
```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.connect({}, object : RoomListener {})
    }
```

### set Delay
```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.launchRiotLiveKitCallScreenWith()
        riotManager.setDelay(500L) 
    }
```

### Optional (Dev convenience)

1. Use method launchRiotLiveKitCallScreenWith to launch call screen.
2. Custom call screen can also be created using methods inside RiotLiveKitManager class. 
3. setDelay() method uses value in milliseconds the upper limit is 70000L default is 0.
