package dev.arbjerg.ukulele.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class SkipCommand : Command("skip", "s") {
    override suspend fun CommandContext.invoke() {
        when {
            argumentText.isBlank() -> skipNext()
            argumentText.toIntOrNull() != null -> skipIndex(argumentText.toInt())
            argumentText.split("\\s+".toRegex()).size == 2 -> skipRange()
        }
    }

    private fun CommandContext.skipNext() {
        printSkipped(player.skip(0..0))
    }

    private fun CommandContext.skipIndex(i: Int) {
        val ind = (i-1).coerceAtLeast(0)
        printSkipped(player.skip(ind..ind))
    }

    private fun CommandContext.skipRange() {
        val args = argumentText.split("\\s+".toRegex())

        val n1 = (args[0].toInt() - 1).coerceAtLeast(0)
        val n2 = (args[1].toInt() - 1).coerceAtLeast(0)
        printSkipped(player.skip(n1..n2))
    }

    private fun CommandContext.printSkipped(skipped: List<AudioTrack>) = when(skipped.size) {
        0 -> replyHelp()
        1 -> reply("Skipped `${skipped.first().info.title}`")
        else -> reply("Skipped `${skipped.size} tracks`")
    }

    override fun HelpContext.provideHelp() {
        addUsage("[count]")
        addDescription("Skips a number of tracks.")
        addDescription("Defaults to the first track if no number is given.")
        addUsage("<from> <to>")
        addDescription("Skips a range of tracks.")
    }
}