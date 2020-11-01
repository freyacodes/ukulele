package dev.arbjerg.ukulele.jda

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

class CommandContext(
        val guild: Guild,
        val channel: TextChannel,
        val invoker: Member,
        val message: Message,
        val command: Command,
        /** Prefix + command name */
        val trigger: String
) {
    /** The command argument text after the trigger */
    val argumentText: String by lazy {
        message.contentRaw.drop(trigger.length).trim()
    }
    val selfMember: Member get() = guild.selfMember

    fun reply(msg: String) {
        channel.sendMessage(msg).queue()
    }

    fun handleException(t: Throwable) {
        command.log.error("Handled exception occurred", t)
        reply("An exception occurred!\n`${t.message}`")
    }
}