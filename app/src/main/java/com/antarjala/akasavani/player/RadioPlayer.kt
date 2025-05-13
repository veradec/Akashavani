package com.antarjala.akasavani.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.antarjala.akasavani.data.RadioStation

class RadioPlayer(private val context: Context) {
    private var player: ExoPlayer? = null
    private var currentStation: RadioStation? = null
    private var _isPlaying: Boolean = false

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    _isPlaying = state == Player.STATE_READY && player?.isPlaying == true
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying = playing
                }
            })
        }
    }

    fun togglePlayPause(station: RadioStation) {
        if (currentStation == station) {
            if (_isPlaying) {
                player?.pause()
            } else {
                player?.play()
            }
            return
        }

        // If it's a different station, stop current and start new
        player?.stop()
        currentStation = station
        _isPlaying = true

        val mediaItem = MediaItem.fromUri(station.streamUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    fun release() {
        player?.release()
        player = null
        currentStation = null
        _isPlaying = false
    }

    fun isPlaying(): Boolean = _isPlaying

    fun getCurrentStation(): RadioStation? = currentStation
} 