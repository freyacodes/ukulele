package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.Permission
import java.net.URL
import java.lang.Exception
import org.springframework.stereotype.Component

@Component
class PlayCommand(
        val players: PlayerRegistry,
        val apm: AudioPlayerManager
) : Command("play", "p") {
    override suspend fun CommandContext.invoke() {
        if (!ensureVoiceChannel()) return
        var identifier = argumentText
        if (checkValidUrl(identifier)) {
            apm.loadItem(identifier, Loader(this, player, identifier))
        }
        else {
            identifier = "ytsearch:$identifier"
            apm.loadItem(identifier, Loader(this, player, identifier))
        }
    }

    fun CommandContext.ensureVoiceChannel(): Boolean {
        val ourVc = guild.selfMember.voiceState?.channel
        val theirVc = invoker.voiceState?.channel

        if (ourVc == null && theirVc == null) {
            reply("You need to be in a voice channel")
            return false
        }

        if (ourVc != theirVc && theirVc != null)  {
            val canTalk = selfMember.hasPermission(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)
            if (!canTalk) {
                reply("I need permission to connect and speak in ${theirVc.name}")
                return false
            }

            guild.audioManager.openAudioConnection(theirVc)
            guild.audioManager.sendingHandler = player
            return true
        }

        return ourVc != null
    }

    fun checkValidUrl(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        }
        catch (e: Exception) {
            false
        }
    }

    class Loader(
            private val ctx: CommandContext,
            private val player: Player,
            private val identifier: String
    ) : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            val started = player.add(track)
            if (started) {
                ctx.reply("Started playing `${track.info.title}`")
            } else {
                ctx.reply("Added `${track.info.title}`")
            }
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            if (identifier.startsWith("ytsearch:")) {
                val track = playlist.tracks[0]  // Pick first search result from Lavaplayer ytsearch playlist
                player.add(track)
                ctx.reply("Added `${track.info.title}`")
            }
            else {
                player.add(*playlist.tracks.toTypedArray())
                ctx.reply("Added `${playlist.tracks.size}` tracks from `${playlist.name}`")
            }
        }

        override fun noMatches() {
            ctx.reply("Nothing found for “$identifier”")
        }

        override fun loadFailed(exception: FriendlyException) {
            ctx.handleException(exception)
        }
    }

    override fun HelpContext.provideHelp() {
        addUsage("<url>")
        addDescription("Add the given track to the queue")
    }
}
