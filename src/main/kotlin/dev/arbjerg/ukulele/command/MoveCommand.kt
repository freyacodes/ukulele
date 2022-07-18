package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import java.util.regex.Pattern
import org.springframework.stereotype.Component

@Component
class MoveCommand : Command("move", "m") {
    override suspend fun CommandContext.invoke() {
        val pattern = Pattern.compile("(|-)\\d+ (|-)\\d+")
        val matcher = pattern.matcher(argumentText)

        matcher.lookingAt()
        if (!matcher.matches() || argumentText.isBlank()) {
            replyHelp()
            return
        }

        val start = matcher.start()
        val end = matcher.end()
        val commandText = argumentText.substring(start, end)

        val arguments = commandText.split(" ")
        val originalPos = arguments[0].toInt() - 1
        val targetPos = arguments[1].toInt() - 1

        if (originalPos < 0 || targetPos < 0 || targetPos == originalPos){
            reply("Original pos must be different than target pos, and they both must be greater than or equal to 1")
            replyHelp()
            return
        }

        if(originalPos > player.tracks.size){
            reply("There is no track $originalPos")
            replyHelp()
            return
        }

        if(targetPos > player.tracks.size){
            reply("There is no track $targetPos")
            replyHelp()
            return
        }

        player.move(originalPos, targetPos)

        reply("Moved track ${originalPos + 1} to ${targetPos + 1}")
    }


    override fun HelpContext.provideHelp() {
        addUsage("<original pos> <target pos>")
        addDescription("Move a track from one position in the queue to another")

    }
}