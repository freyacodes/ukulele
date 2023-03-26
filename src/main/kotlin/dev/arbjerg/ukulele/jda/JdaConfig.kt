package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.config.BotProps
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent.*
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.messages.MessageRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.security.auth.login.LoginException
import kotlin.concurrent.thread

@Configuration
class JdaConfig {

    init {
        MessageRequest.setDefaultMentions(emptyList())
    }

    @Bean
    fun shardManager(botProps: BotProps, eventHandler: EventHandler): ShardManager {
        if (botProps.token.isBlank()) throw RuntimeException("Discord token not configured!")
        val activity = if (botProps.game.isBlank()) Activity.playing("music") else Activity.playing(botProps.game)


        val intents = listOf(
                DIRECT_MESSAGES,
                GUILD_VOICE_STATES,
                GUILD_PRESENCES,
                GUILD_MESSAGES,
                GUILD_MODERATION,
                MESSAGE_CONTENT
        )

        val builder = DefaultShardManagerBuilder.create(botProps.token, intents)
                .disableCache(CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS)
                .setBulkDeleteSplittingEnabled(false)
                .setEnableShutdownHook(false)
                .setAutoReconnect(true)
                .setShardsTotal(botProps.shards)
                .addEventListeners(eventHandler)
                .setActivity(activity)

        val shardManager: ShardManager
        try {
            shardManager = builder.build()
        } catch (e: LoginException) {
            throw RuntimeException("Failed to log in to Discord! Is your token invalid?", e)
        }

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            shardManager.guildCache.forEach {
                if (it.audioManager.isConnected) it.audioManager.closeAudioConnection()
            }
        })

        return shardManager
    }
}