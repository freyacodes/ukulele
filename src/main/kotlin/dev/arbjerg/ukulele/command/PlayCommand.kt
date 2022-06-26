package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class PlayCommand(
        val players: PlayerRegistry,
        val apm: AudioPlayerManager,
        val botProps: BotProps
) : Command("play", "p") {
    override suspend fun CommandContext.invoke() {
        if (!ensureVoiceChannel()) return

        var identifier = argumentText
        if (!checkValidUrl(identifier)) {
            identifier = "ytsearch:$identifier"
        }

        players.get(guild, guildProperties).lastChannel = channel
        apm.loadItem(identifier, Loader(this, player, identifier))
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
        return url.startsWith("http://")
                || url.startsWith("https://")
    }

    inner class Loader(
            private val ctx: CommandContext,
            private val player: Player,
            private val identifier: String
    ) : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            if (track.isOverDurationLimit) {
                ctx.reply("Refusing to play `${track.info.title}` because it is over ${botProps.trackDurationLimit} minutes long")
                return
            }
            val started = player.add(track)
            if (started) {
                ctx.reply("Started playing `${track.info.title}`")
            } else {
                ctx.reply("Added `${track.info.title}`")
            }
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            val accepted = playlist.tracks.filter { !it.isOverDurationLimit }
            val filteredCount = playlist.tracks.size - accepted.size
            if (accepted.isEmpty()) {
                ctx.reply("Refusing to play $filteredCount tracks because because they are all over ${botProps.trackDurationLimit} minutes long")
                return
            }

            if (identifier.startsWith("ytsearch") || identifier.startsWith("ytmsearch") || identifier.startsWith("scsearch:")) {
                this.trackLoaded(accepted.component1());
                return
            }

            player.add(*accepted.toTypedArray())
            ctx.reply(buildString {
                append("Added `${accepted.size}` tracks from `${playlist.name}`.")
                if (filteredCount != 0) append(" `$filteredCount` tracks have been ignored because they are over ${botProps.trackDurationLimit} minutes long")
            })
        }

        override fun noMatches() {
            ctx.reply("Nothing found for “$identifier”")
        }

        override fun loadFailed(exception: FriendlyException) {
            ctx.handleException(exception)
        }

        private val AudioTrack.isOverDurationLimit: Boolean
            get() = botProps.trackDurationLimit > 0 && botProps.trackDurationLimit <= (duration / 60000)
    }

    override fun HelpContext.provideHelp() {
        addUsage("<url>")
        addDescription("Add the given track to the queue")
    }
}
