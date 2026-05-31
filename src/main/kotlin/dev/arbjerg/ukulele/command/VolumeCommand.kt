package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

val PERMITTED_VOLUME_RANGE = 0..150

@Component
class VolumeCommand(val players: PlayerRegistry) : Command("volume", "v") {
    override suspend fun CommandContext.invoke() {
        if (argumentText.isBlank()) return reply("The volume is set to ${player.volume}%.")

        val num = argumentText.removeSuffix("%")
                .toIntOrNull()
                ?: return replyHelp()

        val formerVolume = player.volume
        player.volume = num.coerceIn(PERMITTED_VOLUME_RANGE)
        reply("Changed volume from ${formerVolume}% to ${player.volume}%.")
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays the current volume.")
        addUsage("<${PERMITTED_VOLUME_RANGE.first}-${PERMITTED_VOLUME_RANGE.last}>%")
        addDescription("Sets the volume to the given percentage.")
    }
}