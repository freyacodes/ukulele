package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import dev.arbjerg.ukulele.jda.PrivateMessageContext


@Component
class StopCommand : Command("stop") {
    override suspend fun CommandContext.invoke() {
        val skipped = player.tracks.size

        player.stop()
        guild.audioManager.closeAudioConnection()

        reply("Player stopped. Removed **$skipped** tracks.")
    }

    override suspend fun PrivateMessageContext.invoke() {
        reply("this command in PM isn't supported yet")
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Clear all tracks from the queue and disconnect the player.")
    }
}