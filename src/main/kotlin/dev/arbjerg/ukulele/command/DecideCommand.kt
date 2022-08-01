package dev.arbjerg.ukulele.command
import dev.arbjerg.ukulele.features.HelpContext
import dev.arbjerg.ukulele.jda.Command
import dev.arbjerg.ukulele.jda.CommandContext
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DecideCommand : Command("decide"){
    private val responseWording = listOf(
            "I think the best option is %s",
            "The worst one would be %s",
            "I don't know... %s looks prettier",
            "How would I know you psycho?! Im just a bot... But maybe %s",
            "The ancient gods of the universe have made me feel unworthy, oh wait you are asking me, maybe %s",
            "Seems like %s would work",
            "Fuck off! I'm busy... ",
            "Fuck off! I don't know, %s or something"
    )
    override suspend fun CommandContext.invoke() {
        val options = argumentText.split(",")

        if (options.isEmpty() || options.size < 2) {
            replyHelp()
            return
        }

        val selectedOption = options[Random.nextInt(options.size - 1)].trim()
        val selectedWording = responseWording[Random.nextInt(responseWording.size - 1)]

        reply(selectedWording.format(selectedOption))
    }

    override fun HelpContext.provideHelp() {
        addUsage("<option>, <option>, <option>...")
        addDescription("Lets the bot decide which option is better, needs at least two options.")
    }
}