package com.antarjala.akasavani.data

data class RadioStation(
    val name: String,
    val streamUrl: String,
    val needsHeaders: Boolean = false,
    val headers: Map<String, String> = emptyMap(),
    val isAccessible: Boolean = true
)

object RadioStations {
    val stations = listOf(
        RadioStation(
            name = "All India Radio Vishakapatnam",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio080/playlist.m3u8"
        ),
        RadioStation(
            name = "All India Radio Adilabad",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio218/playlist.m3u8"
        ),
        RadioStation(
            name = "Telugu NRI Radio",
            streamUrl = "http://104.167.4.67:8332/;stream.mp3"
        ),
        RadioStation(
            name = "TeluguOneRadio",
            streamUrl = "https://stream.teluguoneradio.com:8164/\\;steam/1"
        ),
        RadioStation(
            name = "RadioIndia",
            streamUrl = "https://radioindia.net/radio/humgama-telugu/icecast.audio",
            needsHeaders = true
        ),
        RadioStation(
            name = "MyindMedia",
            streamUrl = "https://radioindia.net/radio/humgama-telugu/icecast.audio",
            needsHeaders = true
        ),
        RadioStation(
            name = "AP9FM Guntur Radio",
            streamUrl = "https://stream.ap9fm.in/radio/8000/radio.mp3"
        ),
        RadioStation(
            name = "Radio Hungama",
            streamUrl = "https://stream.zeno.fm/ysucrq37uwzuv"
        )
    )
} 