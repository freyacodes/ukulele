package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class TrackQueue {

    private val queue = mutableListOf<AudioTrack>()

    fun add(vararg tracks: AudioTrack) {
        queue.addAll(tracks)
    }

    fun take() = queue.removeFirstOrNull()

}