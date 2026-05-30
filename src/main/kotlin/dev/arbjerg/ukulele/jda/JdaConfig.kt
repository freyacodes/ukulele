package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.config.BotProps
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.dv8tion.jda.api.requests.GatewayIntent.*
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.messages.MessageRequest
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
            GUILD_VOICE_STATES,
            GUILD_MESSAGES,
            GUILD_BANS,
            DIRECT_MESSAGES,
            MESSAGE_CONTENT
        )

        val builder = DefaultShardManagerBuilder.create(botProps.token, intents)
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS)
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