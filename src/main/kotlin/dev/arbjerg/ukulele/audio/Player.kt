package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.Buffer
import java.nio.ByteBuffer


class Player(apm: AudioPlayerManager) : AudioEventAdapter(), AudioSendHandler {
    private val queue = TrackQueue()
    private val player = apm.createPlayer().apply { addListener(this@Player) }
    private val buffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame().apply { setBuffer(buffer) }
    private val log: Logger = LoggerFactory.getLogger(Player::class.java)

    /**
     * @return whether or not we started playing
     */
    fun add(vararg tracks: AudioTrack): Boolean {
        queue.add(*tracks)
        if (player.playingTrack == null) {
            player.playTrack(queue.take()!!)
            return true
        }
        return false
    }

    fun getQueue(): List<AudioTrack> {
        val tracks = ArrayList<AudioTrack>()

        if (player.playingTrack != null)
            tracks.add(player.playingTrack)

        queue.getQueue().forEach {
            t -> tracks.add(t)
        }
        return tracks
    }

    fun getDuration(): Long {
        var duration: Long = 0

        if (player.playingTrack != null)
            duration += player.playingTrack.info.length
        
        duration += queue.getDuration()
        return duration
    }

    fun skip(range: IntRange): List<AudioTrack> {
        val skipped = mutableListOf<AudioTrack>()
        var newRange = range
        // Skip the first track if it is stored here
        if (range.contains(0) && player.playingTrack != null) {
            skipped.add(player.playingTrack)
            // Reduce range if found
            newRange = 0 until range.last
        }
        if (newRange.last >= 0) skipped.addAll(queue.removeRange(newRange))
        if (skipped.first() == player.playingTrack) player.stopTrack()
        return skipped
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        val new = queue.take() ?: return
        player.playTrack(new)
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        log.error("Track exception", exception)
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        log.error("Track $track got stuck!")
    }

    override fun canProvide(): Boolean {
        return player.provide(frame)
    }

    override fun provide20MsAudio(): ByteBuffer {
        // flip to make it a read buffer
        (buffer as Buffer).flip()
        return buffer
    }

    override fun isOpus() = true
}
