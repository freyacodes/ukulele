package dev.arbjerg.ukulele.features

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository

@Table("guild_properties")
data class GuildProperties(
        @Id val guildId: Long,
        var volume: Int = 100
) : Persistable<Long> {
    @Transient var new: Boolean = false
    override fun getId() = guildId
    override fun isNew() = new
}

interface GuildPropertiesRepository : ReactiveCrudRepository<GuildProperties, Long>