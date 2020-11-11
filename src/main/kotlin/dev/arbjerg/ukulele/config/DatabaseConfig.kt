package dev.arbjerg.ukulele.config

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class DatabaseConfig(private val botProps: BotProps, private val env: Environment) {

    @Bean
    fun connectionFactory(): ConnectionFactory = H2ConnectionFactory(H2ConnectionConfiguration.builder()
            .file(botProps.database  + ";DATABASE_TO_UPPER=false")
            .build())

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        return Flyway(Flyway.configure().dataSource(
                "jdbc:h2:" + botProps.database + ";DATABASE_TO_UPPER=false",
                "",
                ""
        ))
    }
}
