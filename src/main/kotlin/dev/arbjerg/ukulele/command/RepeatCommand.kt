package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class RepeatCommand : Command ("repeat", "r", "loop") {
    override suspend fun CommandContext.invoke() {
        player.toggleRepeat()
        if (player.repeatOne) return reply("The current track will be repeated until toggled off.")
        reply("Repeat has been toggled off and the queue will proceed as normal.")
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Toggles repeat on the current track.")
    }
}