package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class TrackQueue {

    private val queue = mutableListOf<AudioTrack>()

    fun add(vararg tracks: AudioTrack) {
        queue.addAll(tracks)
    }

    fun take() = queue.removeFirstOrNull()
    fun peek() = queue.firstOrNull()
    fun removeRange(range: IntRange): List<AudioTrack> {
        val list = queue.subList(range.first, range.last)
        queue.removeAll(list)
        return list
    }
    fun getQueue(): List<AudioTrack> {
        return queue
    }
    fun getDuration(): Long {
        var totalLength: Long = 0
        queue.forEach{ t ->
            totalLength += t.info.length
        }
        return totalLength
    }
} 