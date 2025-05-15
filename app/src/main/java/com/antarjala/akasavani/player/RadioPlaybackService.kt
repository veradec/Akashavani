package com.antarjala.akasavani.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.antarjala.akasavani.MainActivity
import com.antarjala.akasavani.R
import com.antarjala.akasavani.data.RadioStation

class RadioPlaybackService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var radioPlayer: RadioPlayer? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "RadioPlaybackChannel"
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentStation: RadioStation? = null

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()
            radioPlayer = RadioPlayer(this)
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // Initialize wake lock
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Akasavani:RadioPlaybackWakeLock"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // Start foreground immediately with a basic notification
            startForeground(NOTIFICATION_ID, createNotification(null))
            
            when (intent?.action) {
                ACTION_PLAY -> {
                    val station = intent.getParcelableExtra<RadioStation>(EXTRA_STATION)
                    station?.let { 
                        currentStation = it
                        playRadio(it)
                    }
                }
                ACTION_STOP -> stopRadio()
                ACTION_PAUSE -> pauseRadio()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand", e)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun playRadio(station: RadioStation) {
        try {
            if (requestAudioFocus()) {
                radioPlayer?.togglePlayPause(station)
                // Update notification after playback starts
                updateNotification(station)
                wakeLock?.acquire(10*60*1000L /*10 minutes*/)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in playRadio", e)
            stopSelf()
        }
    }

    private fun pauseRadio() {
        try {
            radioPlayer?.pause()
            updateNotification(currentStation)
            wakeLock?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error in pauseRadio", e)
        }
    }

    private fun stopRadio() {
        try {
            radioPlayer?.release()
            stopForeground(true)
            stopSelf()
            wakeLock?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error in stopRadio", e)
        }
    }

    private fun requestAudioFocus(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build()

                val result = audioManager.requestAudioFocus(audioFocusRequest!!)
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val result = audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting audio focus", e)
            false
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        try {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    pauseRadio()
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    radioPlayer?.play()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onAudioFocusChange", e)
        }
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Radio Playback",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Used for radio playback in background"
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification channel", e)
        }
    }

    private fun createNotification(station: RadioStation?): Notification {
        return try {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val playPauseIntent = Intent(this, RadioPlaybackService::class.java).apply {
                action = if (radioPlayer?.isPlaying() == true) ACTION_PAUSE else ACTION_PLAY
                putExtra(EXTRA_STATION, station)
            }
            val playPausePendingIntent = PendingIntent.getService(
                this,
                0,
                playPauseIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val stopIntent = Intent(this, RadioPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            val stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(station?.name ?: "Radio")
                .setContentText(if (radioPlayer?.isPlaying() == true) "Playing" else "Paused")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(
                    if (radioPlayer?.isPlaying() == true) R.drawable.ic_pause else R.drawable.ic_play,
                    if (radioPlayer?.isPlaying() == true) "Pause" else "Play",
                    playPausePendingIntent
                )
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification", e)
            // Return a basic notification as fallback
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Radio")
                .setContentText("Error creating notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        }
    }

    private fun updateNotification(station: RadioStation?) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification(station))
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        try {
            radioPlayer?.release()
            radioPlayer = null
            wakeLock?.release()
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY = "com.antarjala.akasavani.ACTION_PLAY"
        const val ACTION_PAUSE = "com.antarjala.akasavani.ACTION_PAUSE"
        const val ACTION_STOP = "com.antarjala.akasavani.ACTION_STOP"
        const val EXTRA_STATION = "extra_station"
        private const val TAG = "RadioPlaybackService"
    }
} 