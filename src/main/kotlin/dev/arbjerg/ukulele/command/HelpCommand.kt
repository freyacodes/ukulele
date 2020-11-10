package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class HelpCommand : Command("help") {
    override suspend fun CommandContext.invoke() {
        val command = beans.commandManager[argumentText.trim()] ?: return replyHelp()
        replyHelp(command)
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays general help. (unfinished)") // TODO
        addUsage("<command>")
        addDescription("Displays help about a specific command.")
    }
}