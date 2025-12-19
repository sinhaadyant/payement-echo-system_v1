package com.example.paymentecho.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

/**
 * Basic i18n configuration:
 * - MessageSource uses messages.properties on the classpath
 * - AcceptHeaderLocaleResolver uses the request Accept-Language header
 */
@Configuration
class I18nConfig {

    @Bean
    fun messageSource(): MessageSource {
        val ms = ReloadableResourceBundleMessageSource()
        ms.setBasename("classpath:messages")
        ms.setDefaultEncoding("UTF-8")
        return ms
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val lr = AcceptHeaderLocaleResolver()
        lr.setDefaultLocale(Locale.ENGLISH)
        return lr
    }
}
