package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.features.LeaveOnIdleService
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventHandler(private val commandManager: CommandManager, private val leaveOnIdleService: LeaveOnIdleService) : ListenerAdapter() {

    private val log: Logger = LoggerFactory.getLogger(EventHandler::class.java)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.isWebhookMessage || event.author.isBot) return
        commandManager.onMessage(event.guild, event.channel, event.member!!, event.message)
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        log.info("{}: {} -> {}", event.entity.shardInfo, event.oldStatus, event.newStatus)
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        log.info("Joining voice channel {} in guild {}", event.channelJoined, event.guild)
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        log.info("Leaving voice channel {} in guild {}", event.channelLeft, event.guild)
        leaveOnIdleService.destroyTimer(event.guild)
    }
}