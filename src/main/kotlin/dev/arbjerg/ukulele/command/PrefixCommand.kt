package dev.arbjerg.ukulele.command

import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.data.GuildPropertiesService
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import dev.arbjerg.ukulele.jda.PrivateMessageContext


@Component
class PrefixCommand(val guildPropertiesService: GuildPropertiesService, val botProps: BotProps) : Command("prefix") {
    override suspend fun CommandContext.invoke() = when {
        argumentText == "reset" -> {
            guildPropertiesService.transformAwait(guild.idLong) { it.prefix = null }
            reply("Reset prefix to `${botProps.prefix}`")
        }
        argumentText.isNotBlank() -> {
            val props = guildPropertiesService.transformAwait(guild.idLong) { it.prefix = argumentText }
            reply("Set prefix to `${props.prefix}`")
        }
        else -> {
            replyHelp()
        }
    }

    override suspend fun PrivateMessageContext.invoke() {
        reply("this command in PM isn't supported yet")
    }

    override fun HelpContext.provideHelp() {
        addUsage("<prefix>")
        addDescription("Set command prefix to <prefix>")
        addUsage("reset")
        addDescription("Resets the prefix to the default " + botProps.prefix)
    }
}