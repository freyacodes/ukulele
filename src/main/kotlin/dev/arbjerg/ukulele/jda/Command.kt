package dev.arbjerg.ukulele.jda

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Command(val name: String, vararg val aliases: String) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    suspend fun invoke0(ctx: CommandContext) {
        ctx.apply { invoke() }
    }

    abstract suspend fun CommandContext.invoke()

}