package dev.arbjerg.ukulele.config

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidatorConfig {
    @Bean
    fun urlValidator(): UrlValidator {
        return UrlValidator(arrayOf("https"))
    }
}