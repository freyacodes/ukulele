package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import dev.arbjerg.ukulele.jda.PrivateMessageContext


@Component
class SayCommand : Command("say") {
    override suspend fun CommandContext.invoke() {
        reply(argumentText)
    }

    override suspend fun PrivateMessageContext.invoke() {
        reply("this command in PM isn't supported yet")
    }

    override fun HelpContext.provideHelp() {
        addUsage("<text>")
        addDescription("Repeats the given text")
    }
}
