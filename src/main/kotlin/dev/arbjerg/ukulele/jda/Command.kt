package dev.arbjerg.ukulele.jda

abstract class Command(val name: String, vararg val aliases: String) {

    suspend fun invoke0(ctx: CommandContext) {
        ctx.apply { invoke() }
    }

    abstract suspend fun CommandContext.invoke()

}