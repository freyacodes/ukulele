package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.data.GuildProperties
import dev.arbjerg.ukulele.features.HelpContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.entities.User

class PrivateMessageContext(
    val beans: CommandContext.Beans,
    val author: User,
    val message: Message,
    val command: Command,
    val prefix: String,
    /** Prefix + command name */
    val trigger: String
) {

    @Component
    class Beans(
            val botProps: BotProps
    ) {
        lateinit var commandManager: CommandManager
    }

    val argumentText: String by lazy {
        message.contentRaw.drop(trigger.length).trim()
    }

    fun reply(msg: String) {
        author.openPrivateChannel().queue { it.sendMessage(msg).queue() }
    }

    fun replyEmbed(embed: MessageEmbed) {
        author.openPrivateChannel().queue { it.sendMessage(embed).queue() }
    }

    fun handleException(t: Throwable) {
        command.log.error("Handled exception occurred", t)
        reply("An exception occurred!\n`${t.message}`")
    }
}