package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources
import dev.arbjerg.ukulele.config.BotProps
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.YoutubeSourceOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LavaplayerConfig {
    @Bean
    fun playerManager(botProps: BotProps): AudioPlayerManager {
        val ytOptions = YoutubeSourceOptions()
        .setAllowSearch(true)
        .setAllowDirectVideoIds(true)
        .setAllowDirectPlaylistIds(true)

        if (!botProps.youtubeRemoteCipherUrl.isNullOrEmpty()) {
            ytOptions.setRemoteCipher(
                botProps.youtubeRemoteCipherUrl,
                botProps.youtubeRemoteCipherPassword,
                botProps.youtubeRemoteCipherUserAgent,
            )
        }

        val ytSourceManager = YoutubeAudioSourceManager(
            ytOptions,
            *YoutubeAudioSourceManager.DEFAULT_CLIENTS
        )

        val apm = DefaultAudioPlayerManager()

        // Add the new YoutubeAudioSourceManager
        apm.registerSourceManager(ytSourceManager)

        // Then add the rest, while excluding the legacy `YoutubeAudioSourceManager`
        @Suppress("DEPRECATION")
        registerRemoteSources(
            apm,
            MediaContainerRegistry.DEFAULT_REGISTRY,
            com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager::class.java
        )

        return apm
    }
}
