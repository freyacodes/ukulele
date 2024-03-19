package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.audio.Player
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.data.GuildProperties
import dev.arbjerg.ukulele.features.HelpContext
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.springframework.stereotype.Component

class CommandContext(
    val beans: Beans,
    val guildProperties: GuildProperties,
    val guild: Guild,
    val channel: TextChannel,
    val invoker: Member,
    val message: Message,
    val command: Command,
    val prefix: String,
    /** Prefix + command name */
        val trigger: String
) {
    @Component
    class Beans(
            val players: PlayerRegistry,
            val botProps: BotProps
    ) {
        lateinit var commandManager: CommandManager
    }

    val player: Player by lazy { beans.players.get(guild, guildProperties) }

    /** The command argument text after the trigger */
    val argumentText: String by lazy {
        message.contentRaw.drop(trigger.length).trim()
    }
    val selfMember: Member get() = guild.selfMember

    fun reply(msg: String) {
        channel.sendMessage(msg).queue()
    }

    fun replyMsg(msg: MessageCreateData) {
        channel.sendMessage(msg).queue()
    }

    fun replyEmbed(embed: MessageEmbed) {
        channel.sendMessage(MessageCreateData.fromEmbeds(embed)).queue()
    }

    fun replyHelp(forCommand: Command = command) {
        val help = HelpContext(this, forCommand)
        forCommand.provideHelp0(help)
        channel.sendMessage(help.buildMessage()).queue()
    }

    fun handleException(t: Throwable) {
        command.log.error("Handled exception occurred", t)
        reply("An exception occurred!\n`${t.message}`")
    }
}