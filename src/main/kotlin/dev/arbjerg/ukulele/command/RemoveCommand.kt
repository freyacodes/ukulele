package dev.arbjerg.ukulele.command
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class RemoveCommand : Command("remove", "del", "rem") {
    override suspend fun CommandContext.invoke() {
        val pattern = Pattern.compile("(|-)\\d+")
        val matcher = pattern.matcher(argumentText)

        matcher.lookingAt()
        if (!matcher.matches() || argumentText.isBlank()) {
            replyHelp()
            return
        }

        val start = matcher.start()
        val end = matcher.end()
        val commandText = argumentText.substring(start, end)

        val trackToRemove = commandText.toInt() - 1;

        if (trackToRemove < 0){
            reply("Track must be greater than 1")
            replyHelp()
            return
        }

        if(trackToRemove > player.tracks.size){
            reply("There is no track $trackToRemove")
            replyHelp()
            return
        }

        player.remove(trackToRemove)

        reply("Removed track ${trackToRemove + 1}")


    }

    override fun HelpContext.provideHelp() {
        addUsage("<track pos to remove>")
        addDescription("Removes a track from the queue.")    }
}