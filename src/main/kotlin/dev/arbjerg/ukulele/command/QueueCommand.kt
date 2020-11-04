package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class QueueCommand (
        private val players: PlayerRegistry
) : Command("q") {

    override suspend fun CommandContext.invoke() {
        reply(printQueue(players[guild].getQueue()))
    }

    private fun CommandContext.printQueue(queue: List<AudioTrack>): String {
        val sb = StringBuilder()
        var totalDuration: Long = 0
        if (queue.isEmpty())
            return "Not currently playing anything."

        queue.forEachIndexed { index, t -> 
            sb.append("`[${index+1}]` **${t.info.title}** `[${humanReadableTime(t.duration)}]`\n")
            totalDuration += t.info.length
        }

        sb.append("\n There are **${queue.size}** tracks with a remaining length of **${humanReadableTime(totalDuration)}** in the queue.")

        return sb.toString()
    }

    private fun CommandContext.humanReadableTime(length: Long): String {
        var hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(length),
        TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1));

        return hms
    }
}