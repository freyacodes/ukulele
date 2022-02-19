package dev.arbjerg.ukulele.features

import org.springframework.stereotype.Service
import dev.arbjerg.ukulele.config.BotProps
import net.dv8tion.jda.api.entities.Guild
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class LeaveOnIdleService(private val scheduler: TaskScheduler, private val botProps: BotProps) {
    private val log: Logger = LoggerFactory.getLogger(LeaveOnIdleService::class.java)
    private val timers: ConcurrentHashMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()

    /**
     * Create a new timer which will start counting from the current moment and cancel any existing timers
     * associated with this guild.
     */
    fun onQueueEmpty(guild: Guild) {
        if (botProps.idleTimeMinutes <= 0) return

        log.info("Player for guild {} is idle, starting timeout of {} minutes", guild.idLong, botProps.idleTimeMinutes)
        val instant = Instant.now().plusSeconds(60 * botProps.idleTimeMinutes.toLong())
        val fut = scheduler.schedule({
            guild.audioManager.closeAudioConnection()
        }, instant)

        if (timers.containsKey(guild.idLong)) {
            timers.replace(guild.idLong, fut)?.cancel(true)
        } else {
            timers[guild.idLong] = fut
        }
    }

    fun destroyTimer(guild: Guild) {
        if (botProps.idleTimeMinutes <= 0) return

        timers.remove(guild.idLong)?.let {
            log.debug("Cleaned up idle timer for guild {}", guild.idLong)
            it.cancel(true)
        }
    }
}
