package com.antarjala.akasavani.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.antarjala.akasavani.data.RadioStation

class RadioPlayer(private val context: Context) {
    private var player: ExoPlayer? = null
    private var currentStation: RadioStation? = null
    private var _isPlaying: Boolean = false
    private var wasPlaying: Boolean = false

    private val headphoneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                when (intent.action) {
                    Intent.ACTION_HEADSET_PLUG -> {
                        val state = intent.getIntExtra("state", -1)
                        when (state) {
                            0 -> { // Headphone unplugged
                                if (_isPlaying) {
                                    wasPlaying = true
                                    pause()
                                }
                            }
                            1 -> { // Headphone plugged
                                if (wasPlaying) {
                                    wasPlaying = false
                                    play()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in headphone receiver", e)
            }
        }
    }

    init {
        try {
            initializePlayer()
            registerHeadphoneReceiver()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing RadioPlayer", e)
        }
    }

    private fun registerHeadphoneReceiver() {
        try {
            val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
            context.registerReceiver(headphoneReceiver, filter)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering headphone receiver", e)
        }
    }

    private fun initializePlayer() {
        try {
            player = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        try {
                            _isPlaying = state == Player.STATE_READY && player?.isPlaying == true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onPlaybackStateChanged", e)
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        try {
                            _isPlaying = playing
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onIsPlayingChanged", e)
                        }
                    }

                    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                        Log.e(TAG, "Player error: ${error.message}", error)
                        _isPlaying = false
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ExoPlayer", e)
            throw e
        }
    }

    fun togglePlayPause(station: RadioStation) {
        try {
            if (currentStation == station) {
                if (_isPlaying) {
                    pause()
                    context.stopService(Intent(context, RadioPlaybackService::class.java))
                } else {
                    play()
                    startPlaybackService(station)
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
            startPlaybackService(station)
        } catch (e: Exception) {
            Log.e(TAG, "Error in togglePlayPause", e)
            _isPlaying = false
        }
    }

    fun play() {
        try {
            player?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error in play", e)
            _isPlaying = false
        }
    }

    fun pause() {
        try {
            player?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in pause", e)
        }
    }

    private fun startPlaybackService(station: RadioStation) {
        try {
            val intent = Intent(context, RadioPlaybackService::class.java).apply {
                action = RadioPlaybackService.ACTION_PLAY
                putExtra(RadioPlaybackService.EXTRA_STATION, station)
            }
            context.startForegroundService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting playback service", e)
        }
    }

    fun release() {
        try {
            player?.release()
            player = null
            currentStation = null
            _isPlaying = false
            context.stopService(Intent(context, RadioPlaybackService::class.java))
            try {
                context.unregisterReceiver(headphoneReceiver)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering receiver", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in release", e)
        }
    }

    fun isPlaying(): Boolean = _isPlaying

    fun getCurrentStation(): RadioStation? = currentStation

    companion object {
        private const val TAG = "RadioPlayer"
    }
} 