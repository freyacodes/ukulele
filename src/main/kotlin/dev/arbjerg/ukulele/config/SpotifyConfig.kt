package dev.arbjerg.ukulele.config

import com.adamratzman.spotify.SpotifyAppApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.adamratzman.spotify.spotifyAppApi
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Configuration
class SpotifyConfig {
    private val LOGGER = LoggerFactory.getLogger(SpotifyConfig::class.java);

    @Bean
    @ConditionalOnProperty(prefix = "config", value = ["spotifyClientId", "spotifyClientSecret"])
    fun spotifyAppApiBean(props: BotProps): SpotifyAppApi {
        return runBlocking { spotifyAppApi(props.spotifyClientId!!, props.spotifyClientSecret!!).build().also { LOGGER.info("Successfully loaded Spotify API") } }
    }
}