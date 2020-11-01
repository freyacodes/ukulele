package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.config.BotProps
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.dv8tion.jda.api.requests.GatewayIntent.*
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.cache.CacheFlag
import javax.security.auth.login.LoginException

@Configuration
class JdaConfig {
    @Bean
    fun shardManager(botProps: BotProps, eventHandler: EventHandler): ShardManager {
        if (botProps.token.isBlank()) throw RuntimeException("Discord token not configured!")

        val intents = listOf(
                GUILD_VOICE_STATES,
                GUILD_MESSAGES,
                GUILD_BANS,
                DIRECT_MESSAGES
        )

        val builder = DefaultShardManagerBuilder.create(botProps.token, intents)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                .setBulkDeleteSplittingEnabled(false)
                .setEnableShutdownHook(false)
                .setAutoReconnect(true)
                .setShardsTotal(botProps.shards)
                .addEventListeners(eventHandler)

        val shardManager: ShardManager
        try {
            shardManager = builder.build()
        } catch (e: LoginException) {
            throw RuntimeException("Failed to log in to Discord! Is your token invalid?", e)
        }

        return shardManager
    }
}