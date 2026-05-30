package dev.arbjerg.ukulele.features

import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import net.dv8tion.jda.api.utils.MarkdownUtil
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder

class HelpContext(private val commandContext: CommandContext, private val command: Command) {
    private val lines = mutableListOf<String>()

    fun addUsage(usage: String) = addUsages(usage)

    fun addUsages(vararg usages: String) {
        if (usages.isEmpty()) throw IllegalArgumentException("Expected at least one usage!")
        lines.add(usages.joinToString(" OR ") {
            commandContext.prefix + command.name + " " + it.trim()
        })
    }

    fun addDescription(text: String) {
        lines.add("# " + text.trim())
    }

    fun buildMessage() = MessageCreateBuilder()
        .addContent(MarkdownUtil.codeblock("md", lines.joinToString(separator = "\n")))
        .build()
}