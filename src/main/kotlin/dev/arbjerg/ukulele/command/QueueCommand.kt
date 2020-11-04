package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
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
        if (queue.isEmpty())
            return "Empty Queue"

        queue.forEachIndexed { index, t -> 
            sb.append("`[${index+1}]` ${t.info.title} \n")
        }

        return sb.toString()
    }
}