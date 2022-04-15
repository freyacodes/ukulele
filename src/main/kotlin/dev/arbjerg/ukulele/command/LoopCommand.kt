package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class LoopCommand : Command ("loop", "l") {
    override suspend fun CommandContext.invoke() {
        player.isLooping = !player.isLooping
        if (player.isLooping) {reply("Queue looping is now enabled.")}
        else {reply("Queue looping is now disabled.")}
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Toggles the looping of the queue.")
    }
}
