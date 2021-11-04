package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.features.LeaveOnIdle
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventHandler(private val commandManager: CommandManager, private val leaveOnIdle: LeaveOnIdle) : ListenerAdapter() {

    private val log: Logger = LoggerFactory.getLogger(EventHandler::class.java)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.isWebhookMessage || event.author.isBot) return
        commandManager.onMessage(event.guild, event.channel, event.member!!, event.message)
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        log.info("{}: {} -> {}", event.entity.shardInfo, event.oldStatus, event.newStatus)
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        leaveOnIdle.onVoiceJoin(event.guild, event.channelJoined, event.member)
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        leaveOnIdle.onVoiceLeave(event.guild, event.channelLeft, event.member)
    }
}