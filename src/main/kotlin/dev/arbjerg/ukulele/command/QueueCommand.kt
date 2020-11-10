package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class QueueCommand(
        private val players: PlayerRegistry
) : Command("queue", "q", "list", "l") {

    private val pageSize = 10

    override suspend fun CommandContext.invoke() {
        reply(printQueue(players[guild], argumentText.toIntOrNull() ?: 1))
    }

    private fun CommandContext.printQueue(player: Player, pageIndex: Int): String {
        val totalDuration = players[guild].remainingDuration
        val tracks = player.tracks
        if (tracks.isEmpty())
            return "The queue is empty."

        return paginateQueue(tracks, pageIndex) +
                "\nThere are **${tracks.size}** tracks with a remaining length of " +
                "**${humanReadableTime(totalDuration)}** in the queue."
    }

    private fun humanReadableTime(length: Long): String = if (length < 3600000) {
        String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1))
    } else {
        String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(length),
                TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1))
    }

    private fun paginateQueue(tracks: List<AudioTrack>, index: Int) = buildString {
        val pageCount: Int = (tracks.size + pageSize - 1) / pageSize
        val pageIndex = index.coerceIn(1..pageCount)

        //Add header
        append("Page **$pageIndex** of **$pageCount**\n\n")

        val offset = pageSize * (pageIndex - 1)
        val pageEnd = (offset + pageSize).coerceAtMost(tracks.size)

        tracks.subList(offset, pageEnd).forEachIndexed { i, t ->
            appendLine("`[${offset + i + 1}]` **${t.info.title}** `[${humanReadableTime(t.duration)}]`")
        }
    }

    override fun HelpContext.provideHelp() {
        addUsage("[page]")
        addDescription("Displays the queue, by default for page 1")
    }
}
