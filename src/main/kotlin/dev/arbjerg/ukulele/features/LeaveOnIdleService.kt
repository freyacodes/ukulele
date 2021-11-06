package dev.arbjerg.ukulele.features

import org.springframework.stereotype.Service
import dev.arbjerg.ukulele.config.BotProps
import net.dv8tion.jda.api.entities.Guild
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class LeaveOnIdleService(private val scheduler: TaskScheduler, private val botProps: BotProps) {
    private val log: Logger = LoggerFactory.getLogger(LeaveOnIdleService::class.java)
    private val timers: ConcurrentHashMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()

    /**
     * Runnable that gets passed to the Spring TaskScheduler. Used to keep track of how many minutes
     * a player in a guild has spent idling.
     */
    class IdleTask(val guild: Guild, private val idleTimeMinutes: Int) : Runnable {
        private var numberOfMinutesIdleElapsed = -1

        override fun run() {
            numberOfMinutesIdleElapsed++

            if (numberOfMinutesIdleElapsed >= idleTimeMinutes) {
                guild.audioManager.closeAudioConnection()
            }
        }
    }

    /**
     * Create a new timer which will start counting from the current moment and cancel any existing timers
     * associated with this guild.
     */
    fun maybeCreateTimer(guild: Guild) {
        // don't do anything if idleTimeMinutes is not set
        if (botProps.idleTimeMinutes <= 0) return

        log.info("Player for guild {} is idle, starting timeout of {} minutes", guild.idLong, botProps.idleTimeMinutes)
        val interval = Duration.ofMinutes(1)
        val task = IdleTask(guild, botProps.idleTimeMinutes)
        val fut = scheduler.scheduleAtFixedRate(task, interval)

        if (timers.containsKey(guild.idLong)) {
            timers.replace(guild.idLong, fut)?.cancel(true)
        } else {
            timers[guild.idLong] = fut
        }
    }

    /**
     * If a timer exists for this guild, cancel it.
     */
    fun maybeDestroyTimer(guild: Guild) {
        // don't do anything if idleTimeMinutes is not set
        if (botProps.idleTimeMinutes <= 0) return

        timers[guild.idLong]?.let {
            log.debug("Cleaning up idle timer for guild {}", guild.idLong)
            it.cancel(true)
            timers.remove(guild.idLong)
        }
    }
}