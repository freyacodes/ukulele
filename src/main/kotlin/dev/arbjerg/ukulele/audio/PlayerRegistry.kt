package dev.arbjerg.ukulele.audio

import dev.arbjerg.ukulele.data.GuildProperties
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Service

@Service
class PlayerRegistry(val playerBeans: Player.Beans) {

    private val players = mutableMapOf<Long, Player>()

    fun get(guild: Guild, guildProperties: GuildProperties) = players.computeIfAbsent(guild.idLong) { Player(playerBeans, guildProperties) }

}