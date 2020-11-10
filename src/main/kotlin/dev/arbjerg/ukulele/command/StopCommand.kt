package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class StopCommand(val players: PlayerRegistry) : Command("stop") {
    override suspend fun CommandContext.invoke() {
        val player = players[guild]
        val skipped = player.tracks.size

        player.stop()
        guild.audioManager.closeAudioConnection()

        reply("Player stopped. Removed **$skipped** tracks.")
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Kill the thing")
    }
}