package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class SkipCommand : Command("skip", "s") {
    override suspend fun CommandContext.invoke() {
        val args = argumentText.split("\\s+".toRegex())
        when {
            args.isEmpty() || args[0].isEmpty() -> skipNext()
            else -> skipIndex(args.last().toInt())
        }
    }

    private fun CommandContext.skipNext() {
        printSkipped(player.skip(0..0))
    }

    private fun CommandContext.skipIndex(i: Int) {
        val ind = (i - 1).coerceAtLeast(0)
        if (ind == 0) {
            player.seek(0)
            reply("Skipping to current track (restarting)")
        } else {
            val endRange = ind - 1
            printSkipped(player.skip(0..endRange))
        }
    }

    private fun CommandContext.printSkipped(skipped: List<AudioTrack>) {
        val playing = when (player.tracks.isEmpty()) {
            true -> "The queue is empty and the player is stopped."
            false -> "Playing `${player.tracks.first().info.title}`"
        }

        val skippedMessage = when (skipped.size) {
            0 -> getHelp(this.command).toString()
            1 -> """
            Skipped `${skipped.first().info.title}`
            
            `${playing}`
            """.trimIndent()
            else -> """
                Skipped `${skipped.size} tracks`
                
                `${playing}`
            """.trimIndent()
        }

        reply(skippedMessage)
    }

    override fun HelpContext.provideHelp() {
        addUsage("[index]")
        addDescription("Skips to queue position.")
        addDescription("Skips to next track if no number is given.")
    }
}