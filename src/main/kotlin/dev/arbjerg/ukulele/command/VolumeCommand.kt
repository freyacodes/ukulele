package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class VolumeCommand(val players: PlayerRegistry) : Command("volume", "v") {
    override suspend fun CommandContext.invoke() {
        val player = players[guild]
        if (argumentText.isBlank()) {
            reply("The volume is set to ${player.volume}")
            return
        }

        val num = argumentText.removeSuffix("%").toIntOrNull()
        if (num == null) {
            replyHelp()
            return
        }

        val formerVolume = player.volume
        player.volume = num
        reply("Changed volume from ${formerVolume}% to ${player.volume}%.")
    }
}