package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import dev.arbjerg.ukulele.jda.PrivateMessageContext


@Component
class VolumeCommand(val players: PlayerRegistry) : Command("volume", "v") {
    override suspend fun CommandContext.invoke() {
        if (argumentText.isBlank()) return reply("The volume is set to ${player.volume}%.")

        val num = argumentText.removeSuffix("%")
                .toIntOrNull()
                ?: return replyHelp()

        val formerVolume = player.volume
        player.volume = num
        reply("Changed volume from ${formerVolume}% to ${player.volume}%.")
    }

    override suspend fun PrivateMessageContext.invoke() {
        reply("this command in PM isn't supported yet")
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays the current volume.")
        addUsage("<0-150>%")
        addDescription("Sets the volume to the given percentage.")
    }
}