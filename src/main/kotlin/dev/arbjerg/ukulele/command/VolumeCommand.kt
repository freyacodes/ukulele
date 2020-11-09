package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class VolumeCommand(val players: PlayerRegistry) : Command("volume", "v") {
    override suspend fun CommandContext.invoke() {
        val num = argumentText.removeSuffix("%").toIntOrNull()
        if (num == null) {
            replyHelp()
            return
        }
        val player = players[guild]
        player.volume = num
        reply("Set volume to ${player.volume}%")
    }
}