package com.antarjala.akasavani.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RadioStation(
    val name: String,
    val streamUrl: String,
    val needsHeaders: Boolean = false,
    val headers: Map<String, String> = emptyMap(),
    val isAccessible: Boolean = true
) : Parcelable

object RadioStations {
    val stations = listOf(
        // All India Radio (AIR) Stations
        RadioStation(
            name = "AIR Telugu",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio032/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Vijayawada",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio175/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Tirupati",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio144/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Kurnool",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio052/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Cuddapah",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio018/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Anantapur",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio054/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Nellore",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio168/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Warangal",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio154/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Hyderabad A",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio032/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Hyderabad B",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio033/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Nizamabad",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio222/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Markapur",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio039/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Kothagudem",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio116/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Simhapuri FM",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio168/playlist.m3u8"
        ),

        // AIR Rainbow Stations
        RadioStation(
            name = "AIR Rainbow Hyderabad",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio031/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Rainbow Visakhapatnam",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio081/playlist.m3u8"
        ),
        RadioStation(
            name = "AIR Rainbow Vijayawada",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio174/playlist.m3u8"
        ),

        // Vividh Bharati Stations
        RadioStation(
            name = "Vividh Bharati Hyderabad",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio034/playlist.m3u8"
        ),
        RadioStation(
            name = "Vividh Bharati Vijayawada",
            streamUrl = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio176/playlist.m3u8"
        ),

        // Private Radio Stations
        RadioStation(
            name = "Radio Mirchi 98.3 FM Hyderabad",
            streamUrl = "https://17653.live.streamtheworld.com/NJS_HIN_ESTAAC/HLS/playlist.m3u8"
        ),
        RadioStation(
            name = "Melody Radio Telugu",
            streamUrl = "https://a1.asurahosting.com:9580/radio.mp3"
        ),
        RadioStation(
            name = "London Radio Telugu",
            streamUrl = "https://c8.radioboss.fm/stream/33"
        ),
        RadioStation(
            name = "Radio Mirchi Bay Area Telugu",
            streamUrl = "https://17653.live.streamtheworld.com/MTL_TEL_ESTAAC/HLS/playlist.m3u8"
        ),

        // Existing stations
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