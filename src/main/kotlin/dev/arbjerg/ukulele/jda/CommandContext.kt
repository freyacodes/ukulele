package dev.arbjerg.ukulele.jda

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

class CommandContext(val guild: Guild, val channel: TextChannel, val member: Member, val message: Message, val command: Command) {
    fun reply(msg: String) {
        channel.sendMessage(msg).queue()
    }
}