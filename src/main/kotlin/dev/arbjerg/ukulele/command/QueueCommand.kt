package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import dev.arbjerg.ukulele.utils.TextUtils
import org.springframework.stereotype.Component

@Component
class QueueCommand(
        private val players: PlayerRegistry
) : Command("queue", "q", "list", "l") {

    private val pageSize = 10

    override suspend fun CommandContext.invoke() {
        reply(printQueue(player, argumentText.toIntOrNull() ?: 1))
    }

    private fun printQueue(player: Player, pageIndex: Int): String {
        val totalDuration = player.remainingDuration
        val tracks = player.tracks
        if (tracks.isEmpty())
            return "The queue is empty."

        return buildString {
            append(paginateQueue(tracks, pageIndex))
            append("\nThere are **${tracks.size}** tracks with a remaining length of ")

            if (tracks.any{ it.info.isStream }) {
                append("**${TextUtils.humanReadableTime(totalDuration)}** in the queue excluding streams.")
            } else {
                append("**${TextUtils.humanReadableTime(totalDuration)}** in the queue.")
            }
        }
    }

    private fun paginateQueue(tracks: List<AudioTrack>, index: Int) = buildString {
        val pageCount: Int = (tracks.size + pageSize - 1) / pageSize
        val pageIndex = index.coerceIn(1..pageCount)

        //Add header
        append("Page **$pageIndex** of **$pageCount**\n\n")

        val offset = pageSize * (pageIndex - 1)
        val pageEnd = (offset + pageSize).coerceAtMost(tracks.size)

        tracks.subList(offset, pageEnd).forEachIndexed { i, t ->
            appendLine("`[${offset + i + 1}]` **${t.info.title}** `[${if (t.info.isStream) "Live" else TextUtils.humanReadableTime(t.duration)}]`")
        }
    }

    override fun HelpContext.provideHelp() {
        addUsage("[page]")
        addDescription("Displays the queue, by default for page 1")
    }
}
