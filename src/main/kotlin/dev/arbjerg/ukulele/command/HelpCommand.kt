package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.MessageBuilder
import org.springframework.stereotype.Component

@Component
class HelpCommand : Command("help") {
    override suspend fun CommandContext.invoke() = when {
        argumentText.isNotBlank() -> {
            val command = beans.commandManager[argumentText.trim()]
            if (command == null) {
                replyHelp()
            } else {
                replyHelp(command)
            }
        }
        else -> {
            val commands = beans.commandManager.getCommands()
            val msg = MessageBuilder()
                .append("Available commands:")
                .appendCodeBlock(buildString {
                    commands.forEach {
                        appendLine((listOf(it.name) + it.aliases).joinToString())
                    }
                }, "")
                .append("Use `${trigger} <command>` to see more details.")
            replyMsg(msg.build())
        }
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Displays general help. (unfinished)") // TODO
        addUsage("<command>")
        addDescription("Displays help about a specific command.")
    }
}