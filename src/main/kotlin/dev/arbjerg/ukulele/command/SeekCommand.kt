package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class SeekCommand : Command ("seek") {

    override suspend fun CommandContext.invoke() {
        val track = player.tracks.firstOrNull() ?: return reply("Not playing anything.")

        if (!track.isSeekable)
            return reply("This track is not seekable")

        if (argumentText.isBlank())
            return replyHelp()

        val newPosition = parseTimeString(argumentText) ?: return replyHelp()
        if (newPosition > track.info.length) {
            player.skip(0..0)
            reply("Skipped `${track.info.title}`")
        } else {
            player.seek(newPosition)
            reply("Seeking `${track.info.title}` to ${formatTime(newPosition)}")
        }
    }

    private val timestampPattern: Pattern = Pattern.compile("^(\\d?\\d)(?::([0-5]?\\d))?(?::([0-5]?\\d))?$")

    fun parseTimeString(str: String): Long? {
        var millis: Long = 0
        var seconds: Long = 0
        var minutes: Long = 0
        var hours: Long = 0
        val m = timestampPattern.matcher(str)
        if(!m.find()) return null

        var capturedGroups = 0
        if (m.group(1) != null) capturedGroups++
        if (m.group(2) != null) capturedGroups++
        if (m.group(3) != null) capturedGroups++
        when (capturedGroups) {
            0 -> return null
            1 -> seconds = m.group(1).toLongOrNull() ?: 0
            2 -> {
                minutes = m.group(1).toLongOrNull() ?: 0
                seconds = m.group(2).toLongOrNull() ?: 0
            }
            3 -> {
                hours = m.group(1).toLongOrNull() ?: 0
                minutes = m.group(2).toLongOrNull() ?: 0
                seconds = m.group(3).toLongOrNull() ?: 0
            }
        }
        minutes += hours * 60
        seconds += minutes * 60
        millis = seconds * 1000
        return millis
    }

    fun formatTime(millis: Long): String? {
        if (millis == Long.MAX_VALUE) {
            return "LIVE"
        }
        val t = millis / 1000L
        val sec = (t % 60L).toInt()
        val min = (t % 3600L / 60L).toInt()
        val hrs = (t / 3600L).toInt()
        val timestamp: String = if (hrs != 0) {
            forceTwoDigits(hrs).toString() + ":" + forceTwoDigits(min) + ":" + forceTwoDigits(sec)
        } else {
            forceTwoDigits(min).toString() + ":" + forceTwoDigits(sec)
        }
        return timestamp
    }

    private fun forceTwoDigits(i: Int): String? {
        return if (i < 10) "0$i" else i.toString()
    }

    override fun HelpContext.provideHelp() {
        addUsage("[[hh:]mm:]ss")
        addDescription("Seeks the current track to the selected position")
    }
}