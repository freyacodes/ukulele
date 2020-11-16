package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import dev.arbjerg.ukulele.utils.TextUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class NowPlayingCommand : Command ("nowplaying", "np") {

    override suspend fun CommandContext.invoke() {
        if (player.tracks.isEmpty())
            return reply("Not playing anything.")

        replyEmbed(buildEmbed(player.tracks[0]))
    }

    private fun buildEmbed(track: AudioTrack): MessageEmbed {
        return when(track.sourceManager.sourceName){
            "youtube" -> getYoutubeEmbed(track)
            "soundcloud" -> getSoundCloudEmbed(track)
            else -> return getDefaultEmbed()
        }
    }

    private fun getYoutubeEmbed(track: AudioTrack): MessageEmbed {
        val message = EmbedBuilder()
        val timeField = "[${TextUtils.humanReadableTime(track.position)} / ${TextUtils.humanReadableTime(track.info.length)}]"

        message.setColor(YOUTUBE_RED)

        message.setTitle(track.info.title, track.info.uri)
        message.addField(
                "Time",
                timeField,
                true
        )

        return message.build()
    }

    private fun getSoundCloudEmbed(track: AudioTrack): MessageEmbed {
        val message = EmbedBuilder()
        message.setTitle("Not yet implemented.")
        return message.build()
    }

    private fun getDefaultEmbed(): MessageEmbed {
        val message = EmbedBuilder()
        message.setTitle("Not yet implemented.")
        return message.build()
    }


    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays general help. (unfinished)")
    }

    companion object {
        private val YOUTUBE_RED = Color(205, 32, 31).rgb
        private const val YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v="
    }
}