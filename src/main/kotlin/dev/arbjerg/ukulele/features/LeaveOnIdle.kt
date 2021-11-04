package dev.arbjerg.ukulele.features

import dev.arbjerg.ukulele.audio.Player
import org.springframework.stereotype.Service
import dev.arbjerg.ukulele.audio.PlayerRegistry
import dev.arbjerg.ukulele.config.BotProps
import dev.arbjerg.ukulele.data.GuildPropertiesService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import java.time.Duration
import java.util.concurrent.ScheduledFuture

@Service
class LeaveOnIdle(
        private val players: PlayerRegistry,
        private val guildPropertiesService: GuildPropertiesService,
        private val scheduler: TaskScheduler,
        private val botProps: BotProps
) {
    private val log: Logger = LoggerFactory.getLogger(LeaveOnIdle::class.java)
    private val timers: MutableMap<Guild, ScheduledFuture<*>> = HashMap()

    fun onVoiceJoin(guild: Guild, channel: VoiceChannel, member: Member) {
        if (member == guild.selfMember) {
            log.info("Joined voice channel {} in guild {}", channel.name, guild.name)
            createTimer(guild)
        }
    }

    fun onVoiceLeave(guild: Guild, channel: VoiceChannel, member: Member) {
        if (member == guild.selfMember) {
            log.info("Left voice channel {} in guild {}", channel.name, guild.name)
            removeTimer(guild)
        }
    }

    private fun createTimer(guild: Guild) {
        GlobalScope.launch {
            val taskPeriod = Duration.ofMinutes(1)
            val player = players.get(guild, guildPropertiesService.getAwait(guild.idLong))
            val timerTaskFuture = scheduler.scheduleAtFixedRate(IdleTask(guild, player, botProps.idleTimeMinutes), taskPeriod)

            timers[guild] = timerTaskFuture
        }
    }

    private fun removeTimer(guild: Guild) {
        timers[guild]?.let {
            it.cancel(true)
            timers.remove(guild)
        }
    }
}

class IdleTask(val guild: Guild, val player: Player, val idleTimeMinutes: Int) : Runnable {
    private var numberOfMinutesIdleElapsed = 0
    private val log: Logger = LoggerFactory.getLogger(IdleTask::class.java)

    override fun run() {
        if (player.remainingDuration <= 0) {
            // the player is idle, increment our minutes elapsed
            numberOfMinutesIdleElapsed++
        } else {
            // if the player has something in its queue, reset to 0
            numberOfMinutesIdleElapsed = 0
        }

        if (numberOfMinutesIdleElapsed >= idleTimeMinutes) {
            guild.audioManager.closeAudioConnection()
        }
    }
}