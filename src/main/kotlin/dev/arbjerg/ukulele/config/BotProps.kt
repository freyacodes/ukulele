package dev.arbjerg.ukulele.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("config")
class BotProps(
        var token: String = "",
        var shards: Int = 1
)