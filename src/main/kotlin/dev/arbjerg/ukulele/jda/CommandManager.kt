package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.data.GuildPropertiesService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommandManager(
        private val contextBeans: CommandContext.Beans,
        private val guildProperties: GuildPropertiesService,
        private val botProps: BotProps,
        commands: Collection<Command>
) {

    private final val registry: Map<String, Command>
    private val log: Logger = LoggerFactory.getLogger(CommandManager::class.java)

    init {
        val map = mutableMapOf<String, Command>()
        commands.forEach { c ->
            map[c.name] = c
            c.aliases.forEach { map[it] = c }
        }
        registry = map
        log.info("Registered ${commands.size} commands with ${registry.size} names")
        @Suppress("LeakingThis")
        contextBeans.commandManager = this
    }

    operator fun get(commandName: String) = registry[commandName]

    fun getCommands() = registry.values.distinct()

    fun onMessage(guild: Guild, channel: TextChannel, member: Member, message: Message) {
        GlobalScope.launch {
            // TODO: Allow mentions
            val guildProperties = guildProperties.getAwait(guild.idLong)
            val prefix = guildProperties.prefix ?: botProps.prefix
            if (!message.contentRaw.startsWith(prefix)) return@launch

            val name = message.contentRaw.drop(prefix.length)
                    .takeWhile { !it.isWhitespace() }

            val command = registry[name] ?: return@launch
            val trigger = prefix + name
            val ctx = CommandContext(contextBeans, guildProperties, guild, channel, member, message, command, prefix, trigger)

            log.info("Invocation: ${message.contentRaw}")
            command.invoke0(ctx)
        }
    }

}