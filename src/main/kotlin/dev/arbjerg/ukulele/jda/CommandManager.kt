package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.config.BotProps
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
class CommandManager(private val contextBeans: CommandContext.Beans, private val botProps: BotProps, commands: Collection<Command>) {

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

    fun onMessage(guild: Guild, channel: TextChannel, member: Member, message: Message) {
        // TODO: Allow mentions
        if (!message.contentRaw.startsWith(botProps.prefix)) return

        val name = message.contentRaw.drop(botProps.prefix.length)
                .takeWhile { !it.isWhitespace() }

        val command = registry[name] ?: return
        val trigger = botProps.prefix + name
        val ctx = CommandContext(contextBeans, guild, channel, member, message, command, trigger)

        log.info("Invocation: ${message.contentRaw}")

        GlobalScope.launch { command.invoke0(ctx) }
    }

}