package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.MessageBuilder
import org.springframework.stereotype.Component

@Component
class HelpCommand : Command("help") {
    override suspend fun CommandContext.invoke() {
        if (argumentText.isNotBlank()) {
            replyHelp(beans.commandManager[argumentText.trim()] ?: command)
        } else {
            val msg = MessageBuilder()
                .append("Available commands:")
                .appendCodeBlock(buildString {
                    beans.commandManager.getCommands().forEach {
                        appendLine((listOf(it.name) + it.aliases).joinToString())
                    }
                }, "")
                .append("\nUse \"${trigger} <command>\" to see more details.")
            replyMsg(msg.build())
        }
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays a list of commands and aliases.")
        addUsage("<command>")
        addDescription("Displays help about a specific command.")
    }
}
