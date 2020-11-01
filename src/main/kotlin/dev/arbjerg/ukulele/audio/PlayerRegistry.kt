package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Service

@Service
class PlayerRegistry(val apm: AudioPlayerManager) {

    private val players = mutableMapOf<Long, Player>()

    operator fun get(guild: Guild) = players.computeIfAbsent(guild.idLong) { Player(apm) }

}