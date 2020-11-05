package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import dev.arbjerg.ukulele.audio.TrackQueue
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class QueueCommand (
        private val players: PlayerRegistry
) : Command("q") {

    private val pageSize = 5
    private val response = StringBuilder()
    
    override suspend fun CommandContext.invoke() {
        val queue = players[guild].getQueue()

        when {
            argumentText.isBlank() -> reply(printQueue(queue, 1))
            argumentText.toIntOrNull() != null -> reply(printQueue(queue, argumentText.toInt()))
        }

        response.clear()
    }

    private fun CommandContext.printQueue(queue: List<AudioTrack>, pageIndex: Int): String {
        val totalDuration = players[guild].getDuration()
        
        if (queue.isEmpty())
            return "Not currently playing anything."

        paginateQueue(queue, pageIndex)
        response.append("\nThere are **${queue.size}** tracks with a remaining length of **${humanReadableTime(totalDuration)}** in the queue.")

        return response.toString()
    }

    private fun CommandContext.humanReadableTime(length: Long): String {
        var hms: String

        if (length < 3600000) {
            hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(length),
            TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(length),
            TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1));
        }
         
        return hms
    }

    private fun CommandContext.paginateQueue(queue: List<AudioTrack>, index: Int) {
        val pageCount: Int = (queue.size + pageSize - 1) / pageSize
        val pageIndex: Int 

        response.append("Page **${pageIndex}** of **${pageCount}**\n\n")

        // Handle edge cases
        if (index == 0) 
            pageIndex = 1
        else if (index > pageCount) 
            pageIndex = pageCount
        else
            pageIndex = index


        // Preserve original indexes
        queue.forEachIndexed { ind, t ->
            if (t in queue.chunked(pageSize)[pageIndex - 1]) 
                response.append("`[${ind+1}]` **${t.info.title}** `[${humanReadableTime(t.duration)}]`\n")
        }
    }
}