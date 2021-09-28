package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class LoopCommand : Command ("loop", "l") {
    override suspend fun CommandContext.invoke() {
        player.loop()
        if (player.isLooping) {reply("Looping is now enabled.")}
        else{reply("Looping is now disabled.")}
    }

    override fun HelpContext.provideHelp() {
        addUsage("")
        addDescription("Toggles the playback looping.")
    }
}
