package com.example.paymentecho.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI/Swagger configuration for API documentation.
 * This metadata shows up in Swagger UI and the generated OpenAPI spec.
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun paymentEchoOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Payment Echo API")
                    .description("Production-grade Payment Echo microservice built with Kotlin & Spring Boot 4.")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Payment Echo Team")
                            .email("support@paymentecho.example")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0")
                    )
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Project Documentation")
                    .url("https://docs.paymentecho.example")
            )
}
