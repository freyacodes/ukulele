package dev.arbjerg.ukulele.data

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("replies")
class Replies(
    val list: List<Pair<String, String>> = listOf(
        Pair("<@168194489343672322>", "Who dares summon the devil!"),
        Pair("<@308674959557918732>", "Who dares summon le potato."),
        Pair("<@190624436913831938>", "Who dares summon the kawaii-lord!"),
        Pair("D:", "D:"),
        Pair("uwu", "did someone say uwuᵘʷᵘ oh frick ᵘʷᵘ ᵘʷᵘᵘʷᵘ ᵘʷᵘ ᵘʷᵘ ᵘʷᵘ ᵘʷᵘ ᵘʷᵘ frick sorry guysᵘʷᵘ ᵘʷᵘ ᵘʷᵘ ᵘʷᵘᵘʷᵘ ᵘʷᵘ sorry im dropping ᵘʷᵘ my uwus all over the ᵘʷᵘ place ᵘʷᵘ ᵘʷᵘ ᵘʷᵘ sorry"),
        Pair("nya", "Stop you fucking degenerate"),
        Pair("._.", "Good ol' Albert.")
    )
)