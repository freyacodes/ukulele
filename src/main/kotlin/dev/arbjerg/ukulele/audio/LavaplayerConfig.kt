package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LavaplayerConfig {

    @Bean
    fun playerManager(): AudioPlayerManager {
        val apm = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(apm) // Here's where the YouTube config gets loaded in with null user/pass.
        AudioSourceManagers.registerLocalSource(apm)
        return apm
    }

}