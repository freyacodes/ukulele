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
        addDescription("Displays general help.")
        addUsage("<command>")
        addDescription("Displays help about a specific command.")
        addDescription("")
        addDescription("")
        addDescription("Double colon is the command prefix (i.e. ::<command>).")
        addDescription("Here are the currently available commands for Ukulele:")
        addDescription("nowplaying / np")
        addDescription("pause")
        addDescription("play / p")
        addDescription("prefix")
        addDescription("queue / q / list / l")
        addDescription("repeat / r / loop")
        addDescription("resume")
        addDescription("say")
        addDescription("skip / s")
        addDescription("stop")
        addDescription("volume / v")
    }
}