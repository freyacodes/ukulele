package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class SayCommand : Command("say") {
    override suspend fun CommandContext.invoke() {
        replyTTS(argumentText)
    }

    override fun HelpContext.provideHelp() {
        addUsage("<text>")
        addDescription("Repeats the given text")
    }
}
