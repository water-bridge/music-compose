package com.example.musiccompose.data.network

import android.support.v4.media.MediaMetadataCompat
import com.example.musiccompose.data.network.State.*

// code from UAMP
// I have made some modifications

interface MusicSource : Iterable<MediaMetadataCompat> {

    suspend fun load()

    // method performs action after the data is ready
    fun whenReady(action: (Boolean) -> Unit): Boolean

}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}

abstract class AbstractMusicSource : MusicSource {

    // the state of the source
    var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    // !! not thread safe
    override fun whenReady(action: (Boolean) -> Unit): Boolean =
        when (state) {
            STATE_CREATED, STATE_INITIALIZING -> {
                onReadyListeners += action
                false
            }
            else -> {
                action(state != STATE_ERROR)
                true
            }
        }

}