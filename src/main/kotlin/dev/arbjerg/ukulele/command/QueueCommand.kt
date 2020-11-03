package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class QueueCommand (
        private val players: PlayerRegistry
) : Command("q") {

    override suspend fun CommandContext.invoke() {
        reply(printQueue(players[guild].getQueue()))
    }

    private fun CommandContext.printQueue(queue: List<AudioTrack>): String {
        val sb = StringBuilder()
        when (queue.size) {
            0 -> sb.append("Empty queue")
            else -> {
                queue.forEach {
                    t -> sb.append("${t.info.title} \n")
                }
            }
        }
        return sb.toString()
    }
}