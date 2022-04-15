package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component

@Component
class RepeatCommand : Command ("repeat", "r") {
   override suspend fun CommandContext.invoke() {
       if (argumentText.isBlank()) {
           player.toggleRepeatTrack()
       } else {
           player.repeatTrack = argumentText.toBoolean()
       }
       
       if (player.repeatTrack) return reply("The current track will be repeated until repeat is turned off or the track is skipped.")
       reply("Repeat has been turned off and the queue will proceed as normal.")
   }

   override fun HelpContext.provideHelp() {
       addUsage("")
       addDescription("Toggles the repeat track setting for the player.")
       addUsage("false (or any non true value)")
       addDescription("Turns repeat track off.")
       addUsage("true")
       addDescription("Turns repeat track on.")
   }
}
