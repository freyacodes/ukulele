package dev.arbjerg.ukulele.command

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.SpotifyUriException
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
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Component
import java.net.URL

@Component
class PlayCommand(
        val players: PlayerRegistry,
        val apm: AudioPlayerManager,
        val botProps: BotProps,
        val spotify: SpotifyAppApi?,
        val urlValidator: UrlValidator
) : Command("play", "p") {
    private companion object {
        @JvmStatic val SEARCH_PREFIX_REGEX: Regex by lazy {
            """^.+search:""".toRegex()
        }
    }

    override suspend fun CommandContext.invoke() {
        if (!ensureVoiceChannel()) return
        val identifier = argumentText
        players.get(guild, guildProperties).lastChannel = channel

        if (spotify != null) {
            try {
                val spotifyUri = spotifyUrlToUri(identifier) ?: identifier;
                val searches = convertSpotifyUri(spotifyUri)
                if (searches != null && searches.isNotEmpty()) {
                    val logAddition = searches.size == 1;
                    searches.forEach {
                        apm.loadItemOrdered(identifier, it, Loader(this, player, it, logAddition, true))
                    }
                    reply("Added ${searches.size} tracks from ${getNameFromSpotifyUri(spotifyUri)}")
                    return
                }
            } catch (_: SpotifyUriException) {
                // Identifier isn't a spotify URI
            }
        }
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

    private suspend fun convertSpotifyUri(identifier: String): List<String>? {
        if (!identifier.startsWith("spotify:")) {
            return null;
        }

        val split = identifier.split(":");
        if (split.size != 3) {
            return null;
        }

        when (split[1]) {
            "playlist" -> {
                val spotifyUri = spotify?.playlists?.getPlaylist(identifier);
                if (spotifyUri != null) {
                    return spotifyUri.tracks.getAllItemsNotNull()
                        .filter { playlistTrack -> playlistTrack.isLocal == false }
                        .filter { playlistTrack -> playlistTrack.track?.asTrack?.name != null }
                        .map { playlistTrack -> "ytmsearch:" + playlistTrack.track?.asTrack?.name + " " + (playlistTrack.track?.asTrack?.artists?.joinToString(" ") { it.name } ?: "") }
                }
            }
            else -> {return null}
        }
        return null
    }

    private suspend fun getNameFromSpotifyUri(identifier: String): String? {
        if (!identifier.startsWith("spotify:")) {
            return null;
        }

        val split = identifier.split(":");
        if (split.size != 3) {
            return null;
        }

        when (split[1]) {
            "playlist" -> {
                val spotifyUri = spotify?.playlists?.getPlaylist(identifier);
                if (spotifyUri != null) {
                    return spotifyUri.name
                }
            }
            else -> {return null}
        }
        return null
    }

    private fun spotifyUrlToUri(identifier: String): String? {
        if (!urlValidator.isValid(identifier)) {
            return null
        }

        val url = URL(identifier)
        if (!url.host.endsWith("spotify.com")) {
            return null
        }

        val split = url.path.split("/").filter { it.isNotBlank() }
        if (split.size != 2) {
            return null
        }

        return "spotify:" + split[0] + ":" + split[1]
    }

    inner class Loader(
            private val ctx: CommandContext,
            private val player: Player,
            private val identifier: String,
            private val logAddition: Boolean = true,
            private val isSearch: Boolean = false
    ) : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            if (track.isOverDurationLimit) {
                ctx.reply("Refusing to play `${track.info.title}` because it is over ${botProps.trackDurationLimit} minutes long")
                return
            }
            val started = player.add(track)
            if (logAddition) {
                if (started) {
                    ctx.reply("Started playing `${track.info.title}`")
                } else {
                    ctx.reply("Added `${track.info.title}`")
                }
            }
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            val accepted = playlist.tracks.filter { !it.isOverDurationLimit }
            val filteredCount = playlist.tracks.size - accepted.size
            if (accepted.isEmpty()) {
                ctx.reply("Refusing to play $filteredCount tracks because because they are all over ${botProps.trackDurationLimit} minutes long")
                return
            }

            if (isSearch || SEARCH_PREFIX_REGEX.matches(identifier)) {
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
            ctx.reply(
                "Nothing found for “${if (isSearch) {
                    SEARCH_PREFIX_REGEX.replaceFirst(identifier, "")} else {identifier}}”"
            )
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
