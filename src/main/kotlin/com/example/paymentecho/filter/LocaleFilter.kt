package com.example.paymentecho.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver
import java.util.*

/**
 * Filter that intercepts all HTTP requests to resolve and set the locale based on the Accept-Language header.
 * 
 * This filter runs early (Order 0) to ensure locale is available throughout the request lifecycle.
 * It uses the configured LocaleResolver to resolve the locale from the Accept-Language header
 * and stores it in LocaleContextHolder for use by exception handlers and other components.
 * 
 * Supported languages: hi (Hindi - default), en, es, fr, de, bn, ta, te, kn, ru, zh
 * 
 * @param localeResolver The locale resolver configured in MessageSourceConfig
 */
@Component
@Order(0) // Run before other filters to set locale early
class LocaleFilter(
    private val localeResolver: LocaleResolver
) : Filter {

    private val logger = LoggerFactory.getLogger(LocaleFilter::class.java)

    /**
     * Intercepts the request, resolves locale from Accept-Language header, and sets it in LocaleContextHolder.
     * 
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filter chain
     */
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val acceptLanguage = httpRequest.getHeader("Accept-Language")
        val locale = localeResolver.resolveLocale(httpRequest)
        
        logger.debug("LocaleFilter: Accept-Language header: {}, Resolved locale: {}", acceptLanguage, locale)
        LocaleContextHolder.setLocale(locale)
        
        try {
            chain.doFilter(request, response)
        } finally {
            LocaleContextHolder.resetLocaleContext()
        }
    }
}

