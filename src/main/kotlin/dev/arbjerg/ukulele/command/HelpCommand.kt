package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.utils.MarkdownUtil
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.springframework.stereotype.Component

@Component
class HelpCommand : Command("help") {
    override suspend fun CommandContext.invoke() {
        if (argumentText.isNotBlank()) {
            replyHelp(beans.commandManager[argumentText.trim()] ?: command)
        } else {
            val msg = MessageCreateBuilder()
                .addContent("Available commands:")
                .addContent(MarkdownUtil.codeblock(buildString {
                    beans.commandManager.getCommands().forEach {
                        appendLine((listOf(it.name) + it.aliases).joinToString())
                    }
                }))
                .addContent("\nUse \"${trigger} <command>\" to see more details.")

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
