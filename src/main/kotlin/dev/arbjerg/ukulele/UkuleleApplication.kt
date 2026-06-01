package dev.arbjerg.ukulele

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
class UkuleleApplication

fun main(args: Array<String>) {
    System.setProperty("spring.config.name", "ukulele")
    System.setProperty("spring.config.title", "ukulele")
    runApplication<UkuleleApplication>(*args)
}
