package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class PlayCommand : Command("play", "p") {
    override suspend fun CommandContext.invoke() {
        reply("Test!")
    }
}
